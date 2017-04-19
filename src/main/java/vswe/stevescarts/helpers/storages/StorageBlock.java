package vswe.stevescarts.helpers.storages;

import net.minecraft.item.ItemStack;
import vswe.stevescarts.blocks.ModBlocks;
import vswe.stevescarts.helpers.RecipeHelper;

import javax.annotation.Nonnull;

public class StorageBlock {
	private String name;
	@Nonnull
	private ItemStack item;

	public StorageBlock(final String name,
	                    @Nonnull
		                    ItemStack item) {
		this.name = name;
		this.item = item.copy();
		this.item.setCount(9);
	}

	public String getName() {
		return name;
	}

	public void loadRecipe(final int i) {
		@Nonnull
		ItemStack block = new ItemStack(ModBlocks.STORAGE.getBlock(), 1, i);
		RecipeHelper.addRecipe(block, new Object[][] { { item, item, item }, { item, item, item }, { item, item, item } });
		RecipeHelper.addRecipe(item, new Object[][] { { block } });
	}
}
