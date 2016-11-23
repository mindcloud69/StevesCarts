package vswe.stevesvehicles.block;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import vswe.stevesvehicles.item.ModItems;
import vswe.stevesvehicles.recipe.IRecipeOutput;
import vswe.stevesvehicles.recipe.ShapedModuleRecipe;

public class StorageBlock implements IRecipeOutput {
	private int id;
	private String name;
	private ItemStack item;

	public StorageBlock(int id, String name, ItemStack item) {
		this.id = id;
		this.name = name;
		this.item = item;
	}

	public String getName() {
		return name;
	}

	public void loadRecipe() {
		// compress
		Object[] items = new ItemStack[9];
		for (int i = 0; i < items.length; i++) {
			items[i] = item;
		}
		GameRegistry.addRecipe(new ShapedModuleRecipe(this, 3, 3, items));
		// restore
		GameRegistry.addRecipe(new ShapedModuleRecipe(new IRecipeOutput() {
			@Override
			public ItemStack getItemStack() {
				ItemStack result = item.copy();
				result.setCount(9);
				return result;
			}
		}, 1, 1, new Object[] { getItemStack() }));
	}

	@Override
	public ItemStack getItemStack() {
		return new ItemStack(ModItems.storage, 1, id);
	}
}
