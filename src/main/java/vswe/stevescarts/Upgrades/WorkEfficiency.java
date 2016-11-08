package vswe.stevescarts.Upgrades;

import vswe.stevescarts.Helpers.Localization;

public class WorkEfficiency extends BaseEffect {
	private float efficiency;

	public WorkEfficiency(final float efficiency) {
		this.efficiency = efficiency;
	}

	@Override
	public String getName() {
		return Localization.UPGRADES.EFFICIENCY.translate(((this.getPercentage() >= 0) ? "+" : "") + this.getPercentage());
	}

	private int getPercentage() {
		return (int) (this.efficiency * 100.0f);
	}

	public float getEfficiency() {
		return this.efficiency;
	}
}
