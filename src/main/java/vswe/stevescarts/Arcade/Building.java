package vswe.stevescarts.Arcade;

import vswe.stevescarts.Interfaces.GuiMinecart;

public class Building extends Unit {
	public Building(final ArcadeInvaders game, final int x, final int y) {
		super(game, x, y);
		this.health = 10;
	}

	@Override
	public void draw(final GuiMinecart gui) {
		this.game.getModule().drawImage(gui, this.x, this.y, 32 + (10 - this.health) * 16, 16, 16, 16);
	}

	@Override
	protected int getHitboxWidth() {
		return 16;
	}

	@Override
	protected int getHitboxHeight() {
		return 16;
	}

	@Override
	protected boolean isObstacle() {
		return true;
	}

	@Override
	public UPDATE_RESULT update() {
		if (super.update() == UPDATE_RESULT.DEAD) {
			return UPDATE_RESULT.DEAD;
		}
		for (final Unit invader : this.game.invaders) {
			if (!invader.dead && this.collidesWith(invader)) {
				this.dead = true;
				this.health = 0;
				return UPDATE_RESULT.DEAD;
			}
		}
		return UPDATE_RESULT.DONE;
	}
}
