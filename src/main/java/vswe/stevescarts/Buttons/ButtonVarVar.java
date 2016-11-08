package vswe.stevescarts.Buttons;

import net.minecraft.entity.player.EntityPlayer;
import vswe.stevescarts.Computer.ComputerTask;
import vswe.stevescarts.Modules.Workers.ModuleComputer;

public class ButtonVarVar extends ButtonVar {
	protected boolean increase;

	public ButtonVarVar(final ModuleComputer module, final LOCATION loc, final boolean increase) {
		super(module, loc);
		this.increase = increase;
	}

	@Override
	public String toString() {
		if (this.increase) {
			return "Next " + this.getName() + " variable";
		}
		return "Previous " + this.getName() + " variable";
	}

	@Override
	public boolean isVisible() {
		if (((ModuleComputer) this.module).getSelectedTasks() != null) {
			for (final ComputerTask task : ((ModuleComputer) this.module).getSelectedTasks()) {
				if (!this.isVarVisible(task)) {
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
		return task.getVarVarIndex();
	}

	protected void setIndex(final ComputerTask task, final int val) {
		task.setVarVar(val);
	}

	protected String getName() {
		return "main";
	}

	protected boolean isVarVisible(final ComputerTask task) {
		return true;
	}
}
