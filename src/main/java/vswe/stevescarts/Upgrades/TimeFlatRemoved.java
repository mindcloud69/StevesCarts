package vswe.stevescarts.Upgrades;

import vswe.stevescarts.Helpers.Localization;

public class TimeFlatRemoved extends TimeFlat {
	public TimeFlatRemoved(final int ticks) {
		super(ticks);
	}

	@Override
	public String getName() {
		return Localization.UPGRADES.FLAT_REMOVED.translate(((this.getSeconds() >= 0) ? "+" : "") + this.getSeconds(), String.valueOf(this.getSeconds()));
	}
}
