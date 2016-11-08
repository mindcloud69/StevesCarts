package vswe.stevescarts.Slots;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import vswe.stevescarts.Helpers.TransferHandler;

public abstract class SlotFake extends SlotBase implements ISpecialItemTransferValidator {
	public SlotFake(final IInventory iinventory, final int i, final int j, final int k) {
		super(iinventory, i, j, k);
	}

	public int getSlotStackLimit() {
		return 0;
	}

	public void onPickupFromSlot(final EntityPlayer par1EntityPlayer, final ItemStack par2ItemStack) {
		super.onPickupFromSlot(par1EntityPlayer, par2ItemStack);
		if (par2ItemStack != null && par1EntityPlayer != null && par1EntityPlayer.inventory != null) {
			par1EntityPlayer.inventory.setItemStack(null);
		}
	}

	@Override
	public boolean isItemValidForTransfer(final ItemStack item, final TransferHandler.TRANSFER_TYPE type) {
		return false;
	}
}
