package vswe.stevescarts.containers.slots;

import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public class SlotWater extends SlotBase {
	public SlotWater(final IInventory iinventory, final int i, final int j, final int k) {
		super(iinventory, i, j, k);
	}

	@Override
	public int getSlotStackLimit() {
		return 1;
	}

	@Override
	public boolean isItemValid(
		@Nonnull
			ItemStack itemstack) {
		return itemstack.getItem() == Items.WATER_BUCKET;
	}
}
