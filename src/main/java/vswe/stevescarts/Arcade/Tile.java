package vswe.stevescarts.Arcade;

import vswe.stevescarts.Interfaces.GuiMinecart;

public class Tile {
	private int nearbyCreepers;
	private TILE_STATE state;
	private ArcadeSweeper game;

	public Tile(final ArcadeSweeper game) {
		this.state = TILE_STATE.CLOSED;
		this.game = game;
	}

	public void setCreeper() {
		this.nearbyCreepers = 9;
	}

	public void setNearbyCreepers(final int val) {
		this.nearbyCreepers = val;
	}

	public boolean isCreeper() {
		return this.nearbyCreepers == 9;
	}

	public void draw(final ArcadeSweeper game, final GuiMinecart gui, final int x, final int y, final int mx, final int my) {
		final int[] rect = { x, y, 10, 10 };
		if (this.isCreeper() && game.hasFinished) {
			game.getModule().drawImage(gui, rect, 30, 0);
		} else {
			final int u = (this.isOpen() || (this.state == TILE_STATE.FLAGGED && !this.isCreeper() && !game.isPlaying && !game.hasFinished)) ? 0 : (game.getModule().inRect(mx, my, rect) ? 20 : 10);
			game.getModule().drawImage(gui, rect, u, 0);
			if (this.isOpen() && this.nearbyCreepers != 0) {
				game.getModule().drawImage(gui, x + 1, y + 1, (this.nearbyCreepers - 1) * 8, 11, 8, 8);
			}
			if (this.state == TILE_STATE.FLAGGED) {
				if (!game.isPlaying && !this.isCreeper()) {
					game.getModule().drawImage(gui, x + 1, y + 1, 16, 20, 8, 8);
				} else {
					game.getModule().drawImage(gui, x + 1, y + 1, 0, 20, 8, 8);
				}
			} else if (this.state == TILE_STATE.MARKED) {
				game.getModule().drawImage(gui, x + 1, y + 1, 8, 20, 8, 8);
			}
		}
	}

	private boolean isOpen() {
		return (this.isCreeper() && !this.game.isPlaying && !this.game.hasFinished) || this.state == TILE_STATE.OPENED;
	}

	public TILE_OPEN_RESULT open() {
		if (this.state == TILE_STATE.OPENED || this.state == TILE_STATE.FLAGGED) {
			return TILE_OPEN_RESULT.FAILED;
		}
		this.state = TILE_STATE.OPENED;
		if (this.nearbyCreepers == 0) {
			final ArcadeSweeper game = this.game;
			--game.emptyLeft;
			return TILE_OPEN_RESULT.BLOB;
		}
		if (this.isCreeper()) {
			return TILE_OPEN_RESULT.DEAD;
		}
		final ArcadeSweeper game2 = this.game;
		--game2.emptyLeft;
		return TILE_OPEN_RESULT.OK;
	}

	@SuppressWarnings("incomplete-switch")
	public void mark() {
		switch (this.state) {
			case CLOSED: {
				this.state = TILE_STATE.FLAGGED;
				final ArcadeSweeper game = this.game;
				--game.creepersLeft;
				break;
			}
			case FLAGGED: {
				this.state = TILE_STATE.MARKED;
				final ArcadeSweeper game2 = this.game;
				++game2.creepersLeft;
				break;
			}
			case MARKED: {
				this.state = TILE_STATE.CLOSED;
				break;
			}
		}
	}

	public TILE_STATE getState() {
		return this.state;
	}

	public void setState(final TILE_STATE state) {
		this.state = state;
	}

	public int getNearbyCreepers() {
		return this.nearbyCreepers;
	}

	public enum TILE_STATE {
		CLOSED,
		OPENED,
		FLAGGED,
		MARKED
	}

	public enum TILE_OPEN_RESULT {
		OK,
		BLOB,
		FAILED,
		DEAD
	}
}
