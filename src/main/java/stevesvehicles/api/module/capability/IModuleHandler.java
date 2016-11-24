package stevesvehicles.api.module.capability;

import net.minecraft.util.NonNullList;
import stevesvehicles.api.module.IModule;
import stevesvehicles.api.module.container.IModuleContainer;
import stevesvehicles.api.module.data.ModuleData;

/**
 *  An IModuleHandler handle modules in an ItemStack or something else that use the forge capability system.
 */
public interface IModuleHandler {

	/**
	 * Create and add modules to this handler with the {@link ModuleData}s from the container.
	 * 
	 * @return True if the modules were added to the handler.
	 */
	boolean addModules(IModuleContainer container);

	/**
	 * Add a specific module to this handler.
	 * 
	 * @return True if the module was added to the handler.
	 */
	boolean addModule(IModule module);

	/**
	 * Remove a specific module from this handler.
	 * 
	 * @return True if the module was removed to the handler.
	 */
	boolean removeModule(IModule module);

	/**
	 * @return All modules that this handler contains.
	 */
	NonNullList<IModule> getModules();
}
