package vswe.stevescarts.modules.engines;

import vswe.stevescarts.entitys.MinecartModular;

public class ModuleThermalStandard extends ModuleThermalBase {
	public ModuleThermalStandard(final MinecartModular cart) {
		super(cart);
	}

	@Override
	protected int getEfficiency() {
		return 25;
	}

	@Override
	protected int getCoolantEfficiency() {
		return 0;
	}
}
