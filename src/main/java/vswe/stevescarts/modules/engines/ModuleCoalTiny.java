package vswe.stevescarts.modules.engines;

import vswe.stevescarts.entitys.MinecartModular;

public class ModuleCoalTiny extends ModuleCoalBase {
	public ModuleCoalTiny(final MinecartModular cart) {
		super(cart);
	}

	@Override
	protected int getInventoryWidth() {
		return 1;
	}

	@Override
	public double getFuelMultiplier() {
		return 0.5;
	}
}
