package vswe.stevescarts.modules.engines;

import vswe.stevescarts.entitys.MinecartModular;

public class ModuleSolarBasic extends ModuleSolarTop {
	public ModuleSolarBasic(final MinecartModular cart) {
		super(cart);
	}

	@Override
	protected int getPanelCount() {
		return 2;
	}

	@Override
	protected int getMaxCapacity() {
		return 100000;
	}

	@Override
	protected int getGenSpeed() {
		return 2;
	}
}
