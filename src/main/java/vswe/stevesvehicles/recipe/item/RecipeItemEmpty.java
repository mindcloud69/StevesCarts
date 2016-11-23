package vswe.stevesvehicles.recipe.item;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public class RecipeItemEmpty extends RecipeItem {
	@Override
	public boolean matches(ItemStack other) {
		return other == null;
	}

	@Override
	public boolean isEmpty() {
		return true;
	}

	@Override
	public NonNullList<ItemStack> getVisualStacks() {
		return NonNullList.withSize(1, ItemStack.EMPTY);
	}
}
