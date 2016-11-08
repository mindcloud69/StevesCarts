package vswe.stevescarts.blocks.tileentities;

import java.util.ArrayList;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vswe.stevescarts.PacketHandler;
import vswe.stevescarts.containers.ContainerBase;
import vswe.stevescarts.containers.ContainerDistributor;
import vswe.stevescarts.guis.GuiBase;
import vswe.stevescarts.guis.GuiDistributor;
import vswe.stevescarts.helpers.DistributorSetting;
import vswe.stevescarts.helpers.DistributorSide;
import vswe.stevescarts.helpers.Localization;
import vswe.stevescarts.helpers.Tank;

public class TileEntityDistributor extends TileEntityBase implements IInventory, ISidedInventory, IFluidHandler {
	private ArrayList<DistributorSide> sides;
	private boolean dirty;
	private boolean dirty2;
	private TileEntityManager[] inventories;
	public boolean hasTop;
	public boolean hasBot;

	@SideOnly(Side.CLIENT)
	@Override
	public GuiBase getGui(final InventoryPlayer inv) {
		return new GuiDistributor(inv, this);
	}

	@Override
	public ContainerBase getContainer(final InventoryPlayer inv) {
		return new ContainerDistributor(inv, this);
	}

	public ArrayList<DistributorSide> getSides() {
		return this.sides;
	}

	public TileEntityDistributor() {
		this.dirty = true;
		this.dirty2 = true;
		(this.sides = new ArrayList<DistributorSide>()).add(new DistributorSide(0, Localization.GUI.DISTRIBUTOR.SIDE_ORANGE, EnumFacing.UP));
		this.sides.add(new DistributorSide(1, Localization.GUI.DISTRIBUTOR.SIDE_PURPLE, EnumFacing.DOWN));
		this.sides.add(new DistributorSide(2, Localization.GUI.DISTRIBUTOR.SIDE_YELLOW, EnumFacing.NORTH));
		this.sides.add(new DistributorSide(3, Localization.GUI.DISTRIBUTOR.SIDE_GREEN, EnumFacing.WEST));
		this.sides.add(new DistributorSide(4, Localization.GUI.DISTRIBUTOR.SIDE_BLUE, EnumFacing.SOUTH));
		this.sides.add(new DistributorSide(5, Localization.GUI.DISTRIBUTOR.SIDE_RED, EnumFacing.EAST));
	}

	@Override
	public void readFromNBT(final NBTTagCompound nbttagcompound) {
		super.readFromNBT(nbttagcompound);
		for (final DistributorSide side : this.getSides()) {
			side.setData(nbttagcompound.getInteger("Side" + side.getId()));
		}
	}

	@Override
	public NBTTagCompound writeToNBT(final NBTTagCompound nbttagcompound) {
		super.writeToNBT(nbttagcompound);
		for (final DistributorSide side : this.getSides()) {
			nbttagcompound.setInteger("Side" + side.getId(), side.getData());
		}
		return nbttagcompound;
	}

	public void updateEntity() {
		this.dirty = true;
		this.dirty2 = true;
	}

	protected void sendPacket(final int id) {
		this.sendPacket(id, new byte[0]);
	}

	protected void sendPacket(final int id, final byte data) {
		this.sendPacket(id, new byte[] { data });
	}

	public void sendPacket(final int id, final byte[] data) {
		PacketHandler.sendPacket(id, data);
	}

	@Override
	public void receivePacket(final int id, final byte[] data, final EntityPlayer player) {
		if (id == 0 || id == 1) {
			final byte settingId = data[0];
			final byte sideId = data[1];
			if (settingId >= 0 && settingId < DistributorSetting.settings.size() && sideId >= 0 && sideId < this.getSides().size()) {
				if (id == 0) {
					this.getSides().get(sideId).set(settingId);
				} else {
					this.getSides().get(sideId).reset(settingId);
				}
			}
		}
	}

	@Override
	public void initGuiData(final Container con, final IContainerListener crafting) {
	}

	@Override
	public void checkGuiData(final Container con, final IContainerListener crafting) {
		final ContainerDistributor condist = (ContainerDistributor) con;
		for (int i = 0; i < this.getSides().size(); ++i) {
			final DistributorSide side = this.getSides().get(i);
			if (side.getLowShortData() != condist.cachedValues.get(i * 2)) {
				this.updateGuiData(con, crafting, i * 2, side.getLowShortData());
				condist.cachedValues.set(i * 2, side.getLowShortData());
			}
			if (side.getHighShortData() != condist.cachedValues.get(i * 2 + 1)) {
				this.updateGuiData(con, crafting, i * 2 + 1, side.getHighShortData());
				condist.cachedValues.set(i * 2 + 1, side.getHighShortData());
			}
		}
	}

	@Override
	public void receiveGuiData(final int id, final short data) {
		final int sideId = id / 2;
		final boolean isHigh = id % 2 == 1;
		final DistributorSide side = this.getSides().get(sideId);
		if (isHigh) {
			side.setHighShortData(data);
		} else {
			side.setLowShortData(data);
		}
	}

	public TileEntityManager[] getInventories() {
		if (this.dirty) {
			this.generateInventories();
			this.dirty = false;
		}
		return this.inventories;
	}

	private void generateInventories() {
		final TileEntityManager bot = this.generateManager(-1);
		final TileEntityManager top = this.generateManager(1);
		this.hasTop = (top != null);
		this.hasBot = (bot != null);
		this.inventories = this.populateManagers(top, bot, this.hasTop, this.hasBot);
	}

	private TileEntityManager[] populateManagers(final TileEntityManager topElement, final TileEntityManager botElement, final boolean hasTopElement, final boolean hasBotElement) {
		if (!hasTopElement && !hasBotElement) {
			return new TileEntityManager[0];
		}
		if (!hasBotElement) {
			return new TileEntityManager[] { topElement };
		}
		if (!hasTopElement) {
			return new TileEntityManager[] { botElement };
		}
		return new TileEntityManager[] { botElement, topElement };
	}

	private TileEntityManager generateManager(final int y) {
		final TileEntity TE = this.worldObj.getTileEntity(this.pos.up(y));
		if (TE != null && TE instanceof TileEntityManager) {
			return (TileEntityManager) TE;
		}
		return null;
	}

	@Override
	public boolean isUseableByPlayer(final EntityPlayer entityplayer) {
		return this.worldObj.getTileEntity(this.pos) == this && entityplayer.getDistanceSqToCenter(pos) <= 64.0;
	}

	private int translateSlotId(final int slot) {
		return slot % 60;
	}

	private TileEntityManager getManagerFromSlotId(final int slot) {
		final TileEntityManager[] invs = this.getInventories();
		int id = slot / 60;
		if (!this.hasTop || !this.hasBot) {
			id = 0;
		}
		if (id < 0 || id >= invs.length) {
			return null;
		}
		return invs[id];
	}

	@Override
	public int getSizeInventory() {
		return 120;
	}

	@Override
	public ItemStack getStackInSlot(final int slot) {
		final TileEntityManager manager = this.getManagerFromSlotId(slot);
		if (manager != null) {
			return manager.getStackInSlot(this.translateSlotId(slot));
		}
		return null;
	}

	@Override
	public ItemStack decrStackSize(final int slot, final int count) {
		final TileEntityManager manager = this.getManagerFromSlotId(slot);
		if (manager != null) {
			return manager.decrStackSize(this.translateSlotId(slot), count);
		}
		return null;
	}

	@Override
	public void setInventorySlotContents(final int slot, final ItemStack itemstack) {
		final TileEntityManager manager = this.getManagerFromSlotId(slot);
		if (manager != null) {
			manager.setInventorySlotContents(this.translateSlotId(slot), itemstack);
		}
	}

	@Override
	public String getName() {
		return "container.cargodistributor";
	}

	@Override
	public boolean hasCustomName() {
		return false;
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public void closeInventory(EntityPlayer player) {
	}

	@Override
	public void openInventory(EntityPlayer player) {
	}

	@Override
	public ItemStack removeStackFromSlot(final int slot) {
		final TileEntityManager manager = this.getManagerFromSlotId(slot);
		if (manager != null) {
			return manager.getStackInSlotOnClosing(this.translateSlotId(slot));
		}
		return null;
	}

	private boolean isChunkValid(final DistributorSide side, final TileEntityManager manager, final int chunkId, final boolean top) {
		for (final DistributorSetting setting : DistributorSetting.settings) {
			if (setting.isEnabled(this) && side.isSet(setting.getId()) && setting.isValid(manager, chunkId, top)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public int fill(final EnumFacing from, final FluidStack resource, final boolean doFill) {
		final IFluidTank[] tanks = this.getTanks(from);
		int amount = 0;
		for (final IFluidTank tank : tanks) {
			amount += tank.fill(resource, doFill);
		}
		return amount;
	}

	@Override
	public FluidStack drain(final EnumFacing from, final int maxDrain, final boolean doDrain) {
		return this.drain(from, null, maxDrain, doDrain);
	}

	@Override
	public FluidStack drain(final EnumFacing from, final FluidStack resource, final boolean doDrain) {
		return this.drain(from, resource, (resource == null) ? 0 : resource.amount, doDrain);
	}

	private FluidStack drain(final EnumFacing from, final FluidStack resource, int maxDrain, final boolean doDrain) {
		FluidStack ret = resource;
		if (ret != null) {
			ret = ret.copy();
			ret.amount = 0;
		}
		final IFluidTank[] arr$;
		final IFluidTank[] tanks = arr$ = this.getTanks(from);
		for (final IFluidTank tank : arr$) {
			FluidStack temp = null;
			temp = tank.drain(maxDrain, doDrain);
			if (temp != null && (ret == null || ret.isFluidEqual(temp))) {
				if (ret == null) {
					ret = temp;
				} else {
					final FluidStack fluidStack = ret;
					fluidStack.amount += temp.amount;
				}
				maxDrain -= temp.amount;
				if (maxDrain <= 0) {
					break;
				}
			}
		}
		if (ret != null && ret.amount == 0) {
			return null;
		}
		return ret;
	}

	private IFluidTank[] getTanks(final EnumFacing direction) {
		final TileEntityManager[] invs = this.getInventories();
		if (invs.length > 0) {
			for (final DistributorSide side : this.getSides()) {
				if (side.getSide() == direction) {
					final ArrayList<IFluidTank> tanks = new ArrayList<IFluidTank>();
					if (this.hasTop && this.hasBot) {
						this.populateTanks(tanks, side, invs[0], false);
						this.populateTanks(tanks, side, invs[1], true);
					} else if (this.hasTop) {
						this.populateTanks(tanks, side, invs[0], true);
					} else if (this.hasBot) {
						this.populateTanks(tanks, side, invs[0], false);
					}
					return tanks.toArray(new IFluidTank[tanks.size()]);
				}
			}
		}
		return new IFluidTank[0];
	}

	private void populateTanks(final ArrayList<IFluidTank> tanks, final DistributorSide side, final TileEntityManager manager, final boolean top) {
		if (manager != null && manager instanceof TileEntityLiquid) {
			final TileEntityLiquid fluid = (TileEntityLiquid) manager;
			final Tank[] managerTanks = fluid.getTanks();
			for (int i = 0; i < 4; ++i) {
				if (this.isChunkValid(side, manager, i, top) && !tanks.contains(managerTanks[i])) {
					tanks.add(managerTanks[i]);
				}
			}
		}
	}

	private void populateSlots(final ArrayList<Integer> slotchunks, final DistributorSide side, final TileEntityManager manager, final boolean top) {
		if (manager != null && manager instanceof TileEntityCargo) {
			for (int i = 0; i < 4; ++i) {
				if (this.isChunkValid(side, manager, i, top)) {
					final int chunkid = i + (top ? 4 : 0);
					if (!slotchunks.contains(chunkid)) {
						slotchunks.add(chunkid);
					}
				}
			}
		}
	}

	@Override
	public boolean canInsertItem(final int slot, final ItemStack item, EnumFacing side) {
		return true;
	}

	@Override
	public boolean canExtractItem(final int slot, final ItemStack item, EnumFacing side) {
		return true;
	}

	@Override
	public boolean isItemValidForSlot(final int slotId, final ItemStack item) {
		return true;
	}

	@Override
	public boolean canFill(final EnumFacing from, final Fluid fluid) {
		return true;
	}

	@Override
	public boolean canDrain(final EnumFacing from, final Fluid fluid) {
		return true;
	}

	@Override
	public FluidTankInfo[] getTankInfo(final EnumFacing from) {
		final IFluidTank[] tanks = this.getTanks(from);
		final FluidTankInfo[] infos = new FluidTankInfo[tanks.length];
		for (int i = 0; i < infos.length; ++i) {
			infos[i] = new FluidTankInfo(tanks[i].getFluid(), tanks[i].getCapacity());
		}
		return infos;
	}

	@Override
	public int[] getSlotsForFace(EnumFacing side) {
		final TileEntityManager[] invs = this.getInventories();
		if (invs.length > 0) {
			for (final DistributorSide otherSide : this.getSides()) {
				if (otherSide.getFacing() == side) {
					final ArrayList<Integer> slotchunks = new ArrayList<Integer>();
					if (this.hasTop && this.hasBot) {
						this.populateSlots(slotchunks, otherSide, invs[0], false);
						this.populateSlots(slotchunks, otherSide, invs[1], true);
					} else if (this.hasTop) {
						this.populateSlots(slotchunks, otherSide, invs[0], true);
					} else if (this.hasBot) {
						this.populateSlots(slotchunks, otherSide, invs[0], false);
					}
					final int[] ret = new int[slotchunks.size() * 15];
					int id = 0;
					for (final int chunkid : slotchunks) {
						for (int i = 0; i < 15; ++i) {
							ret[id] = chunkid * 15 + i;
							++id;
						}
					}
					return ret;
				}
			}
		}
		return new int[0];
	}

	@Override
	public int getField(int id) {
		return 0;
	}

	@Override
	public void setField(int id, int value) {

	}

	@Override
	public int getFieldCount() {
		return 0;
	}

	@Override
	public void clear() {

	}
}
