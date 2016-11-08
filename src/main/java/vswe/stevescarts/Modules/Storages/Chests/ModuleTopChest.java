package vswe.stevescarts.modules.storages.chests;

import vswe.stevescarts.entitys.MinecartModular;

public class ModuleTopChest extends ModuleChest {
	public ModuleTopChest(final MinecartModular cart) {
		super(cart);
	}

	@Override
	protected int getInventoryWidth() {
		return 6;
	}

	@Override
	protected int getInventoryHeight() {
		return 3;
	}
}
