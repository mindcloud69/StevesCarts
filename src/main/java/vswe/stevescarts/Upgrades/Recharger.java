package vswe.stevescarts.Upgrades;

import vswe.stevescarts.Helpers.Localization;
import vswe.stevescarts.TileEntities.TileEntityUpgrade;

public class Recharger extends RechargerBase {
	protected int amount;

	public Recharger(final int amount) {
		this.amount = amount;
	}

	@Override
	protected int getAmount(final TileEntityUpgrade upgrade) {
		return this.amount;
	}

	@Override
	protected boolean canGenerate(final TileEntityUpgrade upgrade) {
		return true;
	}

	@Override
	public String getName() {
		return Localization.UPGRADES.GENERATOR.translate(String.valueOf(this.amount), String.valueOf(this.amount));
	}
}
