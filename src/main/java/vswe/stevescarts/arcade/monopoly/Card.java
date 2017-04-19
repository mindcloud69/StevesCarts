package vswe.stevescarts.arcade.monopoly;

import vswe.stevescarts.guis.GuiMinecart;

public abstract class Card {
	private String message;

	public Card(final String message) {
		this.message = message;
	}

	public void render(final ArcadeMonopoly game, final GuiMinecart gui, final int[] rect, final boolean isFront) {
		if (isFront) {
			game.loadTexture(gui, 1);
			game.getModule().drawImage(gui, rect, 67, 177);
			game.getModule().drawSplitString(gui, message, rect[0] + gui.getGuiLeft() + 5, rect[1] + gui.getGuiTop() + 5, rect[2] - 10, true, 4210752);
			if (getNote() != null) {
				int x = 10;
				if (!getMoneyPrefix().equals("")) {
					game.getModule().drawString(gui, getMoneyPrefix(), x, 64, 4210752);
					x += gui.getFontRenderer().getStringWidth(getMoneyPrefix()) + 5;
				}
				getNote().draw(game, gui, x, 59, getNoteCount());
				x += 31;
				if (!getMoneyPostfix().equals("")) {
					game.getModule().drawString(gui, getMoneyPostfix(), x, 64, 4210752);
				}
			}
		} else {
			game.getModule().drawImage(gui, rect, 0, rect[3] * getBackgroundV());
		}
	}

	public int getNoteCount() {
		return 0;
	}

	public Note getNote() {
		return null;
	}

	public String getMoneyPrefix() {
		return "";
	}

	public String getMoneyPostfix() {
		return "";
	}

	public abstract void doStuff(final ArcadeMonopoly p0, final Piece p1);

	public abstract int getBackgroundV();
}
