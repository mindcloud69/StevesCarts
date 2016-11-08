package vswe.stevescarts.Buttons;

import net.minecraft.entity.player.EntityPlayer;
import vswe.stevescarts.Computer.ComputerTask;
import vswe.stevescarts.Modules.Workers.ModuleComputer;

public class ButtonFlowConditionVar extends ButtonFlowCondition {
	protected boolean increase;

	public ButtonFlowConditionVar(final ModuleComputer module, final LOCATION loc, final boolean increase) {
		super(module, loc);
		this.increase = increase;
	}

	@Override
	public String toString() {
		if (this.increase) {
			return "Next variable";
		}
		return "Previous variable";
	}

	@Override
	public int texture() {
		return this.increase ? 30 : 31;
	}

	@Override
	public boolean isEnabled() {
		for (final ComputerTask task : ((ModuleComputer) this.module).getSelectedTasks()) {
			if (this.increase && this.getIndex(task) < task.getProgram().getVars().size() - 1) {
				return true;
			}
			if (!this.increase && this.getIndex(task) > -1) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void onServerClick(final EntityPlayer player, final int mousebutton, final boolean ctrlKey, final boolean shiftKey) {
		for (final ComputerTask task : ((ModuleComputer) this.module).getSelectedTasks()) {
			this.setIndex(task, this.getIndex(task) + (this.increase ? 1 : -1));
		}
	}

	protected int getIndex(final ComputerTask task) {
		return task.getFlowConditionVarIndex();
	}

	protected void setIndex(final ComputerTask task, final int val) {
		task.setFlowConditionVar(val);
	}
}
