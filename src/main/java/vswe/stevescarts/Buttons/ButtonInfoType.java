package vswe.stevescarts.Buttons;

import net.minecraft.entity.player.EntityPlayer;
import vswe.stevescarts.Computer.ComputerTask;
import vswe.stevescarts.Modules.Workers.ModuleComputer;

public class ButtonInfoType extends ButtonAssembly {
	private int typeId;

	public ButtonInfoType(final ModuleComputer module, final LOCATION loc, final int id) {
		super(module, loc);
		this.typeId = id;
	}

	@Override
	public String toString() {
		return "Change to " + ComputerTask.getInfoTypeName(this.typeId);
	}

	@Override
	public boolean isVisible() {
		if (!super.isVisible()) {
			return false;
		}
		if (this.module instanceof ModuleComputer && ((ModuleComputer) this.module).getSelectedTasks() != null && ((ModuleComputer) this.module).getSelectedTasks().size() > 0) {
			for (final ComputerTask task : ((ModuleComputer) this.module).getSelectedTasks()) {
				if (!ComputerTask.isInfo(task.getType())) {
					return false;
				}
			}
			return true;
		}
		return false;
	}

	@Override
	public int texture() {
		return ComputerTask.getInfoImage(this.typeId);
	}

	@Override
	public int ColorCode() {
		return 4;
	}

	@Override
	public boolean isEnabled() {
		for (final ComputerTask task : ((ModuleComputer) this.module).getSelectedTasks()) {
			if (task.getInfoType() != this.typeId) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void onServerClick(final EntityPlayer player, final int mousebutton, final boolean ctrlKey, final boolean shiftKey) {
		for (final ComputerTask task : ((ModuleComputer) this.module).getSelectedTasks()) {
			task.setInfoType(this.typeId);
		}
	}
}
