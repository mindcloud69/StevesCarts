package stevesvehicles.api.modules.container;

import net.minecraft.item.ItemStack;
import stevesvehicles.common.modules.ModuleData;

public class ModuleContainerCapability extends ModuleContainerNBT {
	public ModuleContainerCapability(ItemStack parent, ModuleData... datas) {
		super(parent, datas);
	}

	@Override
	public boolean matches(ItemStack stack) {
		return super.matches(stack) && stack.areCapsCompatible(parent);
	}
}
