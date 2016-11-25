package stevesvehicles.api.modules.container;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import stevesvehicles.api.modules.data.ModuleData;

public class ModuleContainerNBT extends ModuleContainer {

	public ModuleContainerNBT(ItemStack parent, ModuleData[] datas) {
		super(parent, datas);
	}

	@Override
	public boolean matches(ItemStack stack) {
		return ItemStack.areItemStackTagsEqual(stack, parent) && (stack.getItemDamage() == parent.getItemDamage() || parent.getItemDamage() == OreDictionary.WILDCARD_VALUE);
	}
}
