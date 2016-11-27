package stevesvehicles.common.upgrades.effects.fuel;

import stevesvehicles.common.blocks.tileentitys.assembler.UpgradeContainer;
import stevesvehicles.common.upgrades.effects.BaseEffect;

public abstract class RechargerBase extends BaseEffect {
	protected RechargerBase(UpgradeContainer upgrade) {
		super(upgrade);
	}

	private int cooldown;

	@Override
	public void update() {
		if (!upgrade.getMaster().getWorld().isRemote && canGenerate()) {
			if (cooldown >= 1200 / getAmount()) {
				cooldown = 0;
				upgrade.getMaster().increaseFuel(1);
			} else {
				cooldown++;
			}
		}
	}

	protected abstract boolean canGenerate();

	protected abstract int getAmount();

	@Override
	public void init() {
		cooldown = 0;
	}
}
