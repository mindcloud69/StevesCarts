package stevesvehicles.common.container.slots;

import stevesvehicles.common.blocks.tileentitys.assembler.UpgradeContainer;
import stevesvehicles.common.tanks.Tank;

public class SlotLiquidUpgradeInput extends SlotLiquidInput {
	private UpgradeContainer upgrade;

	public SlotLiquidUpgradeInput(UpgradeContainer upgrade, Tank tank, int maxsize, int id, int x, int y) {
		super(upgrade, tank, maxsize, id, x, y);
		this.upgrade = upgrade;
	}
}
