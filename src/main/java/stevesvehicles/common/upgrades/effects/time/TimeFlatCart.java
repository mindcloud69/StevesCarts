package stevesvehicles.common.upgrades.effects.time;

import stevesvehicles.common.blocks.tileentitys.assembler.UpgradeContainer;
import stevesvehicles.common.upgrades.effects.BaseEffect;

public class TimeFlatCart extends BaseEffect {
	private int ticks;

	public TimeFlatCart(UpgradeContainer upgrade, Integer ticks) {
		super(upgrade);
		this.ticks = ticks;
	}

	protected int getSeconds() {
		return ticks / 20;
	}

	public int getTicks() {
		return ticks;
	}
}
