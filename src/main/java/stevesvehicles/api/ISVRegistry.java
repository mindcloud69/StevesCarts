package stevesvehicles.api;

import stevesvehicles.api.modules.Module;
import stevesvehicles.api.modules.container.IModuleContainer;
import stevesvehicles.api.modules.data.ILocalizedText;
import stevesvehicles.api.modules.data.IModuleData;
import stevesvehicles.api.modules.data.IModuleDataGroup;
import stevesvehicles.api.modules.data.IModuleType;
import stevesvehicles.api.modules.handlers.IContentHandlerFactory;
import stevesvehicles.api.modules.handlers.ModuleHandlerType;
import stevesvehicles.api.network.packets.IClientPacket;
import stevesvehicles.api.network.packets.IServerPacket;

public interface ISVRegistry {
	void registerModuleContainers(IModuleContainer... containers);

	IModuleData createModuleData(String unlocalizedName, Class<? extends Module> moduleClass, int modularCost);

	IModuleDataGroup getCombinedGroup(String key, ILocalizedText name, IModuleDataGroup mainGroup, IModuleDataGroup... extraGroups);

	IModuleDataGroup createGroup(String key, ILocalizedText name);

	IModuleDataGroup getGroup(String key);

	void registerModuleTypes(IModuleType... types);

	/**
	 * Register a ModuleHandlerType.
	 */
	void registerModuleHandlerTypes(ModuleHandlerType... types);

	/**
	 * Register a IContentHandlerFactory.
	 */
	void registerContentFactory(Class contentClass, IContentHandlerFactory factory);

	void registerClientPackets(IClientPacket... packets);

	void registerServerPackets(IServerPacket... packets);
}
