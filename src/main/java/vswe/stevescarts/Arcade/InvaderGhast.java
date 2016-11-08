package vswe.stevescarts.Arcade;

import vswe.stevescarts.Interfaces.GuiMinecart;

public class InvaderGhast extends Unit {
	private int tentacleTextureId;
	private int shooting;
	protected boolean isPahighast;
	private boolean hasTarget;
	private int targetX;
	private int targetY;

	public InvaderGhast(final ArcadeInvaders game, final int x, final int y) {
		super(game, x, y);
		this.tentacleTextureId = game.getModule().getCart().rand.nextInt(4);
		this.shooting = -10;
		if (game.canSpawnPahighast && !game.hasPahighast && game.getModule().getCart().rand.nextInt(1000) == 0) {
			this.isPahighast = true;
			game.hasPahighast = true;
		}
	}

	@Override
	public void draw(final GuiMinecart gui) {
		if (this.isPahighast) {
			this.game.drawImageInArea(gui, this.x, this.y, 32, 32, 16, 16);
		} else {
			this.game.drawImageInArea(gui, this.x, this.y, (this.shooting > -10) ? 16 : 0, 0, 16, 16);
		}
		this.game.drawImageInArea(gui, this.x, this.y + 16, 0, 16 + 8 * this.tentacleTextureId, 16, 8);
	}

	@Override
	public UPDATE_RESULT update() {
		if (this.hasTarget) {
			boolean flag = false;
			if (this.x != this.targetX) {
				if (this.x > this.targetX) {
					this.x = Math.max(this.targetX, this.x - 4);
				} else {
					this.x = Math.min(this.targetX, this.x + 4);
				}
				flag = true;
			}
			if (this.y != this.targetY) {
				if (this.y > this.targetY) {
					this.y = Math.max(this.targetY, this.y - 4);
				} else {
					this.y = Math.min(this.targetY, this.y + 4);
				}
				flag = true;
			}
			return flag ? UPDATE_RESULT.TARGET : UPDATE_RESULT.DONE;
		}
		if (super.update() == UPDATE_RESULT.DEAD) {
			return UPDATE_RESULT.DEAD;
		}
		if (this.shooting > -10) {
			if (this.shooting == 0) {
				final ArcadeInvaders game = this.game;
				ArcadeGame.playDefaultSound("mob.ghast.fireball", 0.1f, 1.0f);
				this.game.projectiles.add(new Projectile(this.game, this.x + 8 - 3, this.y + 8 - 3, false));
			}
			--this.shooting;
		}
		if (this.game.moveDown > 0) {
			++this.y;
		} else {
			this.x += this.game.moveDirection * this.game.moveSpeed;
			if (this.y > 130) {
				return UPDATE_RESULT.GAME_OVER;
			}
			if (this.x > 417 || this.x < 10) {
				return UPDATE_RESULT.TURN_BACK;
			}
		}
		if (!this.isPahighast && this.shooting == -10 && this.game.getModule().getCart().rand.nextInt(300) == 0) {
			this.shooting = 10;
		}
		return UPDATE_RESULT.DONE;
	}

	@Override
	protected int getHitboxWidth() {
		return 16;
	}

	@Override
	protected int getHitboxHeight() {
		return 24;
	}

	public void setTarget(final int x, final int y) {
		this.hasTarget = true;
		this.targetX = x;
		this.targetY = y;
	}
}
