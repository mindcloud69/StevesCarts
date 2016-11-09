package vswe.stevescarts.arcade.tracks;

import java.util.ArrayList;

import vswe.stevescarts.guis.GuiMinecart;
import vswe.stevescarts.modules.realtimers.ModuleArcade;

public class TrackDetector extends Track {
	private ArrayList<TrackCoordinate> targets;

	public TrackDetector(final int x, final int y, final TrackOrientation orientation) {
		super(x, y, orientation);
		this.targets = new ArrayList<>();
	}

	@Override
	public Track copy() {
		final TrackDetector newTrack = new TrackDetector(this.getX(), this.getY(), this.getOrientation());
		newTrack.targets = this.targets;
		return newTrack;
	}

	public TrackDetector addTarget(final int x, final int y) {
		if ((int) Math.ceil(this.targets.size() * 1.125f) == 63) {
			return this;
		}
		for (int i = 0; i < this.targets.size(); ++i) {
			if (this.targets.get(i).getX() == x && this.targets.get(i).getY() == y) {
				this.targets.remove(i);
				return this;
			}
		}
		this.targets.add(new TrackCoordinate(x, y));
		return this;
	}

	@Override
	public void setExtraInfo(final byte[] data) {
		int startPosition = 0;
		short content = 0;
		for (int i = 0; i < data.length; ++i) {
			short val = data[i];
			if (val < 0) {
				val += 256;
			}
			content |= (short) ((val & (int) Math.pow(2.0, Math.min(8, 9 - startPosition)) - 1) << startPosition);
			if (startPosition == 0) {
				startPosition = 8;
			} else {
				this.addTarget(content & 0x1F, (content & 0x1E0) >> 5);
				content = (short) ((val & (int) Math.pow(2.0, startPosition - 1) - 1 << 9 - startPosition) >> 9 - startPosition);
				startPosition = (startPosition + 8) % 9;
			}
		}
	}

	@Override
	public byte[] getExtraInfo() {
		final byte[] ret = new byte[(int) Math.ceil(this.targets.size() * 1.125f)];
		int currentByte = 0;
		int startPosition = 0;
		for (int i = 0; i < this.targets.size(); ++i) {
			short data = (short) this.targets.get(i).getX();
			data |= (short) (this.targets.get(i).getY() << 5);
			final byte[] array = ret;
			final int n = currentByte;
			array[n] |= (byte) ((data & (int) Math.pow(2.0, 8 - startPosition) - 1) << startPosition);
			++currentByte;
			ret[currentByte] = (byte) ((data & (int) Math.pow(2.0, 1 + startPosition) - 1 << 8 - startPosition) >> 8 - startPosition);
			startPosition = (startPosition + 1) % 8;
			if (startPosition == 0) {
				++currentByte;
			}
		}
		return ret;
	}

	@Override
	public int getU() {
		return 1;
	}

	@Override
	public void travel(final ArcadeTracks game, final Cart cart) {
		for (final TrackCoordinate target : this.targets) {
			final Track track = game.getTrackMap()[target.getX()][target.getY()];
			if (track != null) {
				track.flip();
			}
		}
	}

	@Override
	public void drawOverlay(final ModuleArcade module, final GuiMinecart gui, final int x, final int y, final boolean isRunning) {
		if (!isRunning && module.inRect(x, y, ArcadeTracks.getTrackArea(this.getX(), this.getY()))) {
			for (final TrackCoordinate target : this.targets) {
				module.drawImage(gui, ArcadeTracks.getTrackArea(target.getX(), target.getY()), 0, 128);
			}
		}
	}

	@Override
	public void onEditorClick(final ArcadeTracks game) {
		game.setEditorDetectorTrack(this);
	}

	private static class TrackCoordinate {
		private int x;
		private int y;

		public TrackCoordinate(final int x, final int y) {
			this.x = x;
			this.y = y;
		}

		public int getX() {
			return this.x;
		}

		public int getY() {
			return this.y;
		}
	}
}
