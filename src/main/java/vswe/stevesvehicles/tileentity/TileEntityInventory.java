package vswe.stevesvehicles.tileentity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;

public abstract class TileEntityInventory extends TileEntityBase implements IInventory {

	private final NonNullList<ItemStack> inventoryStacks = NonNullList.<ItemStack>func_191197_a(5, ItemStack.field_190927_a);

	public TileEntityInventory() {
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		super.readFromNBT(nbttagcompound);;
		inventoryStacks.clear();
		ItemStackHelper.func_191283_b(nbttagcompound, inventoryStacks);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbttagcompound) {
		super.writeToNBT(nbttagcompound);
		ItemStackHelper.func_191282_a(nbttagcompound, inventoryStacks);
		return nbttagcompound;
	}

	@Override
	public ItemStack getStackInSlot(int i) {
		return inventoryStacks.get(i);
	}

	@Override
	public ItemStack decrStackSize(int index, int count)
	{
		return ItemStackHelper.getAndSplit(inventoryStacks, index, count);
	}

	@Override
	public ItemStack removeStackFromSlot(int index)
	{
		return ItemStackHelper.getAndRemove(inventoryStacks, index);
	}

	@Override
	public boolean func_191420_l()
	{
		for (ItemStack itemstack : inventoryStacks)
		{
			if (!itemstack.func_190926_b())
			{
				return false;
			}
		}

		return true;
	}

	@Override
	public void setInventorySlotContents(int i, ItemStack itemstack) {
		inventoryStacks.add(i, itemstack);
		if (itemstack.func_190916_E() > getInventoryStackLimit()) {
			itemstack.func_190920_e(getInventoryStackLimit());
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
