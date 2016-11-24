package stevesvehicles.common.container.slots;

import stevesvehicles.common.blocks.tileentitys.TileEntityUpgrade;
import stevesvehicles.common.tanks.Tank;

public class SlotLiquidUpgradeInput extends SlotLiquidInput {
	private TileEntityUpgrade upgrade;

	public SlotLiquidUpgradeInput(TileEntityUpgrade upgrade, Tank tank, int maxsize, int id, int x, int y) {
		super(upgrade, tank, maxsize, id, x, y);
		this.upgrade = upgrade;
	}
}
