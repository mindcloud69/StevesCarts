package stevesvehicles.common.upgrades.effects.fuel;

import stevesvehicles.common.blocks.tileentitys.TileEntityUpgrade;

public class Recharger extends RechargerBase {
	protected int amount;

	public Recharger(TileEntityUpgrade upgrade, Integer amount) {
		super(upgrade);
		this.amount = amount;
	}

	@Override
	protected int getAmount() {
		return amount;
	}

	@Override
	protected boolean canGenerate() {
		return true;
	}
}
