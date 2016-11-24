package stevesvehicles.common.container.slots;

import net.minecraft.item.ItemStack;
import stevesvehicles.common.transfer.TransferHandler;

public interface ISpecialItemTransferValidator {
	public boolean isItemValidForTransfer(ItemStack item, TransferHandler.TransferType type);
}
