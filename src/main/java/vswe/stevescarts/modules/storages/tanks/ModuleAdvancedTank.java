package vswe.stevescarts.modules.storages.tanks;

import vswe.stevescarts.entitys.MinecartModular;

public class ModuleAdvancedTank extends ModuleTank {
	public ModuleAdvancedTank(final MinecartModular cart) {
		super(cart);
	}

	@Override
	protected int getTankSize() {
		return 32000;
	}
}
