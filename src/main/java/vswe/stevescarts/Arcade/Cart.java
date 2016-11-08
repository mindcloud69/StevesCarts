package vswe.stevescarts.arcade;

import vswe.stevescarts.guis.GuiMinecart;

public class Cart {
	private int x;
	private int y;
	private TrackOrientation.DIRECTION dir;
	private int imageIndex;
	private boolean enabled;

	public Cart(final int imageIndex) {
		this.imageIndex = imageIndex;
		this.enabled = true;
	}

	public int getX() {
		return this.x;
	}

	public int getY() {
		return this.y;
	}

	public TrackOrientation.DIRECTION getDireciotn() {
		return this.dir;
	}

	public void setX(final int val) {
		this.x = val;
	}

	public void setY(final int val) {
		this.y = val;
	}

	public void setDirection(final TrackOrientation.DIRECTION val) {
		this.dir = val;
	}

	public void setAlive(final boolean val) {
		this.enabled = val;
	}

	public void move(final ArcadeTracks game) {
		if (!this.enabled) {
			return;
		}
		this.x += this.dir.getX();
		this.y += this.dir.getY();
		if (this.x < 0 || this.y < 0 || this.x >= game.getTrackMap().length || this.y >= game.getTrackMap()[0].length || game.getTrackMap()[this.x][this.y] == null) {
			if (this.dir != TrackOrientation.DIRECTION.STILL) {
				this.onCrash();
			}
			this.dir = TrackOrientation.DIRECTION.STILL;
		} else {
			game.getTrackMap()[this.x][this.y].travel(game, this);
			this.dir = game.getTrackMap()[this.x][this.y].getOrientation().travel(this.dir.getOpposite());
		}
		if (game.isItemOnGround() && this.x == game.getItemX() && this.y == game.getItemY()) {
			this.onItemPickUp();
			game.pickItemUp();
		}
	}

	public void onItemPickUp() {
	}

	public void onCrash() {
	}

	public void render(final ArcadeTracks game, final GuiMinecart gui, final int tick) {
		if (!this.enabled) {
			return;
		}
		final int x = 7 + (int) (16.0f * (this.x + this.dir.getX() * (tick / 4.0f)));
		final int y = 7 + (int) (16.0f * (this.y + this.dir.getY() * (tick / 4.0f)));
		final int u = 256 - 12 * (this.imageIndex + 1);
		final int v = 244;
		final int w = 12;
		final int h = 12;
		game.drawImageInArea(gui, x, y, u, v, w, h);
	}

	public boolean isAlive() {
		return this.enabled;
	}
}
