package vswe.stevesvehicles.recipe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import vswe.stevesvehicles.recipe.item.RecipeItem;

public class ModuleRecipeShapeless extends ModuleRecipe {
	public ModuleRecipeShapeless(IRecipeOutput result, int count, Object[] recipe) {
		super(result, count, recipe);
	}

	public ModuleRecipeShapeless(IRecipeOutput result, Object[] recipe) {
		super(result, recipe);
	}

	@Override
	public boolean matches(InventoryCrafting crafting, World world) {
		List<RecipeItem> remainingRecipe = new ArrayList<>();
		Collections.addAll(remainingRecipe, recipe);
		for (int i = 0; i < GRID_WIDTH; i++) {
			for (int j = 0; j < GRID_HEIGHT; j++) {
				ItemStack item = crafting.getStackInRowAndColumn(i, j);
				if (!item.func_190926_b()) {
					boolean foundMatch = false;
					for (RecipeItem recipeItem : remainingRecipe) {
						if (recipeItem.matches(item)) {
							remainingRecipe.remove(recipeItem);
							foundMatch = true;
							break;
						}
					}
					if (!foundMatch) {
						return false;
					}
				}
			}
		}
		return remainingRecipe.isEmpty();
	}
}
