package vswe.stevescarts.Upgrades;

import vswe.stevescarts.Helpers.Localization;

public class FuelCost extends BaseEffect {
	private float cost;

	public FuelCost(final float cost) {
		this.cost = cost;
	}

	@Override
	public String getName() {
		return Localization.UPGRADES.FUEL_COST.translate(((this.getPercentage() >= 0) ? "+" : "") + this.getPercentage());
	}

	private int getPercentage() {
		return (int) (this.cost * 100.0f);
	}

	public float getCost() {
		return this.cost;
	}
}
