package vswe.stevescarts.Buttons;

import net.minecraft.entity.player.EntityPlayer;
import vswe.stevescarts.Computer.ComputerProg;
import vswe.stevescarts.Modules.Workers.ModuleComputer;

public class ButtonProgramStart extends ButtonAssembly {
	public ButtonProgramStart(final ModuleComputer module, final LOCATION loc) {
		super(module, loc);
	}

	@Override
	public String toString() {
		return "Start Program";
	}

	@Override
	public boolean isVisible() {
		return super.isVisible();
	}

	@Override
	public boolean isEnabled() {
		return ((ModuleComputer) this.module).getCurrentProg() != null;
	}

	@Override
	public void onServerClick(final EntityPlayer player, final int mousebutton, final boolean ctrlKey, final boolean shiftKey) {
		final ComputerProg program = ((ModuleComputer) this.module).getCurrentProg();
		if (program != null) {
			program.start();
		}
	}
}
