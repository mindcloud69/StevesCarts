package stevesvehicles.api.module.data;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.IForgeRegistryEntry.Impl;
import stevesvehicles.api.module.IModule;
import stevesvehicles.api.module.container.IModuleContainer;

public class ModuleData extends Impl<ModuleData> {

	public IModule createModule(IModuleContainer container, ItemStack stack){
		return null;
	}

	/**
	 *  @return The name of the module in guis or tooltips.
	 */
	public String getDisplayName(){
		return null;
	}
}
