package vswe.stevescarts.helpers;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import reborncore.common.util.RebornCraftingHelper;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public final class RecipeHelper {
	public static void addRecipe(
		@Nonnull
			ItemStack item, final Object[][] recipe) {

		List<Object> objList = new ArrayList<>();
		Object[][] objArray = recipe;
		for (Object[] objects : objArray) {
			for (Object object : objects) {
				objList.add(object);
			}
		}
		NonNullList<Ingredient> ingredients = NonNullList.create();
		for (Object obj : objList) {
			Ingredient ingredient = CraftingHelper.getIngredient(obj);
			if (ingredient == null) {
				ingredient = Ingredient.EMPTY;
			}
			ingredients.add(ingredient);
		}
		ResourceLocation location = RebornCraftingHelper.getNameForRecipe(item);
		ShapedRecipes shapedRecipes = new ShapedRecipes(location.toString(), 3, 3, ingredients, item);
		shapedRecipes.setRegistryName(location);
		ForgeRegistries.RECIPES.register(shapedRecipes);
	}
}
