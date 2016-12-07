package stevesvehicles.api.modules.handlers;

import javax.annotation.Nullable;

import net.minecraft.util.NonNullList;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import stevesvehicles.api.modules.Module;
import stevesvehicles.api.modules.container.IModuleContainer;
import stevesvehicles.common.modules.ModuleData;

/**
 * An IModuleHandler handle modules in an ItemStack or something else that use
 * the forge capability system.
 */
public interface IModuleHandler<P extends ICapabilityProvider> {
	/**
	 * Create and add modules to this handler with the {@link ModuleData}s from
	 * the container.
	 * 
	 * @return True if the modules were added to the handler.
	 */
	boolean addModules(IModuleContainer container);

	/**
	 * Add a specific module to this handler.
	 * 
	 * @return True if the module was added to the handler.
	 */
	boolean addModule(Module module);

	/**
	 * Get the module that is at that position at the module list.
	 * 
	 * If at this position is no module, it will return null.
	 */
	@Nullable
	Module getModule(int position);

	/**
	 * Remove a specific module from this handler.
	 * 
	 * @return True if the module was removed from the handler.
	 */
	boolean removeModule(Module module);

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
	NonNullList<Module> getModules();

	/**
	 * 
	 * @return The type of this handler. Need to be
	 */
	ModuleHandlerType getType();

	/**
	 * 
	 * @return True if this handler is a place holder for a gui or somthing like this.
	 */
	boolean isPlaceholder();

	P getProvider();
}
