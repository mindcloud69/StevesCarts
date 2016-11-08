package vswe.stevescarts.modules.hull;

import vswe.stevescarts.entitys.MinecartModular;

public class ModuleGalgadorian extends ModuleHull {
	public ModuleGalgadorian(final MinecartModular cart) {
		super(cart);
	}

	@Override
	public int getConsumption(final boolean isMoving) {
		if (!isMoving) {
			return super.getConsumption(false);
		}
		return 9;
	}
}
