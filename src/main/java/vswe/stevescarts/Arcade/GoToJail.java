package vswe.stevescarts.Arcade;

public class GoToJail extends CornerPlace {
	public GoToJail(final ArcadeMonopoly game) {
		super(game, 3);
	}

	@Override
	public boolean onPieceStop(final Piece piece) {
		piece.goToJail();
		return super.onPieceStop(piece);
	}
}
