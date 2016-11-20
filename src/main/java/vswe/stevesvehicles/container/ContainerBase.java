package vswe.stevesvehicles.container;

import java.util.Iterator;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vswe.stevesvehicles.tileentity.TileEntityBase;
import vswe.stevesvehicles.transfer.TransferHandler;

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
			return null;
		}
		ItemStack itemstack = null;
		Slot slot = inventorySlots.get(i);
		if (slot != null && slot.getHasStack()) {
			ItemStack itemstack1 = slot.getStack();
			itemstack = itemstack1.copy();
			if (i < getMyInventory().getSizeInventory()) {
				if (!mergeItemStack(itemstack1, getMyInventory().getSizeInventory() + 28, getMyInventory().getSizeInventory() + 36, false)) {
					if (!mergeItemStack(itemstack1, getMyInventory().getSizeInventory(), getMyInventory().getSizeInventory() + 28, false)) {
						return null;
					}
				}
			} else if (!mergeItemStack(itemstack1, 0, getMyInventory().getSizeInventory(), false)) {
				return null;
			}
			if (itemstack1.func_190916_E() == 0) {
				slot.putStack(ItemStack.field_190927_a);
			} else {
				slot.onSlotChanged();
			}
			if (itemstack1.func_190916_E() != itemstack.func_190916_E()) {
				return slot.func_190901_a(player, itemstack1);
			} else {
				return null;
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
			while (item.func_190916_E() > 0 && (!invert && id < end || invert && id >= start)) {
				slot = this.inventorySlots.get(id);
				slotItem = slot.getStack();
				if (slotItem != null && slotItem.func_190916_E() > 0 && slotItem.getItem() == item.getItem() && (!item.getHasSubtypes() || item.getItemDamage() == slotItem.getItemDamage()) && ItemStack.areItemStackTagsEqual(item, slotItem)) {
					int size = slotItem.func_190916_E() + item.func_190916_E();
					int maxLimit = Math.min(item.getMaxStackSize(), slot.getSlotStackLimit());
					if (size <= maxLimit) {
						item.func_190920_e(0);
						slotItem.func_190920_e(size);
						slot.onSlotChanged();
						result = true;
					} else if (slotItem.func_190916_E() < maxLimit) {
						item.func_190918_g(maxLimit - slotItem.func_190916_E());
						slotItem.func_190920_e(maxLimit);
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
		if (item.func_190916_E() > 0) {
			if (invert) {
				id = end - 1;
			} else {
				id = start;
			}
			while (!invert && id < end || invert && id >= start) {
				slot = this.inventorySlots.get(id);
				slotItem = slot.getStack();
				if (slotItem == null && TransferHandler.isItemValidForTransfer(slot, item, TransferHandler.TransferType.SHIFT)) {
					int stackSize = Math.min(slot.getSlotStackLimit(), item.func_190916_E());
					ItemStack newItem = item.copy();
					newItem.func_190920_e(stackSize);
					item.func_190918_g(stackSize);
					slot.putStack(newItem);
					slot.onSlotChanged();
					result = item.func_190916_E() == 0;
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
