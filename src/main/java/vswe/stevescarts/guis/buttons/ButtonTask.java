package vswe.stevescarts.guis.buttons;

import net.minecraft.entity.player.EntityPlayer;
import vswe.stevescarts.computer.ComputerProg;
import vswe.stevescarts.computer.ComputerTask;
import vswe.stevescarts.modules.workers.ModuleComputer;

import java.util.ArrayList;

public class ButtonTask extends ButtonAssembly {
	private int id;

	public ButtonTask(final ModuleComputer module, final LOCATION loc, final int id) {
		super(module, loc);
		this.id = id;
	}

	@Override
	public String toString() {
		final ComputerTask task = this.getTask();
		if (task == null) {
			return "Something went wrong";
		}
		return task.toString();
	}

	@Override
	public boolean isVisible() {
		return super.isVisible() && this.getTask() != null;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	public int borderID() {
		final ComputerTask task = this.getTask();
		if (task != null) {
			final boolean selected = task.getIsActivated();
			boolean running = false;
			if (this.module instanceof ModuleComputer) {
				final ComputerProg program = ((ModuleComputer) this.module).getActiveProgram();
				if (program != null) {
					running = (program.getActiveId() == this.id);
				}
			}
			if (running && selected) {
				return 2;
			}
			if (running) {
				return 1;
			}
			if (selected) {
				return 0;
			}
		}
		return super.borderID();
	}

	@Override
	public int ColorCode() {
		final ComputerTask task = this.getTask();
		if (task != null) {
			return task.getType();
		}
		return 0;
	}

	@Override
	public int texture() {
		final ComputerTask task = this.getTask();
		if (task != null) {
			return task.getImage();
		}
		return super.texture();
	}

	@Override
	public void onServerClick(final EntityPlayer player, final int mousebutton, final boolean ctrlKey, final boolean shiftKey) {
		final ComputerTask task = this.getTask();
		if (!ctrlKey && this.module instanceof ModuleComputer) {
			final ComputerProg program = ((ModuleComputer) this.module).getCurrentProg();
			if (program != null) {
				for (final ComputerTask t : program.getTasks()) {
					if (t != task) {
						t.setIsActivated(false);
					}
				}
			}
		}
		task.setIsActivated(!task.getIsActivated());
	}

	private ComputerTask getTask() {
		final ComputerProg program = ((ModuleComputer) this.module).getCurrentProg();
		if (program == null) {
			return null;
		}
		final ArrayList<ComputerTask> tasks = program.getTasks();
		if (this.id >= 0 && this.id < tasks.size()) {
			return tasks.get(this.id);
		}
		return null;
	}
}
