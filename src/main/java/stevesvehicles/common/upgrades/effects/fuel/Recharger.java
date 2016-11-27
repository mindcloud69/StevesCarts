package stevesvehicles.common.upgrades.effects.fuel;

import stevesvehicles.common.blocks.tileentitys.assembler.UpgradeContainer;

public class Recharger extends RechargerBase {
	protected int amount;

	public Recharger(UpgradeContainer upgrade, Integer amount) {
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
