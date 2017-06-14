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
		if (recipe != null && item != null) {
			if (item.getItem() == ModItems.component && !ModItems.component.isValid(item)) {
				return;
			}
			final ArrayList<Object> usedItems = new ArrayList<>();
			final String chars = "ABCDEFGHI";
			final String[] parts = new String[recipe.length];
			boolean isOreDict = false;
			boolean isSpecial = false;
			@Nonnull
			ItemStack[] items = new ItemStack[recipe.length * recipe[0].length];
			for (int i = 0; i < recipe.length; ++i) {
				parts[i] = "";
				for (int j = 0; j < recipe[i].length; ++j) {
					Object obj = recipe[i][j];
					boolean valid = true;
					if (obj instanceof Item) {
						obj = new ItemStack((Item) obj, 1);
					} else if (obj instanceof Block) {
						obj = new ItemStack((Block) obj, 1);
					} else if (obj instanceof ItemStack) {
						obj = ((ItemStack) obj).copy();
						((ItemStack) obj).setCount(1);
						if (obj != null && ((ItemStack) obj).getItem() instanceof ItemEnchantedBook) {
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
						for (int k = 0; k < usedItems.size(); ++k) {
							if ((usedItems.get(k) instanceof ItemStack && obj instanceof ItemStack && ((ItemStack) usedItems.get(k)).isItemEqual((ItemStack) obj)) || (usedItems.get(k) instanceof String && obj instanceof String && usedItems.get(k).equals(obj))) {
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
					final StringBuilder sb = new StringBuilder();
					final String[] array = parts;
					final int n = i;
					array[n] = sb.append(array[n]).append(myChar).toString();
				}
			}
			final Object[] finalRecipe = new Object[parts.length + usedItems.size() * 2];
			System.arraycopy(parts, 0, finalRecipe, 0, parts.length);
			for (int l = 0; l < usedItems.size(); ++l) {
				finalRecipe[parts.length + l * 2] = chars.charAt(l);
				finalRecipe[parts.length + l * 2 + 1] = usedItems.get(l);
			}
			if (isSpecial) {
				//TODO 1.12 needs enchantment stuff
				RebornCraftingHelper.addShapedRecipe(item, finalRecipe);
			} else if (isOreDict) {
				RebornCraftingHelper.addShapedOreRecipe(item, finalRecipe);
			} else {
				RebornCraftingHelper.addShapedRecipe(item, finalRecipe);
			}
		}
	}
}
