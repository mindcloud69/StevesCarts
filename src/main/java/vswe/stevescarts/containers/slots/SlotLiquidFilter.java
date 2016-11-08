package vswe.stevescarts.containers.slots;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidContainerRegistry;
import vswe.stevescarts.helpers.storages.TransferHandler;

public class SlotLiquidFilter extends SlotBase implements ISpecialItemTransferValidator {
	public SlotLiquidFilter(final IInventory iinventory, final int i, final int j, final int k) {
		super(iinventory, i, j, k);
	}

	@Override
	public boolean isItemValidForTransfer(final ItemStack item, final TransferHandler.TRANSFER_TYPE type) {
		return false;
	}

	@Override
	public boolean isItemValid(final ItemStack itemstack) {
		return isItemStackValid(itemstack);
	}

	public static boolean isItemStackValid(final ItemStack itemstack) {
		return FluidContainerRegistry.isFilledContainer(itemstack);
	}

	@Override
	public int getSlotStackLimit() {
		return 1;
	}
}
