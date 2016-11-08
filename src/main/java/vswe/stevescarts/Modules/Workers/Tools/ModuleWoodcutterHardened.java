package vswe.stevescarts.Modules.Workers.Tools;

import net.minecraft.item.ItemStack;
import vswe.stevescarts.Carts.MinecartModular;
import vswe.stevescarts.Helpers.ComponentTypes;
import vswe.stevescarts.Items.ModItems;

public class ModuleWoodcutterHardened extends ModuleWoodcutter {
	public ModuleWoodcutterHardened(final MinecartModular cart) {
		super(cart);
	}

	@Override
	public int getPercentageDropChance() {
		return 100;
	}

	@Override
	public int getMaxDurability() {
		return 640000;
	}

	@Override
	public String getRepairItemName() {
		return ComponentTypes.REINFORCED_METAL.getLocalizedName();
	}

	@Override
	public int getRepairItemUnits(final ItemStack item) {
		if (item != null && item.getItem() == ModItems.component && item.getItemDamage() == 22) {
			return 320000;
		}
		return 0;
	}

	@Override
	public int getRepairSpeed() {
		return 400;
	}
}
