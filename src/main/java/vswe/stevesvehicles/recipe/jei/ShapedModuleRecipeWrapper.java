package vswe.stevesvehicles.recipe.jei;

import java.util.Arrays;
import java.util.List;

import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.BlankRecipeWrapper;
import mezz.jei.api.recipe.IStackHelper;
import mezz.jei.api.recipe.wrapper.IShapedCraftingRecipeWrapper;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.oredict.ShapedOreRecipe;
import vswe.stevesvehicles.recipe.ShapedModuleRecipe;

public class ShapedModuleRecipeWrapper extends BlankRecipeWrapper implements IShapedCraftingRecipeWrapper {
	private final IJeiHelpers jeiHelpers;
	private final ShapedModuleRecipe recipe;
	private final int width;
	private final int height;

	public ShapedModuleRecipeWrapper(IJeiHelpers jeiHelpers, ShapedModuleRecipe recipe) {
		this.jeiHelpers = jeiHelpers;
		this.recipe = recipe;
		this.width = recipe.getFullWidth();
		this.height = recipe.getFullWidth();
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

	@Override
	public int getWidth() {
		return width;
	}

	@Override
	public int getHeight() {
		return height;
	}

}
