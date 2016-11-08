package vswe.stevescarts.Arcade;

import java.util.ArrayList;

public abstract class CardVillager extends Card {
	public static ArrayList<CardVillager> cards;

	public CardVillager(final String message) {
		super(message);
	}

	@Override
	public int getBackgroundV() {
		return 2;
	}

	static {
		(CardVillager.cards = new ArrayList<CardVillager>()).add(new CardVillager("No, I'm a helicopter.") {
			@Override
			public void doStuff(final ArcadeMonopoly game, final Piece piece) {
			}
		});
	}
}
