package stevesvehicles.common.upgrades.effects.assembly;

import stevesvehicles.common.blocks.tileentitys.TileEntityUpgrade;
import stevesvehicles.common.upgrades.effects.BaseEffect;

public class WorkEfficiency extends BaseEffect {
	private float efficiency;

	public WorkEfficiency(TileEntityUpgrade upgrade, Float efficiency) {
		super(upgrade);
		this.efficiency = efficiency;
	}

	public float getEfficiency() {
		return efficiency;
	}
}
