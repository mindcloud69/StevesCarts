package vswe.stevescarts.Modules.Workers.Tools;

import net.minecraft.item.ItemStack;
import vswe.stevescarts.Carts.MinecartModular;

public class ModuleWoodcutterGalgadorian extends ModuleWoodcutter {
	public ModuleWoodcutterGalgadorian(final MinecartModular cart) {
		super(cart);
	}

	@Override
	public int getPercentageDropChance() {
		return 125;
	}

	@Override
	public int getMaxDurability() {
		return 1;
	}

	@Override
	public String getRepairItemName() {
		return null;
	}

	@Override
	public int getRepairItemUnits(final ItemStack item) {
		return 0;
	}

	@Override
	public boolean useDurability() {
		return false;
	}

	@Override
	public int getRepairSpeed() {
		return 1;
	}
}
