package vswe.stevescarts.Upgrades;

import vswe.stevescarts.Helpers.Localization;

public class FuelCapacity extends BaseEffect {
	private int capacity;

	public FuelCapacity(final int capacity) {
		this.capacity = capacity;
	}

	@Override
	public String getName() {
		return Localization.UPGRADES.FUEL_CAPACITY.translate(((this.capacity >= 0) ? "+" : "") + this.capacity);
	}

	public int getFuelCapacity() {
		return this.capacity;
	}
}
