package vswe.stevescarts.modules.storages.chests;

import vswe.stevescarts.entitys.MinecartModular;

public class ModuleExtractingChests extends ModuleChest {
	private final float startOffset;
	private final float endOffset;
	private float chestOffset;

	public ModuleExtractingChests(final MinecartModular cart) {
		super(cart);
		this.startOffset = -14.0f;
		this.endOffset = -24.5f;
		this.chestOffset = -14.0f;
	}

	@Override
	protected int getInventoryWidth() {
		return 18;
	}

	@Override
	protected int getInventoryHeight() {
		return 4;
	}

	@Override
	protected float chestFullyOpenAngle() {
		return 1.5707964f;
	}

	@Override
	protected void handleChest() {
		if (this.isChestActive() && this.lidClosed() && this.chestOffset > this.endOffset) {
			this.chestOffset -= 0.5f;
		} else if (!this.isChestActive() && this.lidClosed() && this.chestOffset < this.startOffset) {
			this.chestOffset += 0.5f;
		} else {
			super.handleChest();
		}
	}

	public float getChestOffset() {
		return this.chestOffset;
	}
}
