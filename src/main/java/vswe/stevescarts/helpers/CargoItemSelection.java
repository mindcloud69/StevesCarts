package vswe.stevescarts.helpers;

import net.minecraft.item.ItemStack;

public class CargoItemSelection {
	private Class validSlot;
	private ItemStack icon;
	private Localization.GUI.CARGO name;

	public CargoItemSelection(final Localization.GUI.CARGO name, final Class validSlot, @Nonnull ItemStack icon) {
		this.name = name;
		this.validSlot = validSlot;
		this.icon = icon;
	}

	public Class getValidSlot() {
		return this.validSlot;
	}

	public ItemStack getIcon() {
		return this.icon;
	}

	public String getName() {
		if (this.name == null) {
			return null;
		}
		return this.name.translate();
	}
}
