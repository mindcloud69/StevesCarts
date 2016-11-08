package vswe.stevescarts.modules.storages.tanks;

import vswe.stevescarts.entitys.MinecartModular;

public class ModuleFrontTank extends ModuleTank {
	public ModuleFrontTank(final MinecartModular cart) {
		super(cart);
	}

	@Override
	protected int getTankSize() {
		return 8000;
	}
}
