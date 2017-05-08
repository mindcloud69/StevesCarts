package vswe.stevescarts.compat.minecraft;

import vswe.stevescarts.api.ISCHelpers;
import vswe.stevescarts.api.ISCPlugin;

//This is registered last to allow other mods to take control
public class CompatMinecraft implements ISCPlugin {

	@Override
	public void loadAddons(ISCHelpers plugins) {
		plugins.registerTree(new DefaultTreeModule());
		plugins.registerCrop(new DefaultCropModule());
	}
}
