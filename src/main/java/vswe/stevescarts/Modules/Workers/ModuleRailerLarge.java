package vswe.stevescarts.modules.workers;

import vswe.stevescarts.entitys.MinecartModular;

public class ModuleRailerLarge extends ModuleRailer {
	public ModuleRailerLarge(final MinecartModular cart) {
		super(cart);
	}

	@Override
	protected int getInventoryHeight() {
		return 2;
	}
}
