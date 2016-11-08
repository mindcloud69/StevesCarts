package vswe.stevescarts.modules.storages.tanks;

import vswe.stevescarts.entitys.MinecartModular;

public class ModuleSideTanks extends ModuleTank {
	public ModuleSideTanks(final MinecartModular cart) {
		super(cart);
	}

	@Override
	protected int getTankSize() {
		return 8000;
	}
}
