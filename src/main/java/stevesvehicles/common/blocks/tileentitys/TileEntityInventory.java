package stevesvehicles.common.blocks.tileentitys;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;

public abstract class TileEntityInventory extends TileEntityBase implements IInventory {
	public static final ItemStack INVALID_STACK = ItemStack.EMPTY;
	protected NonNullList<ItemStack> inventoryStacks;

	public TileEntityInventory(int size) {
		this(NonNullList.<ItemStack> withSize(size, INVALID_STACK));
	}

	public TileEntityInventory(NonNullList<ItemStack> inventoryStacks) {
		this.inventoryStacks = inventoryStacks;
	}

	@Override
	public int getSizeInventory() {
		return inventoryStacks.size();
	}

	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		super.readFromNBT(nbttagcompound);
		inventoryStacks.clear();
		ItemStackHelper.loadAllItems(nbttagcompound, inventoryStacks);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbttagcompound) {
		super.writeToNBT(nbttagcompound);
		ItemStackHelper.saveAllItems(nbttagcompound, inventoryStacks);
		return nbttagcompound;
	}

	@Override
	public ItemStack getStackInSlot(int i) {
		return inventoryStacks.get(i);
	}

	@Override
	public ItemStack decrStackSize(int index, int count) {
		return ItemStackHelper.getAndSplit(inventoryStacks, index, count);
	}

	@Override
	public ItemStack removeStackFromSlot(int index) {
		return ItemStackHelper.getAndRemove(inventoryStacks, index);
	}

	@Override
	public boolean isEmpty() {
		for (ItemStack itemstack : inventoryStacks) {
			if (!itemstack.isEmpty()) {
				return false;
			}
		}
		return true;
	}

	@Override
	public void setInventorySlotContents(int i, ItemStack itemstack) {
		inventoryStacks.set(i, itemstack);
		if (itemstack.getCount() > getInventoryStackLimit()) {
			itemstack.setCount(getInventoryStackLimit());
		}
		markDirty();
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public void openInventory(EntityPlayer player) {
	}

	@Override
	public void closeInventory(EntityPlayer player) {
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
}
