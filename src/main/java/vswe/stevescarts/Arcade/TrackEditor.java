package vswe.stevescarts.arcade;

public class TrackEditor extends Track {
	private int type;

	public TrackEditor(final TrackOrientation orientation) {
		super(0, 0, orientation);
		this.type = 0;
	}

	@Override
	public Track copy() {
		final TrackEditor newTrack = new TrackEditor(this.getOrientation());
		newTrack.type = this.type;
		return newTrack;
	}

	public Track getRealTrack(final int x, final int y) {
		return getRealTrack(x, y, this.type, this.getOrientation());
	}

	public static Track getRealTrack(final int x, final int y, final int type, final TrackOrientation orientation) {
		switch (type) {
			case 1: {
				return new TrackDetector(x, y, orientation);
			}
			case 2: {
				return new TrackHeavy(x, y, orientation);
			}
			default: {
				return new Track(x, y, orientation);
			}
		}
	}

	@Override
	public int getU() {
		return this.type;
	}

	public int getType() {
		return this.type;
	}

	public void setType(final int val) {
		this.type = val;
	}

	public void nextType() {
		this.type = (this.type + 1) % 3;
	}
}
