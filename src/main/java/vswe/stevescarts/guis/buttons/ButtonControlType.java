package vswe.stevescarts.guis.buttons;

import net.minecraft.entity.player.EntityPlayer;
import vswe.stevescarts.computer.ComputerTask;
import vswe.stevescarts.modules.workers.ModuleComputer;

public class ButtonControlType extends ButtonAssembly {
	private int typeId;

	public ButtonControlType(final ModuleComputer module, final LOCATION loc, final int id) {
		super(module, loc);
		this.typeId = id;
	}

	@Override
	public String toString() {
		return "Change to " + ComputerTask.getControlTypeName(this.typeId);
	}

	@Override
	public boolean isVisible() {
		if (!super.isVisible()) {
			return false;
		}
		if (this.module instanceof ModuleComputer && ((ModuleComputer) this.module).getSelectedTasks() != null && ((ModuleComputer) this.module).getSelectedTasks().size() > 0) {
			for (final ComputerTask task : ((ModuleComputer) this.module).getSelectedTasks()) {
				if (!ComputerTask.isControl(task.getType())) {
					return false;
				}
			}
			return true;
		}
		return false;
	}

	@Override
	public int texture() {
		return ComputerTask.getControlImage(this.typeId);
	}

	@Override
	public int ColorCode() {
		return 3;
	}

	@Override
	public boolean isEnabled() {
		for (final ComputerTask task : ((ModuleComputer) this.module).getSelectedTasks()) {
			if (task.getControlType() != this.typeId) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void onServerClick(final EntityPlayer player, final int mousebutton, final boolean ctrlKey, final boolean shiftKey) {
		for (final ComputerTask task : ((ModuleComputer) this.module).getSelectedTasks()) {
			task.setControlType(this.typeId);
		}
	}
}
