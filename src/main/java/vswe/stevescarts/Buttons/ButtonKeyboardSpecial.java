package vswe.stevescarts.Buttons;

import net.minecraft.entity.player.EntityPlayer;
import vswe.stevescarts.Computer.ComputerVar;
import vswe.stevescarts.Modules.Workers.ModuleComputer;

public class ButtonKeyboardSpecial extends ButtonKeyboard {
	private KEY key;

	protected ButtonKeyboardSpecial(final ModuleComputer module, final int x, final int y, final KEY key) {
		super(module, x, y, ' ');
		this.key = key;
	}

	@Override
	public String toString() {
		return this.key.toString();
	}

	@Override
	public boolean isEnabled() {
		if (this.key == KEY.BACKSPACE || this.key == KEY.ENTER) {
			return ((ModuleComputer) this.module).getWriting().getText().length() > 0;
		}
		return super.isEnabled();
	}

	@Override
	public int texture() {
		if (this.key == KEY.CAPS) {
			return 26;
		}
		if (this.key == KEY.SHIFT) {
			return 27;
		}
		if (this.key == KEY.BACKSPACE) {
			return 28;
		}
		if (this.key == KEY.ENTER) {
			return 29;
		}
		return super.texture();
	}

	@Override
	public int X() {
		final int temp = this.y;
		this.y = 0;
		final int temp2 = super.X();
		this.y = temp;
		return temp2;
	}

	@Override
	public boolean hasText() {
		return false;
	}

	@Override
	public int borderID() {
		if ((this.key == KEY.SHIFT && ((ModuleComputer) this.module).getShift()) || (this.key == KEY.CAPS && ((ModuleComputer) this.module).getCaps())) {
			return 3;
		}
		return super.borderID();
	}

	@Override
	public void onServerClick(final EntityPlayer player, final int mousebutton, final boolean ctrlKey, final boolean shiftKey) {
		if (this.key == KEY.BACKSPACE) {
			((ModuleComputer) this.module).getWriting().removeChar();
		} else if (this.key == KEY.ENTER) {
			if (((ModuleComputer) this.module).getWriting() instanceof ComputerVar) {
				((ComputerVar) ((ModuleComputer) this.module).getWriting()).setEditing(false);
			}
		} else if (this.key == KEY.SHIFT) {
			((ModuleComputer) this.module).flipShift();
		} else if (this.key == KEY.CAPS) {
			((ModuleComputer) this.module).flipCaps();
		}
	}

	public enum KEY {
		SHIFT,
		CAPS,
		BACKSPACE,
		ENTER
	}
}
