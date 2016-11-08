package vswe.stevescarts.Slots;

import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public class SlotWater extends SlotBase {
	public SlotWater(final IInventory iinventory, final int i, final int j, final int k) {
		super(iinventory, i, j, k);
	}

	public int getSlotStackLimit() {
		return 1;
	}

	public boolean isItemValid(final ItemStack itemstack) {
		return itemstack.getItem() == Items.WATER_BUCKET;
	}
}
