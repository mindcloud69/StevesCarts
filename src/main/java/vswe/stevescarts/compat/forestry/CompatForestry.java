package vswe.stevescarts.compat.forestry;

import vswe.stevescarts.api.ISCHelpers;
import vswe.stevescarts.api.ISCPlugin;
import vswe.stevescarts.api.SCLoadingPlugin;

@SCLoadingPlugin(dependentMod = "forestry")
public class CompatForestry implements ISCPlugin{

	@Override
	public void loadAddons(ISCHelpers plugins) {
		plugins.registerTree(new ForestryTreeModule());
	}
}
