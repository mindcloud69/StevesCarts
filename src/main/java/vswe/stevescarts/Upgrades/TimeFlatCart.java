package vswe.stevescarts.upgrades;

import vswe.stevescarts.helpers.Localization;

public class TimeFlatCart extends BaseEffect {
	private int ticks;

	public TimeFlatCart(final int ticks) {
		this.ticks = ticks;
	}

	@Override
	public String getName() {
		return Localization.UPGRADES.CART_FLAT.translate(((this.getSeconds() >= 0) ? "+" : "") + this.getSeconds(), String.valueOf(this.getSeconds()));
	}

	protected int getSeconds() {
		return this.ticks / 20;
	}

	public int getTicks() {
		return this.ticks;
	}
}
