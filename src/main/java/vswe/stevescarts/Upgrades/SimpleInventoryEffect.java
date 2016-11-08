package vswe.stevescarts.Upgrades;

public abstract class SimpleInventoryEffect extends InventoryEffect {
	private int inventoryWidth;
	private int inventoryHeight;

	public SimpleInventoryEffect(final int inventoryWidth, final int inventoryHeight) {
		this.inventoryWidth = inventoryWidth;
		this.inventoryHeight = inventoryHeight;
	}

	@Override
	public int getInventorySize() {
		return this.inventoryWidth * this.inventoryHeight;
	}

	@Override
	public int getSlotX(final int id) {
		return (256 - 18 * this.inventoryWidth) / 2 + id % this.inventoryWidth * 18;
	}

	@Override
	public int getSlotY(final int id) {
		return (107 - 18 * this.inventoryHeight) / 2 + id / this.inventoryWidth * 18;
	}
}
