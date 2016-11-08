package vswe.stevescarts.guis.buttons;

import net.minecraft.entity.player.EntityPlayer;
import vswe.stevescarts.computer.ComputerTask;
import vswe.stevescarts.modules.workers.ModuleComputer;

public class ButtonFlowEndType extends ButtonAssembly {
	private int typeId;

	public ButtonFlowEndType(final ModuleComputer module, final LOCATION loc, final int typeId) {
		super(module, loc);
		this.typeId = typeId;
	}

	@Override
	public String toString() {
		return "Change to End " + ComputerTask.getEndTypeName(this.typeId);
	}

	@Override
	public int texture() {
		return ComputerTask.getEndImage(this.typeId);
	}

	@Override
	public boolean isVisible() {
		if (!super.isVisible()) {
			return false;
		}
		if (((ModuleComputer) this.module).getSelectedTasks() != null && ((ModuleComputer) this.module).getSelectedTasks().size() > 0) {
			for (final ComputerTask task : ((ModuleComputer) this.module).getSelectedTasks()) {
				if (!task.isFlowEnd()) {
					return false;
				}
			}
			return true;
		}
		return false;
	}

	@Override
	public boolean isEnabled() {
		for (final ComputerTask task : ((ModuleComputer) this.module).getSelectedTasks()) {
			if (this.typeId != task.getFlowEndType()) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void onServerClick(final EntityPlayer player, final int mousebutton, final boolean ctrlKey, final boolean shiftKey) {
		for (final ComputerTask task : ((ModuleComputer) this.module).getSelectedTasks()) {
			task.setFlowEndType(this.typeId);
		}
	}
}
