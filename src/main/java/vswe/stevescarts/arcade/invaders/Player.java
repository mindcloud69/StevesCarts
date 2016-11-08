package vswe.stevescarts.arcade.invaders;

import vswe.stevescarts.guis.GuiMinecart;

public class Player extends Unit {
	protected boolean ready;
	private int targetX;
	private int targetY;

	public Player(final ArcadeInvaders game, final int x, final int y) {
		super(game, x, y);
	}

	public Player(final ArcadeInvaders game) {
		this(game, 200, 150);
		this.ready = true;
	}

	@Override
	public void draw(final GuiMinecart gui) {
		if (this.ready || this.targetY == this.y) {
			this.game.drawImageInArea(gui, this.x, this.y, 16, 16, 16, 16);
		} else {
			this.game.drawImageInArea(gui, this.x, this.y, 16, 16, 16, 16, 3, 0, 1000, 1000);
		}
	}

	protected void setTarget(final int x, final int y) {
		this.targetX = x;
		this.targetY = y;
	}

	@Override
	public UPDATE_RESULT update() {
		if (!this.ready) {
			if (this.targetY == this.y && this.targetX == this.x) {
				this.ready = true;
			} else if (this.targetY == this.y) {
				this.x = Math.min(this.targetX, this.x + 8);
			} else if (this.x == -15) {
				this.y = Math.max(this.targetY, this.y - 8);
			} else {
				this.x = Math.max(-15, this.x - 8);
			}
		} else if (super.update() == UPDATE_RESULT.DEAD) {
			return UPDATE_RESULT.DEAD;
		}
		return UPDATE_RESULT.DONE;
	}

	public void move(final int dir) {
		this.x += dir * 5;
		if (this.x < 10) {
			this.x = 10;
		} else if (this.x > 417) {
			this.x = 417;
		}
	}

	@Override
	protected boolean isPlayer() {
		return true;
	}

	@Override
	protected int getHitboxWidth() {
		return 16;
	}

	@Override
	protected int getHitboxHeight() {
		return 16;
	}
}
