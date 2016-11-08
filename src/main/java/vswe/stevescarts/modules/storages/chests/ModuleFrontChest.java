package vswe.stevescarts.modules.storages.chests;

import vswe.stevescarts.entitys.MinecartModular;

public class ModuleFrontChest extends ModuleChest {
	public ModuleFrontChest(final MinecartModular cart) {
		super(cart);
	}

	@Override
	protected int getInventoryWidth() {
		return 4;
	}

	@Override
	protected int getInventoryHeight() {
		return 3;
	}
}
