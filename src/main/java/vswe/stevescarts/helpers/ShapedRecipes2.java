package vswe.stevescarts.helpers;

import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemEnchantedBook;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class ShapedRecipes2 extends ShapedRecipes {
	public ShapedRecipes2(final int par1,
	                      final int par2,
	                      @Nonnull
		                      ItemStack[] par3ArrayOfItemStack,
	                      @Nonnull
		                      ItemStack par4ItemStack) {
		super(par1, par2, par3ArrayOfItemStack, par4ItemStack);
	}

	@Override
	public boolean matches(final InventoryCrafting par1InventoryCrafting, final World par2World) {
		for (int var3 = 0; var3 <= 3 - this.recipeWidth; ++var3) {
			for (int var4 = 0; var4 <= 3 - this.recipeHeight; ++var4) {
				if (this.checkMatch(par1InventoryCrafting, var3, var4, true)) {
					return true;
				}
				if (this.checkMatch(par1InventoryCrafting, var3, var4, false)) {
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
				ItemStack var9 = null;
				if (var7 >= 0 && var8 >= 0 && var7 < this.recipeWidth && var8 < this.recipeHeight) {
					if (par4) {
						var9 = this.recipeItems[this.recipeWidth - var7 - 1 + var8 * this.recipeWidth];
					} else {
						var9 = this.recipeItems[var7 + var8 * this.recipeWidth];
					}
				}
				@Nonnull
				ItemStack var10 = par1InventoryCrafting.getStackInRowAndColumn(var5, var6);
				if (!var10.isEmpty() || !var9.isEmpty()) {
					if ((var10.isEmpty() && !var9.isEmpty()) || (!var10.isEmpty() && var9 == null)) {
						return false;
					}
					if (var9.getItem() != var10.getItem()) {
						return false;
					}
					if (var9.getItemDamage() != -1 && var9.getItemDamage() != var10.getItemDamage()) {
						return false;
					}
					if (var9.getItem() instanceof ItemEnchantedBook && var10.getItem() instanceof ItemEnchantedBook && !Items.ENCHANTED_BOOK.getEnchantments(var9).equals(Items.ENCHANTED_BOOK.getEnchantments(var10))) {
						return false;
					}
				}
			}
		}
		return true;
	}
}
