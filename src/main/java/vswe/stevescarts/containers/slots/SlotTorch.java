package vswe.stevescarts.containers.slots;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public class SlotTorch extends SlotBase {
	public SlotTorch(final IInventory iinventory, final int i, final int j, final int k) {
		super(iinventory, i, j, k);
	}

	@Override
	public boolean isItemValid(
		@Nonnull
			ItemStack itemstack) {
		return Block.getBlockFromItem(itemstack.getItem()) == Blocks.TORCH;
	}
}
