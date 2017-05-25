package vswe.stevescarts.compat.ic2;

import vswe.stevescarts.api.ISCHelpers;
import vswe.stevescarts.api.ISCPlugin;
import vswe.stevescarts.api.SCLoadingPlugin;

/**
 * Created by modmuss50 on 08/05/2017.
 */
@SCLoadingPlugin(dependentMod = "IC2")
public class CompatIC2 implements ISCPlugin {

	@Override
	public void loadAddons(ISCHelpers plugins) {
		plugins.registerTree(new IC2RubberTreeModule());
	}
}
