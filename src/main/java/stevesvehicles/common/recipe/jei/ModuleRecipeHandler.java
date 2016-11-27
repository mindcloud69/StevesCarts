package stevesvehicles.common.recipe.jei;

import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.recipe.IRecipeHandler;
import mezz.jei.api.recipe.IRecipeWrapper;
import mezz.jei.api.recipe.VanillaRecipeCategoryUid;
import mezz.jei.util.ErrorUtil;
import mezz.jei.util.Log;
import stevesvehicles.common.recipe.ModuleRecipe;
import stevesvehicles.common.recipe.ShapedModuleRecipe;
import stevesvehicles.common.recipe.ShapelessModuleRecipe;
import stevesvehicles.common.recipe.item.RecipeItem;

public class ModuleRecipeHandler implements IRecipeHandler<ModuleRecipe> {
	private final IJeiHelpers jeiHelpers;

	public ModuleRecipeHandler(IJeiHelpers jeiHelpers) {
		this.jeiHelpers = jeiHelpers;
	}

	@Override
	public Class<ModuleRecipe> getRecipeClass() {
		return ModuleRecipe.class;
	}

	@Override
	public String getRecipeCategoryUid(ModuleRecipe recipe) {
		return VanillaRecipeCategoryUid.CRAFTING;
	}

	@Override
	public IRecipeWrapper getRecipeWrapper(ModuleRecipe recipe) {
		if (recipe instanceof ShapelessModuleRecipe) {
			return new ShapelessModuleRecipeWrapper(jeiHelpers, (ShapelessModuleRecipe) recipe);
		} else {
			return new ShapedModuleRecipeWrapper(jeiHelpers, (ShapedModuleRecipe) recipe);
		}
	}

	@Override
	public boolean isRecipeValid(ModuleRecipe recipe) {
		if (recipe.getRecipeOutput() == null) {
			String recipeInfo = ErrorUtil.getInfoFromBrokenRecipe(recipe, this);
			Log.error("Recipe has no output. {}", recipeInfo);
			return false;
		}
		int inputCount = 0;
		for (RecipeItem input : recipe.getRecipeItems()) {
			if (input != null) {
				inputCount++;
			}
		}
		if (inputCount > 9) {
			String recipeInfo = ErrorUtil.getInfoFromBrokenRecipe(recipe, this);
			Log.error("Recipe has too many inputs. {}", recipeInfo);
			return false;
		}
		if (inputCount == 0) {
			String recipeInfo = ErrorUtil.getInfoFromBrokenRecipe(recipe, this);
			Log.error("Recipe has no inputs. {}", recipeInfo);
			return false;
		}
		return true;
	}
}
