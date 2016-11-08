package vswe.stevescarts.Modules.Hull;

import vswe.stevescarts.Carts.MinecartModular;

public class ModuleStandard extends ModuleHull {
	public ModuleStandard(final MinecartModular cart) {
		super(cart);
	}

	@Override
	public int getConsumption(final boolean isMoving) {
		if (!isMoving) {
			return super.getConsumption(false);
		}
		return 1;
	}
}
