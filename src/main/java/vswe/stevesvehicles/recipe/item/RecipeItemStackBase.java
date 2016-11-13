package vswe.stevesvehicles.recipe.item;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;


public abstract class RecipeItemStackBase extends RecipeItem {
	protected abstract ItemStack getItemStack();

	@Override
	public boolean matches(ItemStack other) {
		ItemStack item = getItemStack();
		return other != null && item.getItem() == other.getItem() && (item.getItemDamage() == OreDictionary.WILDCARD_VALUE || item.getItemDamage() == other.getItemDamage());
	}

	@Override
	public List<ItemStack> getVisualStacks() {
		List<ItemStack> items = new ArrayList<>();
		items.add(getItemStack());
		return items;
	}
}
