package stevesvehicles.common.upgrades.effects.external;

import stevesvehicles.common.blocks.tileentitys.assembler.UpgradeContainer;
import stevesvehicles.common.upgrades.effects.BaseEffect;

public class Redstone extends BaseEffect {
	public Redstone(UpgradeContainer upgrade) {
		super(upgrade);
	}

	@Override
	public void update() {
		if (upgrade.getMaster().getWorld().getRedstonePower(upgrade.getMaster().getPos(), upgrade.getFacing()) > 0) {
			if (upgrade.getMaster() != null) {
				upgrade.getMaster().doAssemble();
			}
		}
	}
}
