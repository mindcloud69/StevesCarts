package vswe.stevescarts.containers.slots;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidContainerRegistry;
import vswe.stevescarts.helpers.storages.TransferHandler;

public class SlotLiquidOutput extends SlotBase implements ISpecialItemTransferValidator {
	public SlotLiquidOutput(final IInventory iinventory, final int i, final int j, final int k) {
		super(iinventory, i, j, k);
	}

	@Override
	public boolean isItemValid(final ItemStack itemstack) {
		return isItemStackValid(itemstack);
	}

	@Override
	public boolean isItemValidForTransfer(final ItemStack item, final TransferHandler.TRANSFER_TYPE type) {
		return type == TransferHandler.TRANSFER_TYPE.OTHER && FluidContainerRegistry.isContainer(item);
	}

	public static boolean isItemStackValid(final ItemStack itemstack) {
		return false;
	}
}
