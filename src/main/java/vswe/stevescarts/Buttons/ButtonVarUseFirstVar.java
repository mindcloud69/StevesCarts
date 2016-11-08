package vswe.stevescarts.Buttons;

import vswe.stevescarts.Computer.ComputerTask;
import vswe.stevescarts.Modules.Workers.ModuleComputer;

public class ButtonVarUseFirstVar extends ButtonVarUseVar {
	public ButtonVarUseFirstVar(final ModuleComputer module, final LOCATION loc, final boolean use) {
		super(module, loc, use);
	}

	@Override
	protected boolean getUseVar(final ComputerTask task) {
		return task.getVarUseFirstVar();
	}

	@Override
	protected void setUseVar(final ComputerTask task, final boolean val) {
		task.setVarUseFirstVar(val);
	}

	@Override
	protected String getName() {
		return "first";
	}
}
