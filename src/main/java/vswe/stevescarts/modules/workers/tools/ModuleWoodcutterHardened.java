package vswe.stevescarts.modules.workers.tools;

import net.minecraft.item.ItemStack;
import vswe.stevescarts.entitys.EntityMinecartModular;
import vswe.stevescarts.helpers.ComponentTypes;
import vswe.stevescarts.items.ModItems;

public class ModuleWoodcutterHardened extends ModuleWoodcutter {
	public ModuleWoodcutterHardened(final EntityMinecartModular cart) {
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
	public int getRepairItemUnits(
		@Nonnull
			ItemStack item) {
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
