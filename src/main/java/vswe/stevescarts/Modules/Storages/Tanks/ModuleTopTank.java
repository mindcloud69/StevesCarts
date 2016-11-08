package vswe.stevescarts.Modules.Storages.Tanks;

import vswe.stevescarts.Carts.MinecartModular;

public class ModuleTopTank extends ModuleTank {
	public ModuleTopTank(final MinecartModular cart) {
		super(cart);
	}

	@Override
	protected int getTankSize() {
		return 14000;
	}
}
