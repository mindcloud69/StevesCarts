package vswe.stevescarts.guis.buttons;

import net.minecraft.entity.player.EntityPlayer;
import vswe.stevescarts.computer.ComputerTask;
import vswe.stevescarts.modules.workers.ModuleComputer;

public class ButtonFlowConditionInteger extends ButtonFlowCondition {
	private int dif;

	public ButtonFlowConditionInteger(final ModuleComputer module, final LOCATION loc, final int dif) {
		super(module, loc);
		this.dif = dif;
	}

	@Override
	public String toString() {
		if (this.dif < 0) {
			return "Decrease by " + -1 * this.dif;
		}
		return "Increase by " + this.dif;
	}

	@Override
	public int texture() {
		if (this.dif == 1) {
			return 40;
		}
		if (this.dif == -1) {
			return 41;
		}
		if (this.dif == 10) {
			return 42;
		}
		if (this.dif == -10) {
			return 43;
		}
		return super.texture();
	}

	@Override
	public boolean isVisible() {
		if (((ModuleComputer) this.module).getSelectedTasks() != null) {
			for (final ComputerTask task : ((ModuleComputer) this.module).getSelectedTasks()) {
				if (task.getFlowConditionUseSecondVar()) {
					return false;
				}
			}
		}
		return super.isVisible();
	}

	@Override
	public boolean isEnabled() {
		for (final ComputerTask task : ((ModuleComputer) this.module).getSelectedTasks()) {
			if (-128 <= task.getFlowConditionInteger() + this.dif && task.getFlowConditionInteger() + this.dif <= 127) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void onServerClick(final EntityPlayer player, final int mousebutton, final boolean ctrlKey, final boolean shiftKey) {
		for (final ComputerTask task : ((ModuleComputer) this.module).getSelectedTasks()) {
			task.setFlowConditionInteger(task.getFlowConditionInteger() + this.dif);
		}
	}
}
