package stevesvehicles.common.recipe.jei;

import java.util.List;

import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.BlankRecipeWrapper;
import mezz.jei.api.recipe.IStackHelper;
import mezz.jei.api.recipe.wrapper.ICraftingRecipeWrapper;
import net.minecraft.item.ItemStack;
import stevesvehicles.common.recipe.ShapelessModuleRecipe;

public class ShapelessModuleRecipeWrapper extends BlankRecipeWrapper implements ICraftingRecipeWrapper {
	private final IJeiHelpers jeiHelpers;
	private final ShapelessModuleRecipe recipe;

	public ShapelessModuleRecipeWrapper(IJeiHelpers jeiHelpers, ShapelessModuleRecipe recipe) {
		this.jeiHelpers = jeiHelpers;
		this.recipe = recipe;
	}

	@Override
	public void getIngredients(IIngredients ingredients) {
		IStackHelper stackHelper = jeiHelpers.getStackHelper();
		List<List<ItemStack>> inputs = stackHelper.expandRecipeItemStackInputs(recipe.toCluster().getAdvancedVisualStacks());
		ingredients.setInputLists(ItemStack.class, inputs);
		ItemStack recipeOutput = recipe.getRecipeOutput();
		if (recipeOutput != null) {
			ingredients.setOutput(ItemStack.class, recipeOutput);
		}
	}
}
