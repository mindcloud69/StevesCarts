package stevesvehicles.api.module.container;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import stevesvehicles.api.module.data.ModuleData;

public class ModuleContainerDamage extends ModuleContainer {

	public ModuleContainerDamage(ItemStack fallbackParent, ModuleData[] datas) {
		super(fallbackParent, datas);
	}

	@Override
	public boolean matches(ItemStack stack) {
		return super.matches(stack) && (stack.getItemDamage() == parent.getItemDamage() || parent.getItemDamage() == OreDictionary.WILDCARD_VALUE);
	}
}
