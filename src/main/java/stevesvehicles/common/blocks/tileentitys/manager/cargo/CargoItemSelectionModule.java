package stevesvehicles.common.blocks.tileentitys.manager.cargo;

import net.minecraft.item.ItemStack;
import stevesvehicles.client.localization.ILocalizedText;
import stevesvehicles.common.modules.datas.ModuleData;

public class CargoItemSelectionModule extends CargoItemSelection {
	private ModuleData module;

	public CargoItemSelectionModule(ILocalizedText name, Class validSlot, ModuleData module) {
		super(name, validSlot, null);
		this.module = module;
	}

	@Override
	public ItemStack getIcon() {
		return module == null ? null : module.getItemStack();
	}
}
