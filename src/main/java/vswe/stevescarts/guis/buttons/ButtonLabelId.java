package vswe.stevescarts.guis.buttons;

import net.minecraft.entity.player.EntityPlayer;
import vswe.stevescarts.computer.ComputerTask;
import vswe.stevescarts.modules.workers.ModuleComputer;

public class ButtonLabelId extends ButtonAssembly {
	private boolean increase;

	public ButtonLabelId(final ModuleComputer module, final LOCATION loc, final boolean increase) {
		super(module, loc);
		this.increase = increase;
	}

	@Override
	public String toString() {
		return this.increase ? "Increase ID" : "Decrease ID";
	}

	@Override
	public boolean isVisible() {
		if (!super.isVisible()) {
			return false;
		}
		if (this.module instanceof ModuleComputer && ((ModuleComputer) this.module).getSelectedTasks() != null && ((ModuleComputer) this.module).getSelectedTasks().size() > 0) {
			for (final ComputerTask task : ((ModuleComputer) this.module).getSelectedTasks()) {
				if (!task.isFlowLabel() && !task.isFlowGoto()) {
					return false;
				}
			}
			return true;
		}
		return false;
	}

	@Override
	public int texture() {
		return this.increase ? 23 : 24;
	}

	@Override
	public boolean isEnabled() {
		for (final ComputerTask task : ((ModuleComputer) this.module).getSelectedTasks()) {
			if ((this.increase && task.getFlowLabelId() < 31) || (!this.increase && task.getFlowLabelId() > 0)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void onServerClick(final EntityPlayer player, final int mousebutton, final boolean ctrlKey, final boolean shiftKey) {
		for (final ComputerTask task : ((ModuleComputer) this.module).getSelectedTasks()) {
			task.setFlowLabelId(task.getFlowLabelId() + (this.increase ? 1 : -1));
		}
	}
}
