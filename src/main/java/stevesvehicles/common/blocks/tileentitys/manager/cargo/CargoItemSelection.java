package stevesvehicles.common.blocks.tileentitys.manager.cargo;

import net.minecraft.item.ItemStack;
import stevesvehicles.client.localization.ILocalizedText;

public class CargoItemSelection {
	private Class validSlot;
	private ItemStack icon;
	private ILocalizedText name;

	public CargoItemSelection(ILocalizedText name, Class validSlot, ItemStack icon) {
		this.name = name;
		this.validSlot = validSlot;
		this.icon = icon;
	}

	public Class getValidSlot() {
		return validSlot;
	}

	public ItemStack getIcon() {
		return icon;
	}

	public String getName() {
		if (name == null) {
			return null;
		} else {
			return name.translate();
		}
	}
}
