package vswe.stevescarts.Buttons;

import net.minecraft.entity.player.EntityPlayer;
import vswe.stevescarts.Computer.ComputerTask;
import vswe.stevescarts.Modules.Workers.ModuleComputer;

public class ButtonInfoVar extends ButtonAssembly {
	protected boolean increase;

	public ButtonInfoVar(final ModuleComputer module, final LOCATION loc, final boolean increase) {
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
		if (!super.isVisible()) {
			return false;
		}
		if (((ModuleComputer) this.module).getSelectedTasks() != null && ((ModuleComputer) this.module).getSelectedTasks().size() > 0) {
			for (final ComputerTask task : ((ModuleComputer) this.module).getSelectedTasks()) {
				if (!ComputerTask.isInfo(task.getType()) || task.isInfoEmpty()) {
					return false;
				}
			}
			return true;
		}
		return false;
	}

	@Override
	public int texture() {
		return this.increase ? 30 : 31;
	}

	@Override
	public boolean isEnabled() {
		for (final ComputerTask task : ((ModuleComputer) this.module).getSelectedTasks()) {
			if (this.increase && task.getInfoVarIndex() < task.getProgram().getVars().size() - 1) {
				return true;
			}
			if (!this.increase && task.getInfoVarIndex() > -1) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void onServerClick(final EntityPlayer player, final int mousebutton, final boolean ctrlKey, final boolean shiftKey) {
		for (final ComputerTask task : ((ModuleComputer) this.module).getSelectedTasks()) {
			task.setInfoVar(task.getInfoVarIndex() + (this.increase ? 1 : -1));
		}
	}
}
