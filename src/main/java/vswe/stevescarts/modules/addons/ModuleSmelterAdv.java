package vswe.stevescarts.modules.addons;

import vswe.stevescarts.entitys.MinecartModular;

public class ModuleSmelterAdv extends ModuleSmelter {
	public ModuleSmelterAdv(final MinecartModular cart) {
		super(cart);
	}

	@Override
	protected boolean canUseAdvancedFeatures() {
		return true;
	}
}
