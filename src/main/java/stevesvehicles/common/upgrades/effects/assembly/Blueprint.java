package stevesvehicles.common.upgrades.effects.assembly;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import stevesvehicles.common.blocks.tileentitys.assembler.UpgradeContainer;
import stevesvehicles.common.container.slots.SlotCart;
import stevesvehicles.common.modules.datas.ModuleData;
import stevesvehicles.common.modules.datas.ModuleDataItemHandler;
import stevesvehicles.common.upgrades.effects.util.SimpleInventoryEffect;

public class Blueprint extends SimpleInventoryEffect {
	public Blueprint(UpgradeContainer upgrade) {
		super(upgrade, 1, 1);
	}

	@Override
	public Class<? extends Slot> getSlot(int i) {
		return SlotCart.class;
	}

	public boolean isValidForBluePrint(ArrayList<ModuleData> modules, ModuleData module) {
		ItemStack blueprint = upgrade.getStackInSlot(0);
		if (!blueprint.isEmpty()) {
			List<ModuleData> blueprintModules = ModuleDataItemHandler.getModulesFromItem(blueprint);
			if (blueprintModules == null) {
				return false;
			}
			ArrayList<ModuleData> missing = new ArrayList<>();
			for (ModuleData blueprintModule : blueprintModules) {
				int index = modules.indexOf(blueprintModule);
				if (index != -1) {
					modules.remove(index);
				} else {
					missing.add(blueprintModule);
				}
			}
			return missing.contains(module);
		} else {
			// depends on setting, will return false for now
			return false;
		}
	}
}
