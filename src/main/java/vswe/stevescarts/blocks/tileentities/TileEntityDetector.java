package vswe.stevescarts.blocks.tileentities;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vswe.stevescarts.containers.ContainerBase;
import vswe.stevescarts.containers.ContainerDetector;
import vswe.stevescarts.entitys.EntityMinecartModular;
import vswe.stevescarts.guis.GuiBase;
import vswe.stevescarts.guis.GuiDetector;
import vswe.stevescarts.helpers.DetectorType;
import vswe.stevescarts.helpers.LogicObject;

public class TileEntityDetector extends TileEntityBase {
	public LogicObject mainObj;
	private int activeTimer;
	private short oldData;
	private boolean hasOldData;

	@SideOnly(Side.CLIENT)
	@Override
	public GuiBase getGui(final InventoryPlayer inv) {
		return new GuiDetector(inv, this);
	}

	@Override
	public ContainerBase getContainer(final InventoryPlayer inv) {
		return new ContainerDetector(inv, this);
	}

	public TileEntityDetector() {
		this.activeTimer = 20;
		this.mainObj = new LogicObject((byte) 1, (byte) 0);
	}

	@Override
	public void readFromNBT(final NBTTagCompound nbttagcompound) {
		super.readFromNBT(nbttagcompound);
		final byte count = nbttagcompound.getByte("LogicObjectCount");
		for (int i = 0; i < count; ++i) {
			this.loadLogicObjectFromInteger(nbttagcompound.getInteger("LogicObject" + i));
		}
	}

	@Override
	public NBTTagCompound writeToNBT(final NBTTagCompound nbttagcompound) {
		super.writeToNBT(nbttagcompound);
		final int count = this.saveLogicObject(nbttagcompound, this.mainObj, 0, false);
		nbttagcompound.setByte("LogicObjectCount", (byte) count);
		return nbttagcompound;
	}

	private int saveLogicObject(final NBTTagCompound nbttagcompound, final LogicObject obj, int id, final boolean saveMe) {
		if (saveMe) {
			nbttagcompound.setInteger("LogicObject" + id++, this.saveLogicObjectToInteger(obj));
		}
		for (final LogicObject child : obj.getChilds()) {
			id = this.saveLogicObject(nbttagcompound, child, id, true);
		}
		return id;
	}

	private int saveLogicObjectToInteger(final LogicObject obj) {
		int returnVal = 0;
		returnVal |= obj.getId() << 24;
		returnVal |= obj.getParent().getId() << 16;
		returnVal |= obj.getExtra() << 8;
		returnVal |= obj.getData() << 0;
		return returnVal;
	}

	private void loadLogicObjectFromInteger(final int val) {
		final byte id = (byte) (val >> 24 & 0xFF);
		final byte parent = (byte) (val >> 16 & 0xFF);
		final byte extra = (byte) (val >> 8 & 0xFF);
		final byte data = (byte) (val >> 0 & 0xFF);
		this.createObject(id, parent, extra, data);
	}

	@Override
	public void updateEntity() {
		if (this.activeTimer > 0 && --this.activeTimer == 0) {
			IBlockState blockState = world.getBlockState(pos);
			Block block = blockState.getBlock();
			DetectorType.getTypeFromSate(blockState).deactivate(this);
			this.world.setBlockState(pos, block.getStateFromMeta(block.getMetaFromState(blockState) & 0xFFFFFFF7), 3);
		}
	}

	@Override
	public void receivePacket(final int id, final byte[] data, final EntityPlayer player) {
		if (id == 0) {
			byte lowestId = -1;
			for (int i = 0; i < 128; ++i) {
				if (!this.isIdOccupied(this.mainObj, i)) {
					lowestId = (byte) i;
					break;
				}
			}
			if (lowestId == -1) {
				return;
			}
			this.createObject(lowestId, data[0], data[1], data[2]);
		} else if (id == 1) {
			this.removeObject(this.mainObj, data[0]);
		}
	}

	private void createObject(final byte id, final byte parentId, final byte extra, final byte data) {
		final LogicObject newObject = new LogicObject(id, extra, data);
		final LogicObject parent = this.getObjectFromId(this.mainObj, parentId);
		if (parent != null) {
			newObject.setParent(parent);
		}
	}

	private LogicObject getObjectFromId(final LogicObject object, final int id) {
		if (object.getId() == id) {
			return object;
		}
		for (final LogicObject child : object.getChilds()) {
			final LogicObject result = this.getObjectFromId(child, id);
			if (result != null) {
				return result;
			}
		}
		return null;
	}

	private boolean removeObject(final LogicObject object, final int idToRemove) {
		if (object.getId() == idToRemove) {
			object.setParent(null);
			return true;
		}
		for (final LogicObject child : object.getChilds()) {
			if (this.removeObject(child, idToRemove)) {
				return true;
			}
		}
		return false;
	}

	private boolean isIdOccupied(final LogicObject object, final int id) {
		if (object.getId() == id) {
			return true;
		}
		for (final LogicObject child : object.getChilds()) {
			if (this.isIdOccupied(child, id)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void initGuiData(final Container con, final IContainerListener crafting) {
	}

	@Override
	public void checkGuiData(final Container con, final IContainerListener crafting) {
		this.sendUpdatedLogicObjects(con, crafting, this.mainObj, ((ContainerDetector) con).mainObj);
	}

	private void sendUpdatedLogicObjects(final Container con, final IContainerListener crafting, final LogicObject real, LogicObject cache) {
		if (!real.equals(cache)) {
			final LogicObject parent = cache.getParent();
			cache.setParent(null);
			final LogicObject clone = real.copy(parent);
			this.removeLogicObject(con, crafting, cache);
			this.sendLogicObject(con, crafting, clone);
			cache = clone;
		}
		while (real.getChilds().size() > cache.getChilds().size()) {
			final int i = cache.getChilds().size();
			final LogicObject clone = real.getChilds().get(i).copy(cache);
			this.sendLogicObject(con, crafting, clone);
		}
		while (real.getChilds().size() < cache.getChilds().size()) {
			final int i = real.getChilds().size();
			final LogicObject toBeRemoved = cache.getChilds().get(i);
			toBeRemoved.setParent(null);
			this.removeLogicObject(con, crafting, toBeRemoved);
		}
		for (int i = 0; i < real.getChilds().size(); ++i) {
			this.sendUpdatedLogicObjects(con, crafting, real.getChilds().get(i), cache.getChilds().get(i));
		}
	}

	private void sendAllLogicObjects(final Container con, final IContainerListener crafting, final LogicObject obj) {
		this.sendLogicObject(con, crafting, obj);
		for (final LogicObject child : obj.getChilds()) {
			this.sendAllLogicObjects(con, crafting, child);
		}
	}

	private void sendLogicObject(final Container con, final IContainerListener crafting, final LogicObject obj) {
		if (obj.getParent() == null) {
			return;
		}
		final short data = (short) (obj.getId() << 8 | obj.getParent().getId());
		final short data2 = (short) (obj.getExtra() << 8 | obj.getData());
		this.updateGuiData(con, crafting, 0, data);
		this.updateGuiData(con, crafting, 1, data2);
	}

	private void removeLogicObject(final Container con, final IContainerListener crafting, final LogicObject obj) {
		this.updateGuiData(con, crafting, 2, obj.getId());
	}

	@Override
	public void receiveGuiData(final int id, final short data) {
		if (id == 0) {
			this.oldData = data;
			this.hasOldData = true;
		} else if (id == 1) {
			if (!this.hasOldData) {
				System.out.println("Doesn't have the other part of the data");
				return;
			}
			final byte logicid = (byte) ((this.oldData & 0xFF00) >> 8);
			final byte parent = (byte) (this.oldData & 0xFF);
			final byte extra = (byte) ((data & 0xFF00) >> 8);
			final byte logicdata = (byte) (data & 0xFF);
			this.createObject(logicid, parent, extra, logicdata);
			this.recalculateTree();
			this.hasOldData = false;
		} else if (id == 2) {
			this.removeObject(this.mainObj, data);
			this.recalculateTree();
		}
	}

	public void recalculateTree() {
		this.mainObj.generatePosition(5, 60, 245, 0);
	}

	public boolean evaluate(final EntityMinecartModular cart, final int depth) {
		return this.mainObj.evaluateLogicTree(this, cart, depth);
	}

	public void handleCart(final EntityMinecartModular cart) {
		final boolean truthValue = this.evaluate(cart, 0);
		IBlockState blockState = world.getBlockState(pos);
		int meta = blockState.getBlock().getMetaFromState(blockState);
		final boolean isOn = (meta & 0x8) != 0x0;
		if (truthValue != isOn) {
			if (truthValue) {
				DetectorType.getTypeFromSate(blockState).activate(this, cart);
				meta |= 0x8;
			} else {
				DetectorType.getTypeFromSate(blockState).deactivate(this);
				meta &= 0xFFFFFFF7;
			}
			this.world.setBlockState(pos, blockState.getBlock().getStateFromMeta(meta), 3);
		}
		if (truthValue) {
			this.activeTimer = 20;
		}
	}

	@Override
	public boolean isUsableByPlayer(final EntityPlayer entityplayer) {
		return this.world.getTileEntity(this.pos) == this && entityplayer.getDistanceSqToCenter(pos) <= 64.0;
	}
}
