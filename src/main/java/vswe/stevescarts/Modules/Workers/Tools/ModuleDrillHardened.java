package vswe.stevescarts.Modules.Workers.Tools;

import net.minecraft.item.ItemStack;
import vswe.stevescarts.Carts.MinecartModular;
import vswe.stevescarts.Helpers.ComponentTypes;
import vswe.stevescarts.Items.ModItems;

public class ModuleDrillHardened extends ModuleDrill {
	public ModuleDrillHardened(final MinecartModular cart) {
		super(cart);
	}

	@Override
	protected int blocksOnTop() {
		return 5;
	}

	@Override
	protected int blocksOnSide() {
		return 2;
	}

	@Override
	protected float getTimeMult() {
		return 4.0f;
	}

	@Override
	public int getMaxDurability() {
		return 1000000;
	}

	@Override
	public String getRepairItemName() {
		return ComponentTypes.REINFORCED_METAL.getLocalizedName();
	}

	@Override
	public int getRepairItemUnits(final ItemStack item) {
		if (item != null && item.getItem() == ModItems.component && item.getItemDamage() == 22) {
			return 450000;
		}
		return 0;
	}

	@Override
	public int getRepairSpeed() {
		return 200;
	}

	@Override
	public boolean useDurability() {
		return true;
	}
}
