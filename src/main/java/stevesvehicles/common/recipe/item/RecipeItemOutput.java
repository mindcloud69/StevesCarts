package stevesvehicles.common.recipe.item;

import net.minecraft.item.ItemStack;
import stevesvehicles.common.recipe.IRecipeOutput;

public class RecipeItemOutput extends RecipeItemStackBase {
	private IRecipeOutput type;

	public RecipeItemOutput(IRecipeOutput type) {
		this.type = type;
	}

	@Override
	protected ItemStack getItemStack() {
		return type.getItemStack();
	}
}
