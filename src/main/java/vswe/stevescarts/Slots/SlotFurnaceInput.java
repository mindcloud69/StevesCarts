package vswe.stevescarts.Slots;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;

public class SlotFurnaceInput extends SlotFake {
	public SlotFurnaceInput(final IInventory iinventory, final int i, final int j, final int k) {
		super(iinventory, i, j, k);
	}

	public boolean isItemValid(final ItemStack itemstack) {
		return FurnaceRecipes.instance().getSmeltingResult(itemstack) != null;
	}
}
