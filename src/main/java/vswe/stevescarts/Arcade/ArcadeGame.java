package vswe.stevescarts.Arcade;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vswe.stevescarts.Helpers.Localization;
import vswe.stevescarts.Helpers.SoundHandler;
import vswe.stevescarts.Interfaces.GuiMinecart;
import vswe.stevescarts.Modules.Realtimers.ModuleArcade;
import vswe.stevescarts.StevesCarts;

public abstract class ArcadeGame {
	private ModuleArcade module;
	private Localization.ARCADE name;

	public ArcadeGame(final ModuleArcade module, final Localization.ARCADE name) {
		this.name = name;
		this.module = module;
	}

	public String getName() {
		return this.name.translate();
	}

	public ModuleArcade getModule() {
		return this.module;
	}

	@SideOnly(Side.CLIENT)
	public void update() {
		if (StevesCarts.instance.useArcadeSounds) {
			this.getModule().getCart().silent();
		}
	}

	@SideOnly(Side.CLIENT)
	public void drawForeground(final GuiMinecart gui) {
	}

	@SideOnly(Side.CLIENT)
	public void drawBackground(final GuiMinecart gui, final int x, final int y) {
	}

	@SideOnly(Side.CLIENT)
	public void drawMouseOver(final GuiMinecart gui, final int x, final int y) {
	}

	@SideOnly(Side.CLIENT)
	public void mouseClicked(final GuiMinecart gui, final int x, final int y, final int button) {
	}

	@SideOnly(Side.CLIENT)
	public void mouseMovedOrUp(final GuiMinecart gui, final int x, final int y, final int button) {
	}

	@SideOnly(Side.CLIENT)
	public void keyPress(final GuiMinecart gui, final char character, final int extraInformation) {
	}

	public void Save(final NBTTagCompound tagCompound, final int id) {
	}

	public void Load(final NBTTagCompound tagCompound, final int id) {
	}

	public void receivePacket(final int id, final byte[] data, final EntityPlayer player) {
	}

	public void checkGuiData(final Object[] info) {
	}

	public void receiveGuiData(final int id, final short data) {
	}

	public boolean disableStandardKeyFunctionality() {
		return false;
	}

	@SideOnly(Side.CLIENT)
	public static void playSound(final String sound, final float volume, final float pitch) {
		if (StevesCarts.instance.useArcadeSounds && sound != null) {
			SoundHandler.playSound(sound, SoundCategory.BLOCKS, volume, pitch);
		}
	}

	@SideOnly(Side.CLIENT)
	public static void playDefaultSound(final String sound, final float volume, final float pitch) {
		if (StevesCarts.instance.useArcadeSounds && sound != null) {
			SoundHandler.playDefaultSound(sound, SoundCategory.BLOCKS, volume, pitch);
		}
	}

	public boolean allowKeyRepeat() {
		return false;
	}

	public void load(final GuiMinecart gui) {
		gui.enableKeyRepeat(this.allowKeyRepeat());
	}

	public void unload(final GuiMinecart gui) {
		if (this.allowKeyRepeat()) {
			gui.enableKeyRepeat(false);
		}
	}

	public void drawImageInArea(final GuiMinecart gui, final int x, final int y, final int u, final int v, final int w, final int h) {
		this.drawImageInArea(gui, x, y, u, v, w, h, 5, 4, 443, 168);
	}

	public void drawImageInArea(final GuiMinecart gui, int x, int y, int u, int v, int w, int h, final int x1, final int y1, final int x2, final int y2) {
		if (x < x1) {
			w -= x1 - x;
			u += x1 - x;
			x = x1;
		} else if (x + w > x2) {
			w = x2 - x;
		}
		if (y < y1) {
			h -= y1 - y;
			v += y1 - y;
			y = y1;
		} else if (y + h > y2) {
			h = y2 - y;
		}
		if (w > 0 && h > 0) {
			this.getModule().drawImage(gui, x, y, u, v, w, h);
		}
	}
}
