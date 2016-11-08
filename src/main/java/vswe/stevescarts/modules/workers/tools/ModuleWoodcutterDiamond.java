package vswe.stevescarts.modules.workers.tools;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import vswe.stevescarts.entitys.EntityMinecartModular;
import vswe.stevescarts.helpers.Localization;

public class ModuleWoodcutterDiamond extends ModuleWoodcutter {
	public ModuleWoodcutterDiamond(final EntityMinecartModular cart) {
		super(cart);
	}

	@Override
	public int getPercentageDropChance() {
		return 80;
	}

	@Override
	public int getMaxDurability() {
		return 320000;
	}

	@Override
	public String getRepairItemName() {
		return Localization.MODULES.TOOLS.DIAMONDS.translate();
	}

	@Override
	public int getRepairItemUnits(final ItemStack item) {
		if (item != null && item.getItem() == Items.DIAMOND) {
			return 160000;
		}
		return 0;
	}

	@Override
	public int getRepairSpeed() {
		return 150;
	}
}
