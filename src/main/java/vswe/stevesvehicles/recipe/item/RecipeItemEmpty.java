package vswe.stevesvehicles.recipe.item;

import java.util.List;

import net.minecraft.item.ItemStack;

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
	public List<ItemStack> getVisualStacks() {
		return null;
	}
}
