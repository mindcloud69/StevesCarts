package vswe.stevescarts.Modules.Storages.Chests;

import vswe.stevescarts.Carts.MinecartModular;

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
