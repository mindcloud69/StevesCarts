package vswe.stevescarts.arcade;

public class Chance extends CardPlace {
	public Chance(final ArcadeMonopoly game) {
		super(game);
	}

	@Override
	protected int getTextureId() {
		return 0;
	}

	@Override
	public Card getCard() {
		return CardChance.cards.get(this.game.getModule().getCart().rand.nextInt(CardChance.cards.size()));
	}
}
