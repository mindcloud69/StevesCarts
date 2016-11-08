package vswe.stevescarts.modules.storages.tanks;

import vswe.stevescarts.entitys.MinecartModular;

public class ModuleTopTank extends ModuleTank {
	public ModuleTopTank(final MinecartModular cart) {
		super(cart);
	}

	@Override
	protected int getTankSize() {
		return 14000;
	}
}
