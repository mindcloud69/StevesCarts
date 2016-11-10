package vswe.stevescarts.blocks.tileentities;

import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import vswe.stevescarts.PacketHandler;
import vswe.stevescarts.containers.ContainerManager;
import vswe.stevescarts.entitys.EntityMinecartModular;
import vswe.stevescarts.helpers.NBTHelper;
import vswe.stevescarts.helpers.storages.TransferManager;

public abstract class TileEntityManager extends TileEntityBase implements IInventory {
	private TransferManager standardTransferHandler;
	private ItemStack[] cargoItemStacks;
	public int layoutType;
	public int moveTime;
	public boolean[] toCart;
	public boolean[] doReturn;
	public int[] amount;
	public int[] color;

	public TileEntityManager() {
		this.toCart = new boolean[] { true, true, true, true };
		this.doReturn = new boolean[] { false, false, false, false };
		this.amount = new int[] { 0, 0, 0, 0 };
		this.color = new int[] { 1, 2, 3, 4 };
		this.cargoItemStacks = new ItemStack[this.getSizeInventory()];
		this.moveTime = 0;
		this.standardTransferHandler = new TransferManager();
	}

	@Override
	public ItemStack getStackInSlot(final int i) {
		return this.cargoItemStacks[i];
	}

	@Override
	public ItemStack decrStackSize(final int i, final int j) {
		if (this.cargoItemStacks[i] == null) {
			return null;
		}
		if (this.cargoItemStacks[i].stackSize <= j) {
			final ItemStack itemstack = this.cargoItemStacks[i];
			this.cargoItemStacks[i] = null;
			this.markDirty();
			return itemstack;
		}
		final ItemStack itemstack2 = this.cargoItemStacks[i].splitStack(j);
		if (this.cargoItemStacks[i].stackSize == 0) {
			this.cargoItemStacks[i] = null;
		}
		this.markDirty();
		return itemstack2;
	}

	@Override
	public void setInventorySlotContents(final int i, final ItemStack itemstack) {
		this.cargoItemStacks[i] = itemstack;
		if (itemstack != null && itemstack.stackSize > this.getInventoryStackLimit()) {
			itemstack.stackSize = this.getInventoryStackLimit();
		}
		this.markDirty();
	}

	@Override
	public void readFromNBT(final NBTTagCompound nbttagcompound) {
		super.readFromNBT(nbttagcompound);
		final NBTTagList nbttaglist = nbttagcompound.getTagList("Items", NBTHelper.COMPOUND.getId());
		this.cargoItemStacks = new ItemStack[this.getSizeInventory()];
		for (int i = 0; i < nbttaglist.tagCount(); ++i) {
			final NBTTagCompound nbttagcompound2 = nbttaglist.getCompoundTagAt(i);
			final byte byte0 = nbttagcompound2.getByte("Slot");
			if (byte0 >= 0 && byte0 < this.cargoItemStacks.length) {
				this.cargoItemStacks[byte0] = ItemStack.loadItemStackFromNBT(nbttagcompound2);
			}
		}
		this.moveTime = nbttagcompound.getByte("movetime");
		this.setLowestSetting(nbttagcompound.getByte("lowestNumber"));
		this.layoutType = nbttagcompound.getByte("layout");
		final byte temp = nbttagcompound.getByte("tocart");
		final byte temp2 = nbttagcompound.getByte("doReturn");
		for (int j = 0; j < 4; ++j) {
			this.amount[j] = nbttagcompound.getByte("amount" + j);
			this.color[j] = nbttagcompound.getByte("color" + j);
			if (this.color[j] == 0) {
				this.color[j] = j + 1;
			}
			this.toCart[j] = ((temp & 1 << j) != 0x0);
			this.doReturn[j] = ((temp2 & 1 << j) != 0x0);
		}
	}

	@Override
	public NBTTagCompound writeToNBT(final NBTTagCompound nbttagcompound) {
		super.writeToNBT(nbttagcompound);
		nbttagcompound.setByte("movetime", (byte) this.moveTime);
		nbttagcompound.setByte("lowestNumber", (byte) this.getLowestSetting());
		nbttagcompound.setByte("layout", (byte) this.layoutType);
		byte temp = 0;
		byte temp2 = 0;
		for (int i = 0; i < 4; ++i) {
			nbttagcompound.setByte("amount" + i, (byte) this.amount[i]);
			nbttagcompound.setByte("color" + i, (byte) this.color[i]);
			if (this.toCart[i]) {
				temp |= (byte) (1 << i);
			}
			if (this.doReturn[i]) {
				temp2 |= (byte) (1 << i);
			}
		}
		nbttagcompound.setByte("tocart", temp);
		nbttagcompound.setByte("doReturn", temp2);
		final NBTTagList nbttaglist = new NBTTagList();
		for (int j = 0; j < this.cargoItemStacks.length; ++j) {
			if (this.cargoItemStacks[j] != null) {
				final NBTTagCompound nbttagcompound2 = new NBTTagCompound();
				nbttagcompound2.setByte("Slot", (byte) j);
				this.cargoItemStacks[j].writeToNBT(nbttagcompound2);
				nbttaglist.appendTag(nbttagcompound2);
			}
		}
		nbttagcompound.setTag("Items", nbttaglist);
		return nbttagcompound;
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	public EntityMinecartModular getCart() {
		return this.standardTransferHandler.getCart();
	}

	public void setCart(final EntityMinecartModular cart) {
		this.standardTransferHandler.setCart(cart);
	}

	public int getSetting() {
		return this.standardTransferHandler.getSetting();
	}

	public void setSetting(final int val) {
		this.standardTransferHandler.setSetting(val);
	}

	public int getSide() {
		return this.standardTransferHandler.getSide();
	}

	public void setSide(final int val) {
		this.standardTransferHandler.setSide(val);
	}

	public int getLastSetting() {
		return this.standardTransferHandler.getLastSetting();
	}

	public void setLastSetting(final int val) {
		this.standardTransferHandler.setLastSetting(val);
	}

	public int getLowestSetting() {
		return this.standardTransferHandler.getLowestSetting();
	}

	public void setLowestSetting(final int val) {
		this.standardTransferHandler.setLowestSetting(val);
	}

	public int getWorkload() {
		return this.standardTransferHandler.getWorkload();
	}

	public void setWorkload(final int val) {
		this.standardTransferHandler.setWorkload(val);
	}

	@Override
	public void updateEntity() {
		if (this.worldObj.isRemote) {
			this.updateLayout();
			return;
		}
		if (this.getCart() == null || this.getCart().isDead || this.getSide() < 0 || this.getSide() > 3 || !this.getCart().isDisabled()) {
			this.standardTransferHandler.reset();
			return;
		}
		++this.moveTime;
		if (this.moveTime >= 24) {
			this.moveTime = 0;
			if (!this.exchangeItems(this.standardTransferHandler)) {
				this.getCart().releaseCart();
				if (this.doReturn[this.getSide()]) {
					this.getCart().turnback();
				}
				this.standardTransferHandler.reset();
			}
		}
	}

	public boolean exchangeItems(final TransferManager transfer) {
		transfer.setSetting(transfer.getLowestSetting());
		while (transfer.getSetting() < 4) {
			Label_0130:
			{
			if (this.color[transfer.getSetting()] - 1 == transfer.getSide()) {
				transfer.setLowestSetting(transfer.getSetting());
				if (transfer.getLastSetting() != transfer.getSetting()) {
					transfer.setWorkload(0);
					transfer.setLastSetting(transfer.getSetting());
					return true;
				}
				Label_0108:
				{
					if (this.toCart[transfer.getSetting()]) {
						if (!transfer.getToCartEnabled()) {
							break Label_0108;
						}
					} else if (!transfer.getFromCartEnabled()) {
						break Label_0108;
					}
					if (this.isTargetValid(transfer)) {
						if (this.doTransfer(transfer)) {
							return true;
						}
						break Label_0130;
					}
				}
				transfer.setLowestSetting(transfer.getSetting() + 1);
				return true;
			}
			}
		transfer.setSetting(transfer.getSetting() + 1);
		}
		return false;
	}

	public void sendPacket(final int id) {
		this.sendPacket(id, new byte[0]);
	}

	public void sendPacket(final int id, final byte data) {
		this.sendPacket(id, new byte[] { data });
	}

	public void sendPacket(final int id, final byte[] data) {
		PacketHandler.sendPacket(id, data);
	}

	@Override
	public void receivePacket(final int id, final byte[] data, final EntityPlayer player) {
		if (id == 0) {
			final int railID = data[0];
			this.toCart[railID] = !this.toCart[railID];
			if (this.color[railID] - 1 == this.getSide()) {
				this.reset();
			}
		} else if (id == 4) {
			final int railID = data[0];
			if (this.color[railID] != 5) {
				this.doReturn[this.color[railID] - 1] = !this.doReturn[this.color[railID] - 1];
			}
		} else if (id == 5) {
			final int difference = data[0];
			this.layoutType += difference;
			if (this.layoutType > 2) {
				this.layoutType = 0;
			} else if (this.layoutType < 0) {
				this.layoutType = 2;
			}
			this.reset();
		} else {
			final byte railsAndDifferenceCombined = data[0];
			final int railID2 = railsAndDifferenceCombined & 0x3;
			final int k = (railsAndDifferenceCombined & 0x4) >> 2;
			int difference2;
			if (k == 0) {
				difference2 = 1;
			} else {
				difference2 = -1;
			}
			if (id == 2) {
				final int[] amount = this.amount;
				final int n = railID2;
				amount[n] += difference2;
				if (this.amount[railID2] >= this.getAmountCount()) {
					this.amount[railID2] = 0;
				} else if (this.amount[railID2] < 0) {
					this.amount[railID2] = this.getAmountCount() - 1;
				}
				if (this.color[railID2] - 1 == this.getSide()) {
					this.reset();
				}
			} else if (id == 3) {
				if (this.color[railID2] != 5) {
					boolean willStillExist = false;
					for (int side = 0; side < 4; ++side) {
						if (side != railID2 && this.color[railID2] == this.color[side]) {
							willStillExist = true;
							break;
						}
					}
					if (!willStillExist) {
						this.doReturn[this.color[railID2] - 1] = false;
					}
				}
				final int[] color = this.color;
				final int n2 = railID2;
				color[n2] += difference2;
				if (this.color[railID2] > 5) {
					this.color[railID2] = 1;
				} else if (this.color[railID2] < 1) {
					this.color[railID2] = 5;
				}
				if (this.color[railID2] - 1 == this.getSide()) {
					this.reset();
				}
			} else {
				this.receiveClickData(id, railID2, difference2);
			}
		}
	}

	@Override
	public void initGuiData(final Container con, final IContainerListener crafting) {
		this.checkGuiData((ContainerManager) con, crafting, true);
	}

	@Override
	public void checkGuiData(final Container con, final IContainerListener crafting) {
		this.checkGuiData((ContainerManager) con, crafting, false);
	}

	public void checkGuiData(final ContainerManager con, final IContainerListener crafting, final boolean isNew) {
		short header = (short) (this.moveTime & 0x1F);
		header |= (short) ((this.layoutType & 0x3) << 5);
		for (int i = 0; i < 4; ++i) {
			header |= (short) ((this.toCart[i] ? 1 : 0) << 7 + i);
		}
		for (int i = 0; i < 4; ++i) {
			header |= (short) ((this.doReturn[i] ? 1 : 0) << 11 + i);
		}
		if (isNew || con.lastHeader != header) {
			this.updateGuiData(con, crafting, 0, header);
			con.lastHeader = header;
		}
		short colorShort = 0;
		for (int j = 0; j < 4; ++j) {
			colorShort |= (short) ((this.color[j] & 0x7) << j * 3);
		}
		colorShort |= (short) ((this.getLastSetting() & 0x7) << 12);
		if (isNew || con.lastColor != colorShort) {
			this.updateGuiData(con, crafting, 1, colorShort);
			con.lastColor = colorShort;
		}
		short amountShort = 0;
		for (int k = 0; k < 4; ++k) {
			amountShort |= (short) ((this.amount[k] & 0xF) << k * 4);
		}
		if (isNew || con.lastAmount != amountShort) {
			this.updateGuiData(con, crafting, 3, amountShort);
			con.lastAmount = amountShort;
		}
	}

	@Override
	public void receiveGuiData(final int id, final short data) {
		if (id == 0) {
			this.moveTime = (data & 0x1F);
			this.layoutType = (data & 0x60) >> 5;
			this.updateLayout();
			for (int i = 0; i < 4; ++i) {
				this.toCart[i] = ((data & 1 << 7 + i) != 0x0);
			}
			for (int i = 0; i < 4; ++i) {
				this.doReturn[i] = ((data & 1 << 11 + i) != 0x0);
			}
		} else if (id == 1) {
			for (int i = 0; i < 4; ++i) {
				this.color[i] = (data & 7 << i * 3) >> i * 3;
			}
			this.setLastSetting((data & 0x7000) >> 12);
		} else if (id == 3) {
			for (int i = 0; i < 4; ++i) {
				this.amount[i] = (data & 15 << i * 4) >> i * 4;
			}
		}
	}

	public int moveProgressScaled(final int i) {
		return this.moveTime * i / 24;
	}

	@Override
	public void closeInventory(EntityPlayer player) {
	}

	@Override
	public void openInventory(EntityPlayer player) {
	}

	@Override
	public boolean isUseableByPlayer(final EntityPlayer entityplayer) {
		return this.worldObj.getTileEntity(this.pos) == this && entityplayer.getDistanceSq(this.pos.getX() + 0.5, this.pos.getY() + 0.5, this.pos.getZ() + 0.5) <= 64.0;
	}

	public ItemStack getStackInSlotOnClosing(final int par1) {
		if (this.cargoItemStacks[par1] != null) {
			final ItemStack var2 = this.cargoItemStacks[par1];
			this.cargoItemStacks[par1] = null;
			return var2;
		}
		return null;
	}

	protected void updateLayout() {
	}

	protected void receiveClickData(final int packetid, final int id, final int dif) {
	}

	protected abstract boolean isTargetValid(final TransferManager p0);

	protected abstract boolean doTransfer(final TransferManager p0);

	public abstract int getAmountCount();

	protected void reset() {
		this.setWorkload(this.moveTime = 0);
	}

	protected int getAmountId(final int id) {
		return this.amount[id];
	}

	@Override
	public int getSizeInventory() {
		return cargoItemStacks.length;
	}

	@Nullable
	@Override
	public ItemStack removeStackFromSlot(int index) {
		return ItemStackHelper.getAndRemove(cargoItemStacks, index);
	}


	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack) {
		return true;
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

	@Override
	public String getName() {
		return "container.cargomanager";
	}

	@Override
	public boolean hasCustomName() {
		return false;
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing)
	{
		if(capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
		{
			return true;
		}
		return super.hasCapability(capability, facing);
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing)
	{
		if(capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
		{
			return (T) new InvWrapper(this);
		}
		return super.getCapability(capability, facing);
	}
}
