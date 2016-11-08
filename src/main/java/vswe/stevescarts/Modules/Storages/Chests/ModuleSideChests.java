package vswe.stevescarts.modules.storages.chests;

import vswe.stevescarts.entitys.MinecartModular;

public class ModuleSideChests extends ModuleChest {
	public ModuleSideChests(final MinecartModular cart) {
		super(cart);
	}

	@Override
	protected int getInventoryWidth() {
		return 5;
	}

	@Override
	protected int getInventoryHeight() {
		return 3;
	}
}
