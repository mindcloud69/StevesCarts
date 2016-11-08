package vswe.stevescarts.Modules.Workers.Tools;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import vswe.stevescarts.Carts.MinecartModular;
import vswe.stevescarts.Helpers.Localization;

public class ModuleDrillDiamond extends ModuleDrill {
	public ModuleDrillDiamond(final MinecartModular cart) {
		super(cart);
	}

	@Override
	protected int blocksOnTop() {
		return 3;
	}

	@Override
	protected int blocksOnSide() {
		return 1;
	}

	@Override
	protected float getTimeMult() {
		return 8.0f;
	}

	@Override
	public int getMaxDurability() {
		return 300000;
	}

	@Override
	public String getRepairItemName() {
		return Localization.MODULES.TOOLS.DIAMONDS.translate();
	}

	@Override
	public int getRepairItemUnits(final ItemStack item) {
		if (item != null && item.getItem() == Items.DIAMOND) {
			return 100000;
		}
		return 0;
	}

	@Override
	public int getRepairSpeed() {
		return 50;
	}

	@Override
	public boolean useDurability() {
		return true;
	}
}
