package stevesvehicles.api.modules.handlers;

import javax.annotation.Nullable;

import net.minecraft.util.NonNullList;
import stevesvehicles.api.modules.IModule;
import stevesvehicles.api.modules.container.IModuleContainer;
import stevesvehicles.api.modules.data.ModuleData;

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
	 * Get the module that is at that position at the module list.
	 * 
	 * If at this position is no module, it will return null.
	 */
	@Nullable
	IModule getModule(int position);

	/**
	 * Remove a specific module from this handler.
	 * 
	 * @return True if the module was removed from the handler.
	 */
	boolean removeModule(IModule module);

	/**
	 * Remove the module from this handler that is at this specific position.
	 * 
	 * If at this position is no module, it will return false.
	 * 
	 * @return True if the module was removed from the handler. 
	 */
	boolean removeModule(int position);

	/**
	 * @return All modules that this handler contains.
	 */
	NonNullList<IModule> getModules();

	/**
	 * 
	 * @return The type of this handler. Need to be 
	 */
	ModuleHandlerType getType();
}
