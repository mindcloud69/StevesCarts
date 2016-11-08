package vswe.stevescarts.Buttons;

import net.minecraft.entity.player.EntityPlayer;
import vswe.stevescarts.Computer.ComputerProg;
import vswe.stevescarts.Modules.Workers.ModuleComputer;

public class ButtonProgramAdd extends ButtonAssembly {
	public ButtonProgramAdd(final ModuleComputer module, final LOCATION loc) {
		super(module, loc);
	}

	@Override
	public String toString() {
		return "Add new program";
	}

	@Override
	public boolean isVisible() {
		return super.isVisible();
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	public void onServerClick(final EntityPlayer player, final int mousebutton, final boolean ctrlKey, final boolean shiftKey) {
		((ModuleComputer) this.module).setCurrentProg(new ComputerProg((ModuleComputer) this.module));
	}
}
