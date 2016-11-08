package vswe.stevescarts.Modules.Realtimers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vswe.stevescarts.Arcade.*;
import vswe.stevescarts.Carts.MinecartModular;
import vswe.stevescarts.Helpers.ResourceHelper;
import vswe.stevescarts.Interfaces.GuiMinecart;
import vswe.stevescarts.Modules.ModuleBase;

import java.util.ArrayList;

public class ModuleArcade extends ModuleBase {
	private ArrayList<ArcadeGame> games;
	private ArcadeGame currentGame;
	private int afkTimer;

	public ModuleArcade(final MinecartModular cart) {
		super(cart);
		(this.games = new ArrayList<ArcadeGame>()).add(new ArcadeTracks(this));
		this.games.add(new ArcadeTetris(this));
		this.games.add(new ArcadeInvaders(this));
		this.games.add(new ArcadeSweeper(this));
	}

	private boolean isGameActive() {
		return this.getCart().worldObj.isRemote && this.currentGame != null;
	}

	@Override
	public boolean doStealInterface() {
		return this.isGameActive();
	}

	@Override
	public boolean hasSlots() {
		return false;
	}

	@Override
	public boolean hasGui() {
		return true;
	}

	@Override
	public int guiWidth() {
		return 190;
	}

	@Override
	public int guiHeight() {
		return 115;
	}

	@Override
	public void update() {
		if (this.isGameActive() && this.afkTimer < 10) {
			this.currentGame.update();
			++this.afkTimer;
		}
	}

	@Override
	public void drawForeground(final GuiMinecart gui) {
		if (this.isGameActive()) {
			this.currentGame.drawForeground(gui);
		} else {
			this.drawString(gui, this.getModuleName(), 8, 6, 4210752);
			for (int i = 0; i < this.games.size(); ++i) {
				final int[] text = this.getButtonTextArea(i);
				if (text[3] == 8) {
					this.drawString(gui, this.games.get(i).getName(), text[0], text[1], 4210752);
				}
			}
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void drawBackground(final GuiMinecart gui, final int x, final int y) {
		ResourceHelper.bindResource("/gui/arcade.png");
		this.afkTimer = 0;
		if (this.isGameActive()) {
			final int[] rect = this.getExitArea();
			final int srcX = 0;
			final int srcY = 104 + (this.inRect(x, y, rect) ? 16 : 0);
			this.drawImage(gui, rect, srcX, srcY);
			this.currentGame.drawBackground(gui, x, y);
		} else {
			final int[] rect = this.getListArea();
			this.drawImage(gui, rect, 0, 0);
			for (int i = 0; i < this.games.size(); ++i) {
				final int[] button = this.getButtonGraphicArea(i);
				final int srcX2 = 0;
				final int srcY2 = 136 + (this.inRect(x, y, this.getButtonBoundsArea(i)) ? button[3] : 0);
				if (button[3] > 0) {
					this.drawImage(gui, button, srcX2, srcY2);
					final int[] icon = this.getButtonIconArea(i);
					this.drawImage(gui, icon, i * 16, rect[3]);
				}
			}
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void drawMouseOver(final GuiMinecart gui, final int x, final int y) {
		if (this.isGameActive()) {
			this.drawStringOnMouseOver(gui, "Exit", x, y, this.getExitArea());
			this.currentGame.drawMouseOver(gui, x, y);
		}
	}

	private int[] getExitArea() {
		return new int[] { 455, 6, 16, 16 };
	}

	private int[] getListArea() {
		return new int[] { 15, 20, 170, 88 };
	}

	private int[] getButtonBoundsArea(final int i) {
		return this.getButtonArea(i, false);
	}

	private int[] getButtonGraphicArea(final int i) {
		return this.getButtonArea(i, true);
	}

	private int[] getButtonArea(final int i, final boolean graphic) {
		final int[] list = this.getListArea();
		return new int[] { list[0] + 2, list[1] + 2 + i * 21, 166, graphic ? 21 : 20 };
	}

	private int[] getButtonTextArea(final int i) {
		final int[] button = this.getButtonGraphicArea(i);
		return new int[] { button[0] + 24, button[1] + 6, button[2], 8 };
	}

	private int[] getButtonIconArea(final int i) {
		final int[] button = this.getButtonGraphicArea(i);
		return new int[] { button[0] + 2, button[1] + 2, 16, 16 };
	}

	@Override
	public void mouseClicked(final GuiMinecart gui, final int x, final int y, final int button) {
		if (this.isGameActive()) {
			if (button == 0 && this.inRect(x, y, this.getExitArea())) {
				this.currentGame.unload(gui);
				this.currentGame = null;
			} else {
				this.currentGame.mouseClicked(gui, x, y, button);
			}
		} else if (button == 0) {
			for (int i = 0; i < this.games.size(); ++i) {
				if (this.inRect(x, y, this.getButtonBoundsArea(i))) {
					(this.currentGame = this.games.get(i)).load(gui);
					break;
				}
			}
		}
	}

	@Override
	public void mouseMovedOrUp(final GuiMinecart gui, final int x, final int y, final int button) {
		if (this.isGameActive()) {
			this.currentGame.mouseMovedOrUp(gui, x, y, button);
		}
	}

	@Override
	public void keyPress(final GuiMinecart gui, final char character, final int extraInformation) {
		if (this.isGameActive()) {
			this.currentGame.keyPress(gui, character, extraInformation);
		}
	}

	@Override
	protected void Save(final NBTTagCompound tagCompound, final int id) {
		for (final ArcadeGame game : this.games) {
			game.Save(tagCompound, id);
		}
	}

	@Override
	protected void Load(final NBTTagCompound tagCompound, final int id) {
		for (final ArcadeGame game : this.games) {
			game.Load(tagCompound, id);
		}
	}

	public int numberOfPackets() {
		return 4;
	}

	@Override
	protected void receivePacket(final int id, final byte[] data, final EntityPlayer player) {
		for (final ArcadeGame game : this.games) {
			game.receivePacket(id, data, player);
		}
	}

	@Override
	public int numberOfGuiData() {
		return TrackStory.stories.size() + 5;
	}

	@Override
	protected void checkGuiData(final Object[] info) {
		for (final ArcadeGame game : this.games) {
			game.checkGuiData(info);
		}
	}

	@Override
	public void receiveGuiData(final int id, final short data) {
		for (final ArcadeGame game : this.games) {
			game.receiveGuiData(id, data);
		}
	}

	@Override
	public boolean disableStandardKeyFunctionality() {
		return this.currentGame != null && this.currentGame.disableStandardKeyFunctionality();
	}
}
