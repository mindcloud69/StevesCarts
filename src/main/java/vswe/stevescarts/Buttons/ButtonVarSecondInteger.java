package vswe.stevescarts.Buttons;

import vswe.stevescarts.Computer.ComputerTask;
import vswe.stevescarts.Modules.Workers.ModuleComputer;

public class ButtonVarSecondInteger extends ButtonVarInteger {
	public ButtonVarSecondInteger(final ModuleComputer module, final LOCATION loc, final int dif) {
		super(module, loc, dif);
	}

	@Override
	protected String getName() {
		return "second";
	}

	@Override
	protected boolean isVarVisible(final ComputerTask task) {
		return task.getVarUseSecondVar();
	}

	@Override
	protected int getInteger(final ComputerTask task) {
		return task.getVarSecondInteger();
	}

	@Override
	protected void setInteger(final ComputerTask task, final int val) {
		task.setVarSecondInteger(val);
	}

	@Override
	protected boolean isSecondValue() {
		return true;
	}
}
