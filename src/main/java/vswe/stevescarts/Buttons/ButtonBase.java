package vswe.stevescarts.Buttons;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vswe.stevescarts.Helpers.ResourceHelper;
import vswe.stevescarts.Interfaces.GuiMinecart;
import vswe.stevescarts.Modules.ModuleBase;

public abstract class ButtonBase {
	protected final LOCATION loc;
	protected final ModuleBase module;
	private boolean lastVisibility;
	private int currentID;
	private int moduleID;
	@SideOnly(Side.CLIENT)
	private static ResourceLocation texture;

	public ButtonBase(final ModuleBase module, final LOCATION loc) {
		(this.module = module).addButton(this);
		this.loc = loc;
	}

	public void setCurrentID(final int id) {
		this.currentID = id;
	}

	public void setIdInModule(final int id) {
		this.moduleID = id;
	}

	public int getIdInModule() {
		return this.moduleID;
	}

	@Override
	public String toString() {
		return "";
	}

	public boolean isEnabled() {
		return false;
	}

	public boolean hasText() {
		return false;
	}

	public boolean isVisible() {
		return false;
	}

	public final void computeOnClick(final GuiMinecart gui, final int mousebutton) {
		if (this.isVisible() && this.isEnabled()) {
			this.onClientClick(mousebutton, GuiMinecart.isCtrlKeyDown(), GuiMinecart.isShiftKeyDown());
			if (this.handleClickOnServer()) {
				byte clickinformation = (byte) (mousebutton & 0x3F);
				clickinformation |= (byte) ((GuiMinecart.isCtrlKeyDown() ? 1 : 0) << 6);
				clickinformation |= (byte) ((GuiMinecart.isShiftKeyDown() ? 1 : 0) << 7);
				this.module.sendButtonPacket(this, clickinformation);
			}
		}
	}

	public void onClientClick(final int mousebutton, final boolean ctrlKey, final boolean shiftKey) {
	}

	public void onServerClick(final EntityPlayer player, final int mousebutton, final boolean ctrlKey, final boolean shiftKey) {
	}

	public boolean handleClickOnServer() {
		return true;
	}

	private boolean useTexture() {
		return this.texture() != -1;
	}

	public int ColorCode() {
		return 0;
	}

	private boolean hasBorder() {
		return this.borderID() != -1;
	}

	public int borderID() {
		return -1;
	}

	public int texture() {
		return -1;
	}

	public int textureX() {
		return this.texture() % 21 * 12;
	}

	public int textureY() {
		return 60 + this.texture() / 21 * 12;
	}

	public void drawButtonText(final GuiMinecart gui, final ModuleBase module) {
		if (this.isVisible() && this.hasText()) {
			module.drawString(gui, this.toString(), this.X() + 8, this.Y() + 7, 16777215);
		}
	}

	public void drawButton(final GuiMinecart gui, final ModuleBase module, final int x, final int y) {
		final boolean visibility = this.isVisible();
		if (visibility != this.lastVisibility) {
			module.buttonVisibilityChanged();
		}
		this.lastVisibility = visibility;
		ResourceHelper.bindResource(ButtonBase.texture);
		if (!visibility) {
			return;
		}
		int sourceX = 0;
		int sourceY = 20;
		if (this.isEnabled()) {
			sourceX = 20 * (this.ColorCode() + 1);
		}
		if (module.inRect(x, y, this.getBounds())) {
			sourceY += 20;
		}
		module.drawImage(gui, this.getBounds(), sourceX, sourceY);
		if (this.useTexture()) {
			module.drawImage(gui, this.X() + 4, this.Y() + 4, this.textureX(), this.textureY(), 12, 12);
		}
		if (this.hasBorder()) {
			module.drawImage(gui, this.getBounds(), this.borderID() * 20, 0);
		}
	}

	public int[] getBounds() {
		return new int[] { this.X(), this.Y(), 20, 20 };
	}

	public int X() {
		switch (this.loc) {
			case OVERVIEW: {
				return 15 + this.currentID * 25;
			}
			case PROGRAM: {
				return 125 + this.currentID % 6 * 25;
			}
			case TASK: {
				return 306 + this.currentID % 5 * 25;
			}
			case DEFINED: {
				return 0;
			}
			case FLOATING: {
				return 115 + this.currentID % 7 * 25;
			}
			case VARIABLE: {
				return 400 + this.currentID % 3 * 25;
			}
			case BUILD: {
				return 366 + this.currentID % 5 * 25;
			}
			case MODEL: {
				return 111 + this.currentID % 6 * 22;
			}
			default: {
				return -1;
			}
		}
	}

	public int Y() {
		switch (this.loc) {
			case OVERVIEW: {
				return 143;
			}
			case PROGRAM: {
				return 118 + this.currentID / 6 * 25;
			}
			case TASK: {
				return 32 + this.currentID / 5 * 25;
			}
			case DEFINED: {
				return 0;
			}
			case FLOATING: {
				return 32 + this.currentID / 7 * 25;
			}
			case VARIABLE: {
				return 32 + this.currentID / 3 * 25;
			}
			case BUILD: {
				return 118 + this.currentID / 5 * 25;
			}
			case MODEL: {
				return 19 + this.currentID / 6 * 22;
			}
			default: {
				return -1;
			}
		}
	}

	public LOCATION getLocation() {
		return this.loc;
	}

	public int getLocationID() {
		for (int i = 0; i < LOCATION.values().length; ++i) {
			if (LOCATION.values()[i] == this.loc) {
				return i;
			}
		}
		return 0;
	}

	static {
		ButtonBase.texture = ResourceHelper.getResource("/gui/buttons.png");
	}

	public enum LOCATION {
		OVERVIEW,
		PROGRAM,
		TASK,
		DEFINED,
		FLOATING,
		VARIABLE,
		BUILD,
		MODEL
	}
}
