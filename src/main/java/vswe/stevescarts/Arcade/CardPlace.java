package vswe.stevescarts.Arcade;

public abstract class CardPlace extends Place {
	public CardPlace(final ArcadeMonopoly game) {
		super(game);
	}

	public abstract Card getCard();

	@Override
	public boolean onPieceStop(final Piece piece) {
		return false;
	}
}
