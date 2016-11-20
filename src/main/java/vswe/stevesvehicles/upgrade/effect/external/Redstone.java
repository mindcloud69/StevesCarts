package vswe.stevesvehicles.upgrade.effect.external;

import vswe.stevesvehicles.tileentity.TileEntityUpgrade;
import vswe.stevesvehicles.upgrade.effect.BaseEffect;

public class Redstone extends BaseEffect {
	public Redstone(TileEntityUpgrade upgrade) {
		super(upgrade);
	}

	@Override
	public void update() {
		if (upgrade.getWorld().isBlockIndirectlyGettingPowered(upgrade.getPos()) > 0) {
			if (upgrade.getMaster() != null) {
				upgrade.getMaster().doAssemble();
			}
		}
	}
}
