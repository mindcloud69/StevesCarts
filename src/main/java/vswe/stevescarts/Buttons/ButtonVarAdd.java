package vswe.stevescarts.Buttons;

import net.minecraft.entity.player.EntityPlayer;
import vswe.stevescarts.Computer.ComputerVar;
import vswe.stevescarts.Modules.Workers.ModuleComputer;

public class ButtonVarAdd extends ButtonAssembly {
	public ButtonVarAdd(final ModuleComputer module, final LOCATION loc) {
		super(module, loc);
	}

	@Override
	public String toString() {
		return "Add Variable";
	}

	@Override
	public boolean isVisible() {
		return super.isVisible();
	}

	@Override
	public int texture() {
		return 25;
	}

	@Override
	public boolean isEnabled() {
		return ((ModuleComputer) this.module).getCurrentProg() != null;
	}

	@Override
	public void onServerClick(final EntityPlayer player, final int mousebutton, final boolean ctrlKey, final boolean shiftKey) {
		if (((ModuleComputer) this.module).getCurrentProg() != null) {
			final ComputerVar var = new ComputerVar((ModuleComputer) this.module);
			var.setEditing(true);
			((ModuleComputer) this.module).getCurrentProg().getVars().add(var);
		}
	}
}
