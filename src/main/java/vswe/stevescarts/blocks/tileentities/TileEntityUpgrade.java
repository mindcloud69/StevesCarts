package vswe.stevescarts.blocks.tileentities;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.*;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.*;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vswe.stevescarts.blocks.BlockUpgrade;
import vswe.stevescarts.blocks.ModBlocks;
import vswe.stevescarts.containers.ContainerBase;
import vswe.stevescarts.containers.ContainerUpgrade;
import vswe.stevescarts.guis.GuiBase;
import vswe.stevescarts.guis.GuiUpgrade;
import vswe.stevescarts.helpers.NBTHelper;
import vswe.stevescarts.helpers.storages.ITankHolder;
import vswe.stevescarts.helpers.storages.SCTank;
import vswe.stevescarts.helpers.storages.TransferHandler;
import vswe.stevescarts.upgrades.AssemblerUpgrade;
import vswe.stevescarts.upgrades.InterfaceEffect;
import vswe.stevescarts.upgrades.InventoryEffect;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TileEntityUpgrade extends TileEntityBase implements IInventory, ISidedInventory, ITankHolder, ITickable {
	public SCTank tank = new SCTank(this, 0, 0);
	private TileEntityCartAssembler master;
	private int type;
	private boolean initialized;
	private NBTTagCompound comp;
	NonNullList<ItemStack> inventoryStacks;
	private int[] slotsForSide;
	BlockUpgrade blockUpgrade = (BlockUpgrade) ModBlocks.UPGRADE.getBlock();
	boolean shouldSetType;

	@SideOnly(Side.CLIENT)
	@Override
	public GuiBase getGui(final InventoryPlayer inv) {
		return new GuiUpgrade(inv, this);
	}

	@Override
	public ContainerBase getContainer(final InventoryPlayer inv) {
		return new ContainerUpgrade(inv, this);
	}

	public void setMaster(final TileEntityCartAssembler master, EnumFacing side) {
		this.master = master;
		if (side != null) {
			world.setBlockState(pos, blockUpgrade.getDefaultState().withProperty(BlockUpgrade.FACING, side).withProperty(BlockUpgrade.TYPE, getType()));
		} else {
			world.setBlockState(pos, blockUpgrade.getDefaultState().withProperty(BlockUpgrade.TYPE, getType()));
		}

	}

	public EnumFacing getSide() {
		return world.getBlockState(pos).getValue(BlockUpgrade.FACING);
	}

	public TileEntityCartAssembler getMaster() {
		return this.master;
	}

	public void setType(final int type) {
		setType(type, true);
	}

	public void setType(final int type, boolean setBlockState) {
		this.type = type;
		if (setBlockState) {
			world.setBlockState(pos, blockUpgrade.getDefaultState().withProperty(BlockUpgrade.TYPE, type).withProperty(BlockUpgrade.FACING, getSide()));
		}
		if (!this.initialized) {
			this.initialized = true;
			final AssemblerUpgrade upgrade = this.getUpgrade();
			if (upgrade != null) {
				this.comp = new NBTTagCompound();
				this.slotsForSide = new int[upgrade.getInventorySize()];
				upgrade.init(this);
				if (upgrade.getInventorySize() > 0) {
					this.inventoryStacks = NonNullList.withSize(upgrade.getInventorySize(), ItemStack.EMPTY);
					for (int i = 0; i < this.slotsForSide.length; ++i) {
						this.slotsForSide[i] = i;
					}
				}
			} else {
				this.inventoryStacks = null;
			}
		}
	}

	public int getType() {
		return this.type;
	}

	public NBTTagCompound getCompound() {
		return this.comp;
	}

	@Nullable
	@Override
	public SPacketUpdateTileEntity getUpdatePacket() {
		final NBTTagCompound var1 = new NBTTagCompound();
		this.writeToNBT(var1);
		return new SPacketUpdateTileEntity(this.pos, 1, var1);
	}

	@Override
	public void onDataPacket(final NetworkManager net, final SPacketUpdateTileEntity pkt) {
		this.readFromNBT(pkt.getNbtCompound());
	}

	@Override
	public NBTTagCompound getUpdateTag() {
		return writeToNBT(super.getUpdateTag());
	}

	public AssemblerUpgrade getUpgrade() {
		return AssemblerUpgrade.getUpgrade(this.type);
	}

	public boolean hasInventory() {
		return this.inventoryStacks != null;
	}

	@Override
	public void readFromNBT(final NBTTagCompound tagCompound) {
		super.readFromNBT(tagCompound);
		this.setType(tagCompound.getByte("Type"), false);
		shouldSetType = true;
		final NBTTagList items = tagCompound.getTagList("Items", NBTHelper.COMPOUND.getId());
		for (int i = 0; i < items.tagCount(); ++i) {
			final NBTTagCompound item = items.getCompoundTagAt(i);
			final int slot = item.getByte("Slot") & 0xFF;
			@Nonnull
			ItemStack iStack = new ItemStack(item);
			if (slot >= 0 && slot < this.getSizeInventory()) {
				this.setInventorySlotContents(slot, iStack);
			}
		}
		final AssemblerUpgrade upgrade = this.getUpgrade();
		if (upgrade != null) {
			upgrade.load(this, tagCompound);
		}
	}

	@Override
	public NBTTagCompound writeToNBT(final NBTTagCompound tagCompound) {
		super.writeToNBT(tagCompound);
		final NBTTagList items = new NBTTagList();
		if (this.inventoryStacks != null) {
			for (int i = 0; i < this.inventoryStacks.size(); ++i) {
				@Nonnull
				ItemStack iStack = this.inventoryStacks.get(i);
				if (!iStack.isEmpty()) {
					final NBTTagCompound item = new NBTTagCompound();
					item.setByte("Slot", (byte) i);
					iStack.writeToNBT(item);
					items.appendTag(item);
				}
			}
		}
		tagCompound.setTag("Items", items);
		tagCompound.setByte("Type", (byte) this.type);
		final AssemblerUpgrade upgrade = this.getUpgrade();
		if (upgrade != null) {
			upgrade.save(this, tagCompound);
		}
		return tagCompound;
	}

	@Override
	public boolean isUsableByPlayer(final EntityPlayer entityplayer) {
		return this.world.getTileEntity(this.pos) == this && entityplayer.getDistanceSqToCenter(pos) <= 64.0;
	}

	@Override
	public void openInventory(EntityPlayer player) {

	}

	@Override
	public void closeInventory(EntityPlayer player) {

	}

	@Override
	public void updateEntity() {
		if (this.getUpgrade() != null && this.getMaster() != null) {
			this.getUpgrade().update(this);
		}
	}

	@Override
	public void initGuiData(final Container con, final IContainerListener crafting) {
		if (this.getUpgrade() != null) {
			final InterfaceEffect gui = this.getUpgrade().getInterfaceEffect();
			if (gui != null) {
				gui.checkGuiData(this, (ContainerUpgrade) con, crafting, true);
			}
		}
	}

	@Override
	public void checkGuiData(final Container con, final IContainerListener crafting) {
		if (this.getUpgrade() != null) {
			final InterfaceEffect gui = this.getUpgrade().getInterfaceEffect();
			if (gui != null) {
				gui.checkGuiData(this, (ContainerUpgrade) con, crafting, false);
			}
		}
	}

	@Override
	public void receiveGuiData(final int id, final short data) {
		if (this.getUpgrade() != null) {
			final InterfaceEffect gui = this.getUpgrade().getInterfaceEffect();
			if (gui != null) {
				gui.receiveGuiData(this, id, data);
			}
		}
	}

	@Override
	public int getSizeInventory() {
		if (this.inventoryStacks != null) {
			return this.inventoryStacks.size();
		}
		if (this.master == null) {
			return 0;
		}
		return this.master.getSizeInventory();
	}

	@Override
	public boolean isEmpty() {
		for (ItemStack stack : inventoryStacks) {
			if (!stack.isEmpty()) {
				return false;
			}
		}
		return true;
	}

	@Override
	@Nonnull
	public ItemStack getStackInSlot(final int i) {
		if (this.inventoryStacks == null) {
			if (this.master == null) {
				return ItemStack.EMPTY;
			}
			return this.master.getStackInSlot(i);
		} else {
			if (i < 0 || i >= this.getSizeInventory()) {
				return ItemStack.EMPTY;
			}
			return this.inventoryStacks.get(i);
		}
	}

	@Override
	@Nonnull
	public ItemStack decrStackSize(final int i, final int j) {
		if (this.inventoryStacks == null) {
			if (this.master == null) {
				return ItemStack.EMPTY;
			}
			return this.master.decrStackSize(i, j);
		} else {
			if (i < 0 || i >= this.getSizeInventory()) {
				return ItemStack.EMPTY;
			}
			if (this.inventoryStacks.get(i).isEmpty()) {
				return ItemStack.EMPTY;
			}
			if (this.inventoryStacks.get(i).getCount() <= j) {
				@Nonnull
				ItemStack itemstack = this.inventoryStacks.get(i);
				inventoryStacks.set(i, ItemStack.EMPTY);
				this.markDirty();
				return itemstack;
			}
			@Nonnull
			ItemStack itemstack2 = this.inventoryStacks.get(i).splitStack(j);
			if (this.inventoryStacks.get(i).getCount() == 0) {
				this.inventoryStacks.set(i, ItemStack.EMPTY);
			}
			this.markDirty();
			return itemstack2;
		}
	}

	@Nullable
	@Override
	@Nonnull
	public ItemStack removeStackFromSlot(int index) {
		return null;
	}

	@Override
	public void setInventorySlotContents(final int i,
	                                     @Nonnull
		                                     ItemStack itemstack) {
		if (this.inventoryStacks == null) {
			if (this.master != null) {
				this.master.setInventorySlotContents(i, itemstack);
			}
		} else {
			if (i < 0 || i >= this.getSizeInventory()) {
				return;
			}
			this.inventoryStacks.set(i, itemstack);
			if (!itemstack.isEmpty() && itemstack.getCount() > this.getInventoryStackLimit()) {
				itemstack.setCount(this.getInventoryStackLimit());
			}
			this.markDirty();
		}
	}

	public String getInventoryName() {
		return "container.assemblerupgrade";
	}

	public boolean hasCustomInventoryName() {
		return false;
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	public void closeInventory() {
	}

	public void openInventory() {
	}

	@Nonnull
	public ItemStack getStackInSlotOnClosing(final int i) {
		if (this.inventoryStacks == null) {
			if (this.master == null) {
				return null;
			}
			return this.master.getStackInSlot(i);
		} else {
			@Nonnull
			ItemStack item = this.getStackInSlot(i);
			if (item != null) {
				this.setInventorySlotContents(i, null);
				return item;
			}
			return null;
		}
	}

	@Override
	public void markDirty() {
		if (this.getUpgrade() != null) {
			final InventoryEffect inv = this.getUpgrade().getInventoryEffect();
			if (inv != null) {
				inv.onInventoryChanged(this);
			}
		}
	}

	@Override
	public boolean isItemValidForSlot(final int slot,
	                                  @Nonnull
		                                  ItemStack item) {
		if (this.getUpgrade() != null) {
			final InventoryEffect inv = this.getUpgrade().getInventoryEffect();
			if (inv != null) {
				return inv.isItemValid(slot, item);
			}
		}
		return this.getMaster() != null && this.getMaster().isItemValidForSlot(slot, item);
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
	public boolean canInsertItem(final int slot,
	                             @Nonnull
		                             ItemStack item, EnumFacing side) {
		if (this.getUpgrade() != null) {
			final InventoryEffect inv = this.getUpgrade().getInventoryEffect();
			if (inv != null) {
				return this.isItemValidForSlot(slot, item);
			}
		}
		return this.getMaster() != null && this.getMaster().canInsertItem(slot, item, side);
	}

	@Override
	public boolean canExtractItem(final int slot,
	                              @Nonnull
		                              ItemStack item, EnumFacing side) {
		if (this.getUpgrade() != null) {
			final InventoryEffect inv = this.getUpgrade().getInventoryEffect();
			if (inv != null) {
				return true;
			}
		}
		return this.getMaster() != null && this.getMaster().canExtractItem(slot, item, side);
	}


	@Override
	@Nonnull
	public ItemStack getInputContainer(final int tankid) {
		return this.getStackInSlot(0);
	}

	@Override
	public void clearInputContainer(final int tankid) {
		this.setInventorySlotContents(0, null);
	}

	@Override
	public void addToOutputContainer(final int tankid,
	                                 @Nonnull
		                                 ItemStack item) {
		TransferHandler.TransferItem(item, this, 1, 1, new ContainerUpgrade(null, this), Slot.class, null, -1);
	}

	@Override
	public void onFluidUpdated(final int tankid) {
	}

	@Override
	public void drawImage(int p0, GuiBase p1, int p3, int p4, int p5, int p6, int p7, int p8) {

	}

	//	@SideOnly(Side.CLIENT)
	//	public void drawImage(final int tankid, final GuiBase gui, final IIcon icon, final int targetX, final int targetY, final int srcX, final int srcY, final int sizeX, final int sizeY) {
	//		gui.drawIcon(icon, gui.getGuiLeft() + targetX, gui.getGuiTop() + targetY, sizeX / 16.0f, sizeY / 16.0f, srcX / 16.0f, srcY / 16.0f);
	//	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
			return true;
		}
		return super.hasCapability(capability, facing);
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
			return (T) tank;
		}
		return super.getCapability(capability, facing);
	}

	@Override
	public int[] getSlotsForFace(EnumFacing side) {
		if (this.getUpgrade() != null) {
			final InventoryEffect inv = this.getUpgrade().getInventoryEffect();
			if (inv != null) {
				return this.slotsForSide;
			}
		}
		if (this.getMaster() != null) {
			return this.getMaster().getSlotsForFace(side);
		}
		return new int[0];
	}

	@Override
	public String getName() {
		return null;
	}

	@Override
	public boolean hasCustomName() {
		return false;
	}

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate) {
		return false;
	}

	@Override
	public void update() {
		super.update();
		if (shouldSetType) {
			world.setBlockState(pos, world.getBlockState(pos).withProperty(BlockUpgrade.TYPE, type).withProperty(BlockUpgrade.FACING, getSide()));
			shouldSetType = false;
		}
	}
}
