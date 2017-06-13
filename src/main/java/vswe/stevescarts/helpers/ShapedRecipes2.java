package vswe.stevescarts.helpers;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemEnchantedBook;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import reborncore.common.util.CraftingHelper;

import javax.annotation.Nonnull;

public class ShapedRecipes2 extends ShapedRecipes {

	public ShapedRecipes2(final int par1,
	                      final int par2,
		                      NonNullList<Ingredient> ingredientList,
	                      @Nonnull
		                      ItemStack par4ItemStack) {

		super("", par1, par2, ingredientList, par4ItemStack);
	}

	public static ShapedRecipes2 create(final int par1,
	                                    final int par2,
	                                    ItemStack[] stackList,
	                                    @Nonnull
		                                    ItemStack par4ItemStack){
		NonNullList<Ingredient> ingredientList = NonNullList.create();
		for(ItemStack stack : stackList){
			ingredientList.add(CraftingHelper.toIngredient(stack));
		}
		return new ShapedRecipes2(par1, par2, ingredientList, par4ItemStack);
	}

	@Override
	public boolean matches(final InventoryCrafting par1InventoryCrafting, final World par2World) {
		for (int var3 = 0; var3 <= 3 - recipeWidth; ++var3) {
			for (int var4 = 0; var4 <= 3 - recipeHeight; ++var4) {
				if (checkMatch(par1InventoryCrafting, var3, var4, true)) {
					return true;
				}
				if (checkMatch(par1InventoryCrafting, var3, var4, false)) {
					return true;
				}
			}
		}
		return false;
	}

	private boolean checkMatch(final InventoryCrafting par1InventoryCrafting, final int par2, final int par3, final boolean par4) {
		for (int var5 = 0; var5 < 3; ++var5) {
			for (int var6 = 0; var6 < 3; ++var6) {
				final int var7 = var5 - par2;
				final int var8 = var6 - par3;
				@Nonnull
				Ingredient var9 = Ingredient.EMPTY;
				if (var7 >= 0 && var8 >= 0 && var7 < recipeWidth && var8 < recipeHeight) {
					if (par4) {
						var9 = recipeItems.get(recipeWidth - var7 - 1 + var8 * recipeWidth);
					} else {
						var9 = recipeItems.get(var7 + var8 * recipeWidth);
					}
				}
				@Nonnull
				ItemStack var10 = par1InventoryCrafting.getStackInRowAndColumn(var5, var6);
				if(var9.getMatchingStacks().length <= 0){
					return false;
				}
				if (!var10.isEmpty() && !var9.getMatchingStacks()[0].isEmpty()) {
					if ((var10.isEmpty() && !var9.getMatchingStacks()[0].isEmpty()) || (!var10.isEmpty() && var9 == null)) {
						return false;
					}
					if (var9.getMatchingStacks()[0].getItem() != var10.getItem()) {
						return false;
					}
					if (var9.getMatchingStacks()[0].getItemDamage() != -1 && var9.getMatchingStacks()[0].getItemDamage() != var10.getItemDamage()) {
						return false;
					}
					if (var9.getMatchingStacks()[0].getItem() instanceof ItemEnchantedBook && var10.getItem() instanceof ItemEnchantedBook && !ItemEnchantedBook.getEnchantments(var9.getMatchingStacks()[0]).equals(ItemEnchantedBook.getEnchantments(var10))) {
						return false;
					}
				}
			}
		}
		return true;
	}
}
