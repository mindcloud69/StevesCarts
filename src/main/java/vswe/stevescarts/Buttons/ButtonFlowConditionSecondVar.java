package vswe.stevescarts.Buttons;

import vswe.stevescarts.Computer.ComputerTask;
import vswe.stevescarts.Modules.Workers.ModuleComputer;

public class ButtonFlowConditionSecondVar extends ButtonFlowConditionVar {
	public ButtonFlowConditionSecondVar(final ModuleComputer module, final LOCATION loc, final boolean increase) {
		super(module, loc, increase);
	}

	@Override
	public boolean isVisible() {
		if (((ModuleComputer) this.module).getSelectedTasks() != null) {
			for (final ComputerTask task : ((ModuleComputer) this.module).getSelectedTasks()) {
				if (!task.getFlowConditionUseSecondVar()) {
					return false;
				}
			}
		}
		return super.isVisible();
	}

	@Override
	protected int getIndex(final ComputerTask task) {
		return task.getFlowConditionSecondVarIndex();
	}

	@Override
	protected void setIndex(final ComputerTask task, final int val) {
		task.setFlowConditionSecondVar(val);
	}
}
