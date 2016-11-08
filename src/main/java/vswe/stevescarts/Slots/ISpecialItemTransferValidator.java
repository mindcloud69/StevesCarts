package vswe.stevescarts.Slots;

import net.minecraft.item.ItemStack;
import vswe.stevescarts.Helpers.TransferHandler;

public interface ISpecialItemTransferValidator {
	boolean isItemValidForTransfer(final ItemStack p0, final TransferHandler.TRANSFER_TYPE p1);
}
