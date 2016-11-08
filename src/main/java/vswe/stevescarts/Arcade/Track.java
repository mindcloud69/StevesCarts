package vswe.stevescarts.Arcade;

import java.util.ArrayList;

import vswe.stevescarts.Interfaces.GuiBase;
import vswe.stevescarts.Interfaces.GuiMinecart;
import vswe.stevescarts.Modules.Realtimers.ModuleArcade;

public class Track {
	private int x;
	private int y;
	private int v;
	private GuiBase.RENDER_ROTATION rotation;
	private TrackOrientation orientation;
	private TrackOrientation orientationBackup;

	public Track(final int x, final int y, final TrackOrientation orientation) {
		this.x = x;
		this.y = y;
		this.setOrientation(orientation);
	}

	private void setV(final int v) {
		this.v = v;
	}

	private void setRotation(final GuiBase.RENDER_ROTATION rotation) {
		this.rotation = rotation;
	}

	public void setOrientation(final TrackOrientation orientation) {
		this.orientation = orientation;
		this.setV(orientation.getV());
		this.setRotation(orientation.getRotation());
	}

	public int getX() {
		return this.x;
	}

	public int getY() {
		return this.y;
	}

	public int getU() {
		return 0;
	}

	public int getV() {
		return this.v;
	}

	public GuiBase.RENDER_ROTATION getRotation() {
		return this.rotation;
	}

	public TrackOrientation getOrientation() {
		return this.orientation;
	}

	public void onClick(final ArcadeTracks game) {
		this.flip();
	}

	public void onEditorClick(final ArcadeTracks game) {
		if (this.orientation.getOpposite() != null && game.getEditorDetectorTrack() != null) {
			game.getEditorDetectorTrack().addTarget(this.getX(), this.getY());
		}
	}

	public void flip() {
		if (this.orientation.getOpposite() != null) {
			ArcadeGame.playSound("gearswitch", 1.0f, 1.0f);
			this.setOrientation(this.orientation.getOpposite());
		}
	}

	public void saveBackup() {
		this.orientationBackup = this.orientation;
	}

	public void loadBackup() {
		this.setOrientation(this.orientationBackup);
	}

	public Track copy() {
		return new Track(this.x, this.y, this.orientation);
	}

	public void travel(final ArcadeTracks game, final Cart cart) {
	}

	public void drawOverlay(final ModuleArcade module, final GuiMinecart gui, final int x, final int y, final boolean isRunning) {
	}

	public static void addTrack(final ArrayList<Track> tracks, final int x1, final int y1, final int x2, final int y2) {
		if (x1 != x2 && y1 != y2) {
			final TrackOrientation corner = getCorner(x1 >= x2, y1 < y2);
			int x2h;
			if (x1 < (x2h = x2)) {
				--x2h;
			} else {
				++x2h;
			}
			int y1v = y1;
			if (y1 < y2) {
				++y1v;
			} else {
				--y1v;
			}
			addHorizontalTrack(tracks, x1, x2h, y1);
			tracks.add(new Track(x2, y1, corner));
			addVerticalTrack(tracks, x2, y1v, y2);
		} else if (x1 != x2) {
			addHorizontalTrack(tracks, x1, x2, y1);
		} else {
			addVerticalTrack(tracks, x1, y1, y2);
		}
	}

	private static TrackOrientation getCorner(final boolean right, final boolean down) {
		if (right) {
			if (down) {
				return TrackOrientation.CORNER_DOWN_RIGHT;
			}
			return TrackOrientation.CORNER_UP_RIGHT;
		} else {
			if (down) {
				return TrackOrientation.CORNER_DOWN_LEFT;
			}
			return TrackOrientation.CORNER_UP_LEFT;
		}
	}

	private static void addHorizontalTrack(final ArrayList<Track> tracks, int x1, int x2, final int y) {
		if (x1 > x2) {
			final int temp = x1;
			x1 = x2;
			x2 = temp;
		}
		for (int x3 = x1; x3 <= x2; ++x3) {
			tracks.add(new Track(x3, y, TrackOrientation.STRAIGHT_HORIZONTAL));
		}
	}

	private static void addVerticalTrack(final ArrayList<Track> tracks, final int x, int y1, int y2) {
		if (y1 > y2) {
			final int temp = y1;
			y1 = y2;
			y2 = temp;
		}
		for (int y3 = y1; y3 <= y2; ++y3) {
			tracks.add(new Track(x, y3, TrackOrientation.STRAIGHT_VERTICAL));
		}
	}

	public void setExtraInfo(final byte[] data) {
	}

	public byte[] getExtraInfo() {
		return new byte[0];
	}
}
