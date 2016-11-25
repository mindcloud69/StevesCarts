package stevesvehicles.api;

import stevesvehicles.api.modules.container.IModuleContainer;
import stevesvehicles.api.modules.data.ModuleData;
import stevesvehicles.api.modules.handlers.IContentHandlerFactory;
import stevesvehicles.api.modules.handlers.ModuleHandlerType;

public interface ISVRegistry {

	void registerItemContainer(IModuleContainer container);

	/**
	 * Register a ModuleHandlerType.
	 */
	void registerModuleHandlerType(ModuleHandlerType type);
	
	/**
	 * Register a IContentHandlerFactory.
	 */
	void registerContentFactory(Class contentClass, IContentHandlerFactory factory);
}
