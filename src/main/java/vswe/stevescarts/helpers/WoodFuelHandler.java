package vswe.stevescarts.helpers;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.IFuelHandler;
import net.minecraftforge.fml.common.registry.GameRegistry;
import vswe.stevescarts.items.ItemCartComponent;
import vswe.stevescarts.items.ModItems;

public class WoodFuelHandler implements IFuelHandler {
	public WoodFuelHandler() {
		GameRegistry.registerFuelHandler(this);
	}

	@Override
	public int getBurnTime(final ItemStack fuel) {
		if (fuel != null && fuel.getItem() != null && fuel.getItem() == ModItems.component) {
			if (ItemCartComponent.isWoodLog(fuel)) {
				return 150;
			}
			if (ItemCartComponent.isWoodTwig(fuel)) {
				return 50;
			}
		}
		return 0;
	}
}
