package vswe.stevescarts.containers.slots;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import vswe.stevescarts.helpers.storages.TransferHandler;

public class SlotCartCrafterResult extends SlotBase implements ISpecialItemTransferValidator {
	public SlotCartCrafterResult(final IInventory iinventory, final int i, final int j, final int k) {
		super(iinventory, i, j, k);
	}

	@Override
	public boolean isItemValid(final ItemStack itemstack) {
		return false;
	}

	@Override
	public boolean canTakeStack(final EntityPlayer par1EntityPlayer) {
		return false;
	}

	@Override
	public boolean isItemValidForTransfer(final ItemStack item, final TransferHandler.TRANSFER_TYPE type) {
		return false;
	}

	@Override
	public int getSlotStackLimit() {
		return 0;
	}
}
