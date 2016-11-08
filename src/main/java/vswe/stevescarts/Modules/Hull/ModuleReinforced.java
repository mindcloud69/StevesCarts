package vswe.stevescarts.modules.hull;

import vswe.stevescarts.entitys.MinecartModular;

public class ModuleReinforced extends ModuleHull {
	public ModuleReinforced(final MinecartModular cart) {
		super(cart);
	}

	@Override
	public int getConsumption(final boolean isMoving) {
		if (!isMoving) {
			return super.getConsumption(false);
		}
		return 3;
	}
}
