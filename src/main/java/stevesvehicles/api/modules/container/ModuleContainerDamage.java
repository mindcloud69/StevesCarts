package stevesvehicles.api.modules.container;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import stevesvehicles.api.modules.data.ModuleData;

public class ModuleContainerDamage extends ModuleContainer {
	public ModuleContainerDamage(ItemStack parent, ModuleData[] datas) {
		super(parent, datas);
	}

	@Override
	public boolean matches(ItemStack stack) {
		return super.matches(stack) && (stack.getItemDamage() == parent.getItemDamage() || parent.getItemDamage() == OreDictionary.WILDCARD_VALUE);
	}
}
