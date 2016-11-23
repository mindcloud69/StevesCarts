package vswe.stevesvehicles.recipe;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.ForgeHooks;
import vswe.stevesvehicles.recipe.item.RecipeItem;
import vswe.stevesvehicles.recipe.item.RecipeItemCluster;

public abstract class ModuleRecipe implements IRecipe {
	private IRecipeOutput result;
	private int count;
	protected RecipeItem[] recipe;
	protected static final int GRID_WIDTH = 3;
	protected static final int GRID_HEIGHT = 3;

	public ModuleRecipe(IRecipeOutput result, Object[] recipe) {
		this(result, 1, recipe);
	}

	public ModuleRecipe(IRecipeOutput result, int count, Object[] recipe) {
		this.result = result;
		this.count = count;
		this.recipe = new RecipeItem[recipe.length];
		for (int i = 0; i < recipe.length; i++) {
			this.recipe[i] = RecipeItem.createRecipeItem(recipe[i]);
		}
	}

	@Override
	public ItemStack getCraftingResult(InventoryCrafting inventory) {
		return getResult();
	}

	@Override
	public int getRecipeSize() {
		return recipe.length;
	}

	@Override
	public ItemStack getRecipeOutput() {
		return getResult();
	}

	private ItemStack getResult() {
		ItemStack item = result.getItemStack();
		if (item != null) {
			item.setCount(item.getCount() * count);
		}
		return item;
	}

	public RecipeItem[] getRecipeItems() {
		return recipe;
	}
	
	public RecipeItemCluster toCluster(){
		return new RecipeItemCluster(recipe);
	}

	@Override
	public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inv) {
		return ForgeHooks.defaultRecipeGetRemainingItems(inv);
	}
}
