package vswe.stevescarts.Arcade;

import vswe.stevescarts.Interfaces.GuiMinecart;

public class Projectile extends Unit {
	protected boolean playerProjectile;

	public Projectile(final ArcadeInvaders game, final int x, final int y, final boolean playerProjectile) {
		super(game, x, y);
		this.playerProjectile = playerProjectile;
	}

	@Override
	public void draw(final GuiMinecart gui) {
		if (this.playerProjectile) {
			this.game.getModule().drawImage(gui, this.x, this.y, 38, 0, 5, 16);
		} else {
			this.game.getModule().drawImage(gui, this.x, this.y, 32, 0, 6, 6);
		}
	}

	@Override
	protected void hitCalculation() {
	}

	@Override
	public UPDATE_RESULT update() {
		if (super.update() == UPDATE_RESULT.DEAD) {
			return UPDATE_RESULT.DEAD;
		}
		this.y += (this.playerProjectile ? -5 : 5);
		if (this.y < 0 || this.y > 168) {
			this.dead = true;
			return UPDATE_RESULT.DEAD;
		}
		return UPDATE_RESULT.DONE;
	}

	@Override
	protected int getHitboxWidth() {
		return this.playerProjectile ? 5 : 6;
	}

	@Override
	protected int getHitboxHeight() {
		return this.playerProjectile ? 16 : 6;
	}
}
