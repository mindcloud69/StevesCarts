package vswe.stevescarts.arcade;

import java.util.EnumSet;

import org.lwjgl.opengl.GL11;

import vswe.stevescarts.guis.GuiMinecart;

public class Place {
	protected ArcadeMonopoly game;

	public Place(final ArcadeMonopoly game) {
		this.game = game;
	}

	protected int getTextureId() {
		return -1;
	}

	public void draw(final GuiMinecart gui, final EnumSet<PLACE_STATE> states) {
		int t;
		int u;
		int v;
		if (this.getTextureId() == -1) {
			t = 1;
			u = 0;
			v = 0;
		} else {
			t = 3 + this.getTextureId() / 6;
			u = this.getTextureId() % 3;
			v = this.getTextureId() % 6 / 3;
		}
		this.game.loadTexture(gui, t);
		this.applyColorFilter(gui, states);
		this.game.getModule().drawImage(gui, 0, 0, 76 * u, 122 * v, 76, 122);
	}

	public void applyColorFilter(final GuiMinecart gui, final EnumSet<PLACE_STATE> states) {
		if (states.contains(PLACE_STATE.SELECTED)) {
			if (states.contains(PLACE_STATE.HOVER)) {
				GL11.glColor4f(1.0f, 0.8f, 0.5f, 1.0f);
			} else {
				GL11.glColor4f(1.0f, 1.0f, 0.75f, 1.0f);
			}
		} else if (states.contains(PLACE_STATE.MARKED)) {
			if (states.contains(PLACE_STATE.HOVER)) {
				GL11.glColor4f(1.0f, 0.75f, 1.0f, 1.0f);
			} else {
				GL11.glColor4f(1.0f, 0.85f, 0.85f, 1.0f);
			}
		} else if (states.contains(PLACE_STATE.HOVER)) {
			GL11.glColor4f(0.9f, 0.9f, 1.0f, 1.0f);
		}
	}

	public void drawText(final GuiMinecart gui, final EnumSet<PLACE_STATE> states) {
	}

	public void drawPiece(final GuiMinecart gui, final Piece piece, final int total, final int pos, final int area, final EnumSet<PLACE_STATE> states) {
		final int SIZE = 24;
		final int PADDING = 5;
		final int MARGIN = 2;
		final int allowedWidth = this.getAllowedWidth(area) - 10;
		final int fullWidth = total * 26 - 2;
		int startX;
		int offSet;
		if (allowedWidth < fullWidth && total > 1) {
			startX = 5;
			offSet = (allowedWidth - 24) / (total - 1);
		} else {
			startX = 5 + (allowedWidth - fullWidth) / 2;
			offSet = 26;
		}
		this.game.getModule().drawImage(gui, startX + offSet * pos, this.getPieceYPosition(area), 232, piece.getV() * 24, 24, 24);
	}

	protected int getPieceYPosition(final int area) {
		return 70;
	}

	protected int getAllowedWidth(final int area) {
		return 76;
	}

	public void onPiecePass(final Piece piece) {
	}

	public boolean onPieceStop(final Piece piece) {
		return true;
	}

	public void onClick() {
	}

	public int getPieceAreaCount() {
		return 1;
	}

	public int getPieceAreaForPiece(final Piece piece) {
		return 0;
	}

	public enum PLACE_STATE {
		HOVER,
		SELECTED,
		MARKED,
		ZOOMED
	}
}
