package vswe.stevescarts.guis.buttons;

import net.minecraft.entity.player.EntityPlayer;
import vswe.stevescarts.computer.ComputerTask;
import vswe.stevescarts.modules.workers.ModuleComputer;

public class ButtonControlInteger extends ButtonControl {
	private int dif;

	public ButtonControlInteger(final ModuleComputer module, final LOCATION loc, final int dif) {
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
				if (task.getControlUseVar() || !task.getControlUseBigInteger(Math.abs(this.dif))) {
					return false;
				}
			}
		}
		return super.isVisible();
	}

	@Override
	public boolean isEnabled() {
		for (final ComputerTask task : ((ModuleComputer) this.module).getSelectedTasks()) {
			if (task.getControlMinInteger() <= task.getControlInteger() + this.dif && task.getControlInteger() + this.dif <= task.getControlMaxInteger()) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void onServerClick(final EntityPlayer player, final int mousebutton, final boolean ctrlKey, final boolean shiftKey) {
		for (final ComputerTask task : ((ModuleComputer) this.module).getSelectedTasks()) {
			task.setControlInteger(task.getControlInteger() + this.dif);
		}
	}
}
