package vswe.stevescarts.Buttons;

import net.minecraft.entity.player.EntityPlayer;
import vswe.stevescarts.Computer.ComputerTask;
import vswe.stevescarts.Modules.Workers.ModuleComputer;

public class ButtonControlUseVar extends ButtonControl {
	private boolean use;

	public ButtonControlUseVar(final ModuleComputer module, final LOCATION loc, final boolean use) {
		super(module, loc);
		this.use = use;
	}

	@Override
	public String toString() {
		return this.use ? "Use variable" : "Use integer";
	}

	@Override
	public int texture() {
		return this.use ? 38 : 39;
	}

	@Override
	public boolean isEnabled() {
		for (final ComputerTask task : ((ModuleComputer) this.module).getSelectedTasks()) {
			if (this.use != task.getControlUseVar()) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void onServerClick(final EntityPlayer player, final int mousebutton, final boolean ctrlKey, final boolean shiftKey) {
		for (final ComputerTask task : ((ModuleComputer) this.module).getSelectedTasks()) {
			task.setControlUseVar(this.use);
		}
	}
}
