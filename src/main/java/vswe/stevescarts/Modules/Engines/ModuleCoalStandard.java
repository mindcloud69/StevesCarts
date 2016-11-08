package vswe.stevescarts.modules.engines;

import vswe.stevescarts.entitys.MinecartModular;

public class ModuleCoalStandard extends ModuleCoalBase {
	public ModuleCoalStandard(final MinecartModular cart) {
		super(cart);
	}

	@Override
	public double getFuelMultiplier() {
		return 2.25;
	}
}
