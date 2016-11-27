package stevesvehicles.common.recipe.item;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public class RecipeItemCluster extends RecipeItem {
	private RecipeItem[] elements;

	public RecipeItemCluster(Object[] elements) {
		this.elements = new RecipeItem[elements.length];
		for (int i = 0; i < elements.length; i++) {
			this.elements[i] = createRecipeItem(elements[i]);
		}
	}

	public RecipeItemCluster(NonNullList<ItemStack> elements) {
		this.elements = new RecipeItem[elements.size()];
		for (int i = 0; i < elements.size(); i++) {
			this.elements[i] = createRecipeItem(elements.get(i));
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
	public NonNullList<ItemStack> getVisualStacks() {
		NonNullList<ItemStack> items = NonNullList.create();
		for (RecipeItem element : elements) {
			List<ItemStack> elementItems = element.getVisualStacks();
			if (elementItems != null) {
				items.addAll(elementItems);
			}
		}
		return items;
	}

	public NonNullList<Object> getAdvancedVisualStacks() {
		NonNullList<Object> items = NonNullList.withSize(9, ItemStack.EMPTY);
		for (int i = 0; i < elements.length; i++) {
			RecipeItem element = elements[i];
			List<ItemStack> elementItems = element.getVisualStacks();
			if (elementItems != null) {
				int size = elementItems.size();
				if (size > 1) {
					items.set(i, elementItems);
				} else {
					items.set(i, elementItems.get(0));
				}
			}
		}
		return items;
	}
}
