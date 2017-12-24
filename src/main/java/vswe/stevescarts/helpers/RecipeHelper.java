package vswe.stevescarts.helpers;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemEnchantedBook;
import net.minecraft.item.ItemStack;
import reborncore.common.util.RebornCraftingHelper;
import vswe.stevescarts.items.ModItems;

import javax.annotation.Nonnull;
import java.util.ArrayList;

public final class RecipeHelper {
	public static void addRecipe(
		@Nonnull
			ItemStack item, final Object[][] recipe) {

		if (recipe != null && !item.isEmpty()) {
			if (item.getItem() == ModItems.component && !ModItems.component.isValid(item)) {
				return;
			}

			ArrayList<Object> usedItems = new ArrayList<Object>();
			String chars = "ABCDEFGHI";
			String[] parts = new String[recipe.length];
			boolean isOreDict = false;
			boolean isSpecial = false;
			ItemStack[] items = new ItemStack[recipe.length * recipe[0].length];
			for (int i = 0; i < recipe.length; i++) {
				parts[i] = "";
				for (int j = 0; j < recipe[i].length; j++) {
					Object obj = recipe[i][j];

					boolean valid = true;
					if (obj instanceof Item) {
						obj = new ItemStack((Item) obj, 1);
					} else if (obj instanceof Block) {
						obj = new ItemStack((Block) obj, 1);
					} else if (obj instanceof ItemStack) {
						obj = ((ItemStack) obj).copy();
						((ItemStack) obj).setCount(1);
						if (!((ItemStack) obj).isEmpty() && ((ItemStack) obj).getItem() instanceof ItemEnchantedBook) {
							isSpecial = true;
						}
					} else if (obj instanceof String) {
						isOreDict = true;
					} else {
						valid = false;
					}

					if (obj instanceof ItemStack) {
						items[j + i * recipe[i].length] = (ItemStack) obj;
					}
					char myChar;
					if (valid) {
						int ind = -1;
						for (int k = 0; k < usedItems.size(); k++) {
							if ((usedItems.get(k) instanceof ItemStack &&
								obj instanceof ItemStack &&
								((ItemStack) usedItems.get(k)).isItemEqual((ItemStack) obj))
								||
								(usedItems.get(k) instanceof String &&
									obj instanceof String &&
									usedItems.get(k).equals(obj))
								) {
								ind = k;
								break;
							}
						}

						if (ind == -1) {
							usedItems.add(obj);
							ind = usedItems.size() - 1;
						}

						myChar = chars.charAt(ind);

					} else {
						myChar = ' ';
					}

					parts[i] += myChar;
				}
			}

			Object[] finalRecipe = new Object[parts.length + usedItems.size() * 2];
			System.arraycopy(parts, 0, finalRecipe, 0, parts.length);
			for (int i = 0; i < usedItems.size(); i++) {
				finalRecipe[parts.length + i * 2] = chars.charAt(i);
				finalRecipe[parts.length + i * 2 + 1] = usedItems.get(i);
			}
			if (isSpecial) {
				//GameRegistry.addRecipe(new ShapedRecipes2(recipe[0].length, recipe.length, items, item));
			} else if (isOreDict) {
				RebornCraftingHelper.addShapedRecipe(item, finalRecipe);
			} else {
				RebornCraftingHelper.addShapedOreRecipe(item, finalRecipe);
			}

		}
	}
}
