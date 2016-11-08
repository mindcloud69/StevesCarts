package vswe.stevescarts.guis.buttons;

import vswe.stevescarts.computer.ComputerTask;
import vswe.stevescarts.modules.workers.ModuleComputer;

public abstract class ButtonFlowFor extends ButtonAssembly {
	public ButtonFlowFor(final ModuleComputer module, final LOCATION loc) {
		super(module, loc);
	}

	@Override
	public boolean isVisible() {
		if (!super.isVisible()) {
			return false;
		}
		if (((ModuleComputer) this.module).getSelectedTasks() != null && ((ModuleComputer) this.module).getSelectedTasks().size() > 0) {
			for (final ComputerTask task : ((ModuleComputer) this.module).getSelectedTasks()) {
				if (!task.isFlowFor()) {
					return false;
				}
			}
			return true;
		}
		return false;
	}
}
