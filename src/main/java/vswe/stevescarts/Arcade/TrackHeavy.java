package vswe.stevescarts.Arcade;

public class TrackHeavy extends Track {
	public TrackHeavy(final int x, final int y, final TrackOrientation orientation) {
		super(x, y, orientation);
	}

	@Override
	public void onClick(final ArcadeTracks game) {
	}

	@Override
	public Track copy() {
		return new TrackHeavy(this.getX(), this.getY(), this.getOrientation());
	}

	@Override
	public int getU() {
		return 2;
	}
}
