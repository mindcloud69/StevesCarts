package vswe.stevesvehicles.recipe.item;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;

public class RecipeItemCluster extends RecipeItem {
	private RecipeItem[] elements;

	public RecipeItemCluster(Object[] elements) {
		this.elements = new RecipeItem[elements.length];
		for (int i = 0; i < elements.length; i++) {
			this.elements[i] = createRecipeItem(elements[i]);
		}
	}

	@Override
	public boolean matches(ItemStack other) {
		for (RecipeItem element : elements) {
			if (element != null && element.matches(other)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public List<ItemStack> getVisualStacks() {
		List<ItemStack> items = new ArrayList<>();
		for (RecipeItem element : elements) {
			List<ItemStack> elementItems = element.getVisualStacks();
			if (elementItems != null) {
				items.addAll(elementItems);
			}
		}
		return items;
	}
}
