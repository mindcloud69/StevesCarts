package vswe.stevescarts.containers.slots;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import vswe.stevescarts.helpers.storages.TransferHandler;

import javax.annotation.Nonnull;

public abstract class SlotFake extends SlotBase implements ISpecialItemTransferValidator {
	public SlotFake(final IInventory iinventory, final int i, final int j, final int k) {
		super(iinventory, i, j, k);
	}

	@Override
	public int getSlotStackLimit() {
		return 0;
	}

	@Override
	public ItemStack onTake(final EntityPlayer par1EntityPlayer,
	                             @Nonnull
		                             ItemStack par2ItemStack) {
		super.onTake(par1EntityPlayer, par2ItemStack);
		if (par2ItemStack != null && par1EntityPlayer != null && par1EntityPlayer.inventory != null) {
			par1EntityPlayer.inventory.setItemStack(null);
		}
		return ItemStack.EMPTY;
	}

	@Override
	public boolean isItemValidForTransfer(
		@Nonnull
			ItemStack item, final TransferHandler.TRANSFER_TYPE type) {
		return false;
	}
}
