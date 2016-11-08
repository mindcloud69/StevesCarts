package vswe.stevescarts.Buttons;

import net.minecraft.entity.player.EntityPlayer;
import vswe.stevescarts.Computer.ComputerTask;
import vswe.stevescarts.Modules.Workers.ModuleComputer;

public abstract class ButtonFlowForUseVar extends ButtonFlowFor {
	private boolean use;

	public ButtonFlowForUseVar(final ModuleComputer module, final LOCATION loc, final boolean use) {
		super(module, loc);
		this.use = use;
	}

	@Override
	public String toString() {
		return this.use ? ("Use " + this.getName() + " variable") : ("Use " + this.getName() + " integer");
	}

	@Override
	public int texture() {
		return this.use ? 38 : 39;
	}

	@Override
	public boolean isEnabled() {
		for (final ComputerTask task : ((ModuleComputer) this.module).getSelectedTasks()) {
			if (this.use != this.getUseVar(task)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void onServerClick(final EntityPlayer player, final int mousebutton, final boolean ctrlKey, final boolean shiftKey) {
		for (final ComputerTask task : ((ModuleComputer) this.module).getSelectedTasks()) {
			this.setUseVar(task, this.use);
		}
	}

	protected abstract boolean getUseVar(final ComputerTask p0);

	protected abstract void setUseVar(final ComputerTask p0, final boolean p1);

	protected abstract String getName();
}
