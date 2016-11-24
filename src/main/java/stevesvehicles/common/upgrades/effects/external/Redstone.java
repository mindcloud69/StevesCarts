package stevesvehicles.common.upgrades.effects.external;

import stevesvehicles.common.blocks.tileentitys.TileEntityUpgrade;
import stevesvehicles.common.upgrades.effects.BaseEffect;

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
