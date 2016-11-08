package vswe.stevescarts.helpers.storages;

import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import vswe.stevescarts.containers.slots.ISpecialItemTransferValidator;
import vswe.stevescarts.containers.slots.ISpecialSlotValidator;

public class TransferHandler {
	public static boolean isSlotOfType(final Slot slot, final Class slotType) {
		if (slot instanceof ISpecialSlotValidator) {
			final ISpecialSlotValidator specSlot = (ISpecialSlotValidator) slot;
			return specSlot.isSlotValid();
		}
		return slotType.isInstance(slot);
	}

	public static boolean isItemValidForTransfer(final Slot slot, final ItemStack item, final TRANSFER_TYPE type) {
		if (slot instanceof ISpecialItemTransferValidator) {
			final ISpecialItemTransferValidator specSlot = (ISpecialItemTransferValidator) slot;
			return specSlot.isItemValidForTransfer(item, type);
		}
		return slot.isItemValid(item);
	}

	public static void TransferItem(final ItemStack iStack, final IInventory inv, final Container cont, final int maxItems) {
		TransferItem(iStack, inv, cont, Slot.class, null, maxItems);
	}

	public static void TransferItem(final ItemStack iStack, final IInventory inv, final Container cont, final Class validSlot, final int maxItems, final TRANSFER_TYPE type) {
		TransferItem(iStack, inv, 0, inv.getSizeInventory() - 1, cont, validSlot, null, maxItems, type, false);
	}

	public static void TransferItem(final ItemStack iStack, final IInventory inv, final Container cont, final Class validSlot, final Class invalidSlot, final int maxItems) {
		TransferItem(iStack, inv, 0, inv.getSizeInventory() - 1, cont, validSlot, invalidSlot, maxItems);
	}

	public static void TransferItem(final ItemStack iStack,
			final IInventory inv,
			final int start,
			final int end,
			final Container cont,
			final Class validSlot,
			final Class invalidSlot,
			final int maxItems) {
		TransferItem(iStack, inv, start, end, cont, validSlot, invalidSlot, maxItems, TRANSFER_TYPE.OTHER, false);
	}

	public static void TransferItem(final ItemStack iStack,
			final IInventory inv,
			int start,
			int end,
			final Container cont,
			final Class validSlot,
			final Class invalidSlot,
			int maxItems,
			final TRANSFER_TYPE type,
			final boolean fake) {
		start = Math.max(0, start);
		end = Math.min(inv.getSizeInventory() - 1, end);
		int startEmpty = start;
		int startOccupied = start;
		int pos;
		do {
			pos = -1;
			for (int i = startEmpty; i <= end; ++i) {
				if (isSlotOfType(cont.getSlot(i), validSlot) && (invalidSlot == null || !isSlotOfType(cont.getSlot(i), invalidSlot)) && inv.getStackInSlot(i) != null && inv.getStackInSlot(i).getItem() == iStack.getItem() && inv.getStackInSlot(i).isStackable() && inv.getStackInSlot(i).stackSize < inv.getStackInSlot(i).getMaxStackSize() && inv.getStackInSlot(i).stackSize < cont.getSlot(i).getSlotStackLimit() && inv.getStackInSlot(i).stackSize > 0 && iStack.stackSize > 0 && (!inv.getStackInSlot(i).getHasSubtypes() || inv.getStackInSlot(i).getItemDamage() == iStack.getItemDamage()) && (inv.getStackInSlot(i).getTagCompound() == null || inv.getStackInSlot(i).getTagCompound().equals(iStack.getTagCompound()))) {
					pos = i;
					startEmpty = pos + 1;
					break;
				}
			}
			if (pos == -1) {
				for (int i = startOccupied; i <= end; ++i) {
					if (isSlotOfType(cont.getSlot(i), validSlot) && (invalidSlot == null || !isSlotOfType(cont.getSlot(i), invalidSlot))) {
						final Slot slot = cont.getSlot(i);
						if (isItemValidForTransfer(slot, iStack, type) && inv.getStackInSlot(i) == null) {
							pos = i;
							startOccupied = pos + 1;
							break;
						}
					}
				}
			}
			if (pos != -1) {
				ItemStack existingItem = null;
				if (inv.getStackInSlot(pos) == null) {
					final ItemStack clone = iStack.copy();
					clone.stackSize = 0;
					if (!fake) {
						inv.setInventorySlotContents(pos, clone);
					}
					existingItem = clone;
				} else {
					existingItem = inv.getStackInSlot(pos);
				}
				int stackSize = iStack.stackSize;
				if (stackSize > existingItem.getMaxStackSize() - existingItem.stackSize) {
					stackSize = existingItem.getMaxStackSize() - existingItem.stackSize;
				}
				if (stackSize > cont.getSlot(pos).getSlotStackLimit() - existingItem.stackSize) {
					stackSize = cont.getSlot(pos).getSlotStackLimit() - existingItem.stackSize;
				}
				boolean killMe = false;
				if (maxItems != -1) {
					if (stackSize > maxItems) {
						stackSize = maxItems;
						killMe = true;
					}
					maxItems -= stackSize;
				}
				if (stackSize <= 0) {
					pos = -1;
				} else {
					iStack.stackSize -= stackSize;
					if (!fake) {
						final ItemStack stackInSlot = inv.getStackInSlot(pos);
						stackInSlot.stackSize += stackSize;
					}
					if (iStack.stackSize != 0 && !killMe && maxItems != 0) {
						continue;
					}
					pos = -1;
				}
			}
		} while (pos != -1);
		if (!fake) {
			inv.markDirty();
		}
	}

	public enum TRANSFER_TYPE {
		SHIFT,
		MANAGER,
		OTHER
	}
}
