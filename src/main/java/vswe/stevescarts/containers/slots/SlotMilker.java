package vswe.stevescarts.containers.slots;

import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public class SlotMilker extends SlotBase {
	public SlotMilker(final IInventory iinventory, final int i, final int j, final int k) {
		super(iinventory, i, j, k);
	}

	@Override
	public boolean isItemValid(
		@Nonnull
			ItemStack itemstack) {
		return itemstack != null && itemstack.getItem() == Items.BUCKET;
	}
}
