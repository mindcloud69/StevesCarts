package vswe.stevescarts.Slots;

import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public class SlotFertilizer extends SlotBase {
	public SlotFertilizer(final IInventory iinventory, final int i, final int j, final int k) {
		super(iinventory, i, j, k);
	}

	public boolean isItemValid(final ItemStack itemstack) {
		return itemstack.getItem() == Items.BONE || (itemstack.getItem() == Items.DYE && itemstack.getItemDamage() == 15);
	}
}
