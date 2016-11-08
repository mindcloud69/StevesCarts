package vswe.stevescarts.Arcade;

import vswe.stevescarts.Helpers.Localization;

public class LevelMessage {
	private int x;
	private int y;
	private int w;
	private Localization.STORIES.THE_BEGINNING message;
	private int isRunning;
	private int isStill;
	private int isDone;

	public LevelMessage(final int x, final int y, final int w, final Localization.STORIES.THE_BEGINNING message) {
		this.x = x;
		this.y = y;
		this.w = w;
		this.message = message;
	}

	public int getX() {
		return this.x;
	}

	public int getY() {
		return this.y;
	}

	public int getW() {
		return this.w;
	}

	public String getMessage() {
		return this.message.translate();
	}

	public LevelMessage setMustBeRunning() {
		this.isRunning = 1;
		return this;
	}

	public LevelMessage setMustNotBeRunning() {
		this.isRunning = -1;
		return this;
	}

	public LevelMessage setMustBeStill() {
		this.isStill = 1;
		return this;
	}

	public LevelMessage setMustNotBeStill() {
		this.isStill = -1;
		return this;
	}

	public LevelMessage setMustBeDone() {
		this.isDone = 1;
		return this;
	}

	public LevelMessage setMustNotBeDone() {
		this.isDone = -1;
		return this;
	}

	public boolean isVisible(final boolean isRunning, final boolean isStill, final boolean isDone) {
		return (this.isRunning == 0 || this.isRunning > 0 == isRunning) && (this.isStill == 0 || this.isStill > 0 == isStill) && (this.isDone == 0 || this.isDone > 0 == isDone);
	}
}
