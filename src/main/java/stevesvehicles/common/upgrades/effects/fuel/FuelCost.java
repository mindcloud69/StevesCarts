package stevesvehicles.common.upgrades.effects.fuel;

import stevesvehicles.common.blocks.tileentitys.assembler.UpgradeContainer;
import stevesvehicles.common.upgrades.effects.BaseEffect;

public class FuelCost extends BaseEffect {
	private float cost;

	public FuelCost(UpgradeContainer upgrade, Float cost) {
		super(upgrade);
		this.cost = cost;
	}

	public float getCost() {
		return cost;
	}
}
