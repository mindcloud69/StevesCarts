package vswe.stevescarts.Upgrades;

import vswe.stevescarts.Helpers.Localization;

public class TimeFlat extends BaseEffect {
	private int ticks;

	public TimeFlat(final int ticks) {
		this.ticks = ticks;
	}

	@Override
	public String getName() {
		return Localization.UPGRADES.FLAT.translate(((this.getSeconds() >= 0) ? "+" : "") + this.getSeconds(), String.valueOf(this.getSeconds()));
	}

	protected int getSeconds() {
		return this.ticks / 20;
	}

	public int getTicks() {
		return this.ticks;
	}
}
