package vswe.stevescarts.Buttons;

import net.minecraft.entity.player.EntityPlayer;
import vswe.stevescarts.Computer.ComputerTask;
import vswe.stevescarts.Modules.Workers.ModuleComputer;

public abstract class ButtonVarInteger extends ButtonVar {
	private int dif;

	public ButtonVarInteger(final ModuleComputer module, final LOCATION loc, final int dif) {
		super(module, loc);
		this.dif = dif;
	}

	@Override
	public String toString() {
		if (this.dif < 0) {
			return "Decrease " + this.getName() + " by " + -1 * this.dif;
		}
		return "Increase " + this.getName() + " by " + this.dif;
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
				if (this.isVarVisible(task)) {
					return false;
				}
			}
		}
		return super.isVisible();
	}

	@Override
	public boolean isEnabled() {
		for (final ComputerTask task : ((ModuleComputer) this.module).getSelectedTasks()) {
			if (-128 <= this.getInteger(task) + this.dif && this.getInteger(task) + this.dif <= 127) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void onServerClick(final EntityPlayer player, final int mousebutton, final boolean ctrlKey, final boolean shiftKey) {
		for (final ComputerTask task : ((ModuleComputer) this.module).getSelectedTasks()) {
			this.setInteger(task, this.getInteger(task) + this.dif);
		}
	}

	protected abstract String getName();

	protected abstract boolean isVarVisible(final ComputerTask p0);

	protected abstract int getInteger(final ComputerTask p0);

	protected abstract void setInteger(final ComputerTask p0, final int p1);
}
