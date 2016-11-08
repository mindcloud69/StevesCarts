package vswe.stevescarts.modules.engines;

import vswe.stevescarts.entitys.MinecartModular;

public class ModuleThermalAdvanced extends ModuleThermalBase {
	public ModuleThermalAdvanced(final MinecartModular cart) {
		super(cart);
	}

	@Override
	protected int getEfficiency() {
		return 60;
	}

	@Override
	protected int getCoolantEfficiency() {
		return 90;
	}
}
