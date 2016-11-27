package stevesvehicles.common.recipe.jei;

import mezz.jei.api.BlankModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;

@JEIPlugin
public class JeiPlugin extends BlankModPlugin {
	@Override
	public void register(IModRegistry registry) {
		registry.addRecipeHandlers(new ModuleRecipeHandler(registry.getJeiHelpers()));
		// registry.addRecipes(CraftingManager.getInstance().getRecipeList());
	}
}
