package vswe.stevescarts.modules.addons;

import vswe.stevescarts.entitys.MinecartModular;

public class ModuleCrafterAdv extends ModuleCrafter {
	public ModuleCrafterAdv(final MinecartModular cart) {
		super(cart);
	}

	@Override
	protected boolean canUseAdvancedFeatures() {
		return true;
	}
}
