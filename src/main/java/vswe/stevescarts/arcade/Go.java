package vswe.stevescarts.arcade;

import java.util.EnumSet;

import vswe.stevescarts.guis.GuiMinecart;

public class Go extends CornerPlace {
	public Go(final ArcadeMonopoly game) {
		super(game, 0);
	}

	@Override
	public void draw(final GuiMinecart gui, final EnumSet<PLACE_STATE> states) {
		super.draw(gui, states);
		Note.DIAMOND.draw(this.game, gui, 45, 5, 2);
	}

	@Override
	public void drawText(final GuiMinecart gui, final EnumSet<PLACE_STATE> states) {
		this.game.getModule().drawString(gui, "Collect", 5, 10, 4210752);
		this.game.getModule().drawString(gui, "as you pass.", 5, 20, 4210752);
	}

	@Override
	public void onPiecePass(final Piece piece) {
		piece.addMoney(Note.DIAMOND, 2, true);
	}
}
