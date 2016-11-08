package vswe.stevescarts.Buttons;

import net.minecraft.entity.player.EntityPlayer;
import vswe.stevescarts.Computer.ComputerProg;
import vswe.stevescarts.Computer.ComputerTask;
import vswe.stevescarts.Modules.Workers.ModuleComputer;

public class ButtonTaskType extends ButtonAssembly {
	private int typeId;

	public ButtonTaskType(final ModuleComputer module, final LOCATION loc, final int id) {
		super(module, loc);
		this.typeId = id;
	}

	@Override
	public String toString() {
		if (this.haveTasks()) {
			return "Change to " + ComputerTask.getTypeName(this.typeId);
		}
		return "Add " + ComputerTask.getTypeName(this.typeId) + " task";
	}

	@Override
	public boolean isVisible() {
		return super.isVisible();
	}

	@Override
	public int texture() {
		if (this.typeId < 4) {
			return this.typeId * 2 + (this.haveTasks() ? 1 : 0);
		}
		if (this.typeId == 4) {
			return 66 + (this.haveTasks() ? 1 : 0);
		}
		return this.typeId * 2 + (this.haveTasks() ? 1 : 0) - 2;
	}

	@Override
	public boolean isEnabled() {
		if (!(this.module instanceof ModuleComputer) || ((ModuleComputer) this.module).getCurrentProg() == null) {
			return false;
		}
		if (this.haveTasks()) {
			for (final ComputerTask task : ((ModuleComputer) this.module).getSelectedTasks()) {
				if (task.getType() != this.typeId) {
					return true;
				}
			}
			return false;
		}
		return true;
	}

	private boolean haveTasks() {
		return ((ModuleComputer) this.module).getSelectedTasks().size() > 0;
	}

	@Override
	public void onServerClick(final EntityPlayer player, final int mousebutton, final boolean ctrlKey, final boolean shiftKey) {
		if (this.haveTasks()) {
			for (final ComputerTask task : ((ModuleComputer) this.module).getSelectedTasks()) {
				task.setType(this.typeId);
			}
		} else {
			final ComputerProg program = ((ModuleComputer) this.module).getCurrentProg();
			if (program != null) {
				final ComputerTask task = new ComputerTask((ModuleComputer) this.module, program);
				task.setType(this.typeId);
				program.getTasks().add(task);
			}
		}
	}
}
