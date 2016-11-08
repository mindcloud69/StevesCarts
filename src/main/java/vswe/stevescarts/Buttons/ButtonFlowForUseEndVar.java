package vswe.stevescarts.Buttons;

import vswe.stevescarts.Computer.ComputerTask;
import vswe.stevescarts.Modules.Workers.ModuleComputer;

public class ButtonFlowForUseEndVar extends ButtonFlowForUseVar {
	public ButtonFlowForUseEndVar(final ModuleComputer module, final LOCATION loc, final boolean use) {
		super(module, loc, use);
	}

	@Override
	protected boolean getUseVar(final ComputerTask task) {
		return task.getFlowForUseEndVar();
	}

	@Override
	protected void setUseVar(final ComputerTask task, final boolean val) {
		task.setFlowForUseEndVar(val);
	}

	@Override
	protected String getName() {
		return "end";
	}
}
