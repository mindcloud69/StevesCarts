package stevesvehicles.common.recipe.item;

import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.oredict.OreDictionary;

public abstract class RecipeItemStackBase extends RecipeItem {
	protected abstract ItemStack getItemStack();

	@Override
	public boolean matches(ItemStack other) {
		ItemStack item = getItemStack();
		return !other.isEmpty() && item.getItem() == other.getItem() && (item.getItemDamage() == OreDictionary.WILDCARD_VALUE || item.getItemDamage() == other.getItemDamage());
	}

	@Override
	public NonNullList<ItemStack> getVisualStacks() {
		NonNullList<ItemStack> items = NonNullList.create();
		items.add(getItemStack());
		return items;
	}
}
