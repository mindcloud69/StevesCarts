package vswe.stevescarts.guis.buttons;

import vswe.stevescarts.modules.workers.ModuleComputer;

public abstract class ButtonAssembly extends ButtonBase {
	public ButtonAssembly(final ModuleComputer module, final LOCATION loc) {
		super(module, loc);
	}

	@Override
	public boolean isVisible() {
		return !((ModuleComputer) this.module).isWriting();
	}
}
