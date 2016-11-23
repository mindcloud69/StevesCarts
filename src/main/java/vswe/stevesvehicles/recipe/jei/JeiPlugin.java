package vswe.stevesvehicles.recipe.jei;

import mezz.jei.api.BlankModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.plugins.vanilla.crafting.CraftingRecipeCategory;
import net.minecraft.item.crafting.CraftingManager;

@JEIPlugin
public class JeiPlugin extends BlankModPlugin {
	
	@Override
	public void register(IModRegistry registry) {
		registry.addRecipeHandlers(new ModuleRecipeHandler(registry.getJeiHelpers()));
		registry.addRecipes(CraftingManager.getInstance().getRecipeList());
	}
}
