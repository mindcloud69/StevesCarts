package vswe.stevescarts.arcade;

import java.util.EnumSet;

import vswe.stevescarts.guis.GuiMinecart;

public class Community extends CardPlace {
	public Community(final ArcadeMonopoly game) {
		super(game);
	}

	@Override
	protected int getTextureId() {
		return 5;
	}

	@Override
	public void drawText(final GuiMinecart gui, final EnumSet<PLACE_STATE> states) {
		this.game.getModule().drawSplitString(gui, "Dungeon Chest", 3 + gui.getGuiLeft(), 10 + gui.getGuiTop(), 70, true, 4210752);
	}

	@Override
	public Card getCard() {
		return CardCommunity.cards.get(this.game.getModule().getCart().rand.nextInt(CardCommunity.cards.size()));
	}
}
