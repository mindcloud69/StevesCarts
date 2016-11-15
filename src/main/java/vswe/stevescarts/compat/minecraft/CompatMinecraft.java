package vswe.stevescarts.compat.minecraft;

import vswe.stevescarts.api.ISCHelpers;
import vswe.stevescarts.api.ISCPlugin;
import vswe.stevescarts.api.SCLoadingPlugin;

@SCLoadingPlugin
public class CompatMinecraft implements ISCPlugin {

	@Override
	public void loadAddons(ISCHelpers plugins) {
		plugins.registerTree(new DefaultTreeModule());
		plugins.registerCrop(new DefaultCropModule());
	}
}
