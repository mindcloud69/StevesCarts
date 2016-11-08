package vswe.stevescarts.Buttons;

import vswe.stevescarts.Modules.Workers.ModuleComputer;

public abstract class ButtonAssembly extends ButtonBase {
	public ButtonAssembly(final ModuleComputer module, final LOCATION loc) {
		super(module, loc);
	}

	@Override
	public boolean isVisible() {
		return !((ModuleComputer) this.module).isWriting();
	}
}
