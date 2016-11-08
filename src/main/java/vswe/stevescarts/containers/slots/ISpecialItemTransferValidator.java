package vswe.stevescarts.containers.slots;

import net.minecraft.item.ItemStack;
import vswe.stevescarts.helpers.storages.TransferHandler;

public interface ISpecialItemTransferValidator {
	boolean isItemValidForTransfer(final ItemStack p0, final TransferHandler.TRANSFER_TYPE p1);
}
