package stevesvehicles.common.container;

import java.util.Iterator;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import stevesvehicles.common.blocks.tileentitys.TileEntityBase;
import stevesvehicles.common.transfer.TransferHandler;

public abstract class ContainerBase extends Container {
	/**
	 * The inventory associated with this container
	 * 
	 * @return The IInventory or null if no inventory exists.
	 */
	public abstract IInventory getMyInventory();

	/**
	 * The tile entity this container is associated with
	 * 
	 * @return The Tile Entity or null if none exits.
	 */
	public abstract TileEntityBase getTileEntity();

	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int i) {
		if (getMyInventory() == null) {
			return ItemStack.EMPTY;
		}
		ItemStack itemstack = ItemStack.EMPTY;
		Slot slot = inventorySlots.get(i);
		if (slot != null && slot.getHasStack()) {
			ItemStack itemstack1 = slot.getStack();
			itemstack = itemstack1.copy();
			if (i < getMyInventory().getSizeInventory()) {
				if (!mergeItemStack(itemstack1, getMyInventory().getSizeInventory() + 28, getMyInventory().getSizeInventory() + 36, false)) {
					if (!mergeItemStack(itemstack1, getMyInventory().getSizeInventory(), getMyInventory().getSizeInventory() + 28, false)) {
						return ItemStack.EMPTY;
					}
				}
			} else if (!mergeItemStack(itemstack1, 0, getMyInventory().getSizeInventory(), false)) {
				return ItemStack.EMPTY;
			}
			if (itemstack1.isEmpty()) {
				slot.putStack(ItemStack.EMPTY);
			} else {
				slot.onSlotChanged();
			}
			if (itemstack1.getCount() != itemstack.getCount()) {
				return slot.onTake(player, itemstack1);
			} else {
				return ItemStack.EMPTY;
			}
		}
		return itemstack;
	}

	@Override
	protected boolean mergeItemStack(ItemStack item, int start, int end, boolean invert) {
		if (getMyInventory() == null) {
			return false;
		}
		boolean result = false;
		int id = start;
		if (invert) {
			id = end - 1;
		}
		Slot slot;
		ItemStack slotItem;
		if (item.isStackable()) {
			while (item.getCount() > 0 && (!invert && id < end || invert && id >= start)) {
				slot = this.inventorySlots.get(id);
				slotItem = slot.getStack();
				if (!slotItem.isEmpty() && slotItem.getItem() == item.getItem() && (!item.getHasSubtypes() || item.getItemDamage() == slotItem.getItemDamage()) && ItemStack.areItemStackTagsEqual(item, slotItem)) {
					int size = slotItem.getCount() + item.getCount();
					int maxLimit = Math.min(item.getMaxStackSize(), slot.getSlotStackLimit());
					if (size <= maxLimit) {
						item.setCount(0);
						slotItem.setCount(size);
						slot.onSlotChanged();
						result = true;
					} else if (slotItem.getCount() < maxLimit) {
						item.shrink(maxLimit - slotItem.getCount());
						slotItem.setCount(maxLimit);
						slot.onSlotChanged();
						result = true;
					}
				}
				if (invert) {
					--id;
				} else {
					++id;
				}
			}
		}
		if (item.getCount() > 0) {
			if (invert) {
				id = end - 1;
			} else {
				id = start;
			}
			while (!invert && id < end || invert && id >= start) {
				slot = this.inventorySlots.get(id);
				slotItem = slot.getStack();
				if (slotItem.isEmpty() && TransferHandler.isItemValidForTransfer(slot, item, TransferHandler.TransferType.SHIFT)) {
					int stackSize = Math.min(slot.getSlotStackLimit(), item.getCount());
					ItemStack newItem = item.copy();
					newItem.setCount(stackSize);
					item.shrink(stackSize);
					slot.putStack(newItem);
					slot.onSlotChanged();
					result = item.getCount() == 0;
					break;
				}
				if (invert) {
					--id;
				} else {
					++id;
				}
			}
		}
		return result;
	}

	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return getTileEntity() != null && getTileEntity().isUsableByPlayer(player);
	}

	@Override
	public void addListener(IContainerListener listener) {
		super.addListener(listener);
		if (getTileEntity() != null) {
			getTileEntity().initGuiData(this, listener);
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void updateProgressBar(int id, int val) {
		val &= 65535;
		if (getTileEntity() != null) {
			getTileEntity().receiveGuiData(id, (short) val);
		}
	}

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		if (getTileEntity() != null) {
			Iterator<IContainerListener> playerIterator = this.listeners.iterator();
			while (playerIterator.hasNext()) {
				IContainerListener player = playerIterator.next();
				getTileEntity().checkGuiData(this, player);
			}
		}
	}
}
