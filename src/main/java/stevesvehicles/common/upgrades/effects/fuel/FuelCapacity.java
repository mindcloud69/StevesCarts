package stevesvehicles.common.upgrades.effects.fuel;

import stevesvehicles.common.blocks.tileentitys.TileEntityUpgrade;
import stevesvehicles.common.upgrades.effects.BaseEffect;

public class FuelCapacity extends BaseEffect {
	private int capacity;

	public FuelCapacity(TileEntityUpgrade upgrade, Integer capacity) {
		super(upgrade);
		this.capacity = capacity;
	}

	public int getFuelCapacity() {
		return capacity;
	}
}
