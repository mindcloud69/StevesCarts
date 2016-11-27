package stevesvehicles.common.upgrades.effects.util;

import stevesvehicles.common.blocks.tileentitys.assembler.UpgradeContainer;

public abstract class SimpleInventoryEffect extends InventoryEffect {
	private int inventoryWidth;
	private int inventoryHeight;

	public SimpleInventoryEffect(UpgradeContainer upgrade, Integer inventoryWidth, Integer inventoryHeight) {
		super(upgrade);
		this.inventoryWidth = inventoryWidth;
		this.inventoryHeight = inventoryHeight;
	}

	@Override
	public int getInventorySize() {
		return inventoryWidth * inventoryHeight;
	}

	@Override
	public int getSlotX(int id) {
		return (256 - 18 * inventoryWidth) / 2 + (id % inventoryWidth) * 18;
	}

	@Override
	public int getSlotY(int id) {
		return (107 - 18 * inventoryHeight) / 2 + (id / inventoryWidth) * 18;
	}
}
