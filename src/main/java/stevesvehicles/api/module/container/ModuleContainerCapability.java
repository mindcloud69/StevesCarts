package stevesvehicles.api.module.container;

import net.minecraft.item.ItemStack;
import stevesvehicles.api.module.data.ModuleData;

public class ModuleContainerCapability extends ModuleContainerNBT {

	public ModuleContainerCapability(ItemStack fallbackParent, ModuleData[] datas) {
		super(fallbackParent, datas);
	}

	@Override
	public boolean matches(ItemStack stack) {
		return super.matches(stack) && stack.areCapsCompatible(parent);
	}
}
