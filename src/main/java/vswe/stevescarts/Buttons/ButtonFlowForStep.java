package vswe.stevescarts.Buttons;

import net.minecraft.entity.player.EntityPlayer;
import vswe.stevescarts.Computer.ComputerTask;
import vswe.stevescarts.Modules.Workers.ModuleComputer;

public class ButtonFlowForStep extends ButtonFlowFor {
	private boolean decrease;

	public ButtonFlowForStep(final ModuleComputer module, final LOCATION loc, final boolean decrease) {
		super(module, loc);
		this.decrease = decrease;
	}

	@Override
	public String toString() {
		return this.decrease ? "Set step to -1" : "Set step to +1";
	}

	@Override
	public int texture() {
		return this.decrease ? 45 : 44;
	}

	@Override
	public boolean isEnabled() {
		for (final ComputerTask task : ((ModuleComputer) this.module).getSelectedTasks()) {
			if (this.decrease != task.getFlowForDecrease()) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void onServerClick(final EntityPlayer player, final int mousebutton, final boolean ctrlKey, final boolean shiftKey) {
		for (final ComputerTask task : ((ModuleComputer) this.module).getSelectedTasks()) {
			task.setFlowForDecrease(this.decrease);
		}
	}
}
