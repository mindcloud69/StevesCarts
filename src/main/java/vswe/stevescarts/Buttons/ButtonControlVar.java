package vswe.stevescarts.Buttons;

import net.minecraft.entity.player.EntityPlayer;
import vswe.stevescarts.Computer.ComputerTask;
import vswe.stevescarts.Modules.Workers.ModuleComputer;

public class ButtonControlVar extends ButtonControl {
	protected boolean increase;

	public ButtonControlVar(final ModuleComputer module, final LOCATION loc, final boolean increase) {
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
	public boolean isVisible() {
		if (((ModuleComputer) this.module).getSelectedTasks() != null) {
			for (final ComputerTask task : ((ModuleComputer) this.module).getSelectedTasks()) {
				if (!task.getControlUseVar()) {
					return false;
				}
			}
		}
		return super.isVisible();
	}

	@Override
	public int texture() {
		return this.increase ? 30 : 31;
	}

	@Override
	public boolean isEnabled() {
		for (final ComputerTask task : ((ModuleComputer) this.module).getSelectedTasks()) {
			if (this.increase && task.getControlVarIndex() < task.getProgram().getVars().size() - 1) {
				return true;
			}
			if (!this.increase && task.getControlVarIndex() > -1) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void onServerClick(final EntityPlayer player, final int mousebutton, final boolean ctrlKey, final boolean shiftKey) {
		for (final ComputerTask task : ((ModuleComputer) this.module).getSelectedTasks()) {
			task.setControlVar(task.getControlVarIndex() + (this.increase ? 1 : -1));
		}
	}
}
