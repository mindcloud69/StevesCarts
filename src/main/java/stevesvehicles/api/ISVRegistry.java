package stevesvehicles.api;

import stevesvehicles.api.modules.container.IModuleContainer;
import stevesvehicles.api.modules.handlers.IContentHandlerFactory;
import stevesvehicles.api.modules.handlers.ModuleHandlerType;
import stevesvehicles.api.network.packets.IClientPacket;
import stevesvehicles.api.network.packets.IServerPacket;

public interface ISVRegistry {
	void registerModuleContainer(IModuleContainer container);

	/**
	 * Register a ModuleHandlerType.
	 */
	void registerModuleHandlerType(ModuleHandlerType type);

	/**
	 * Register a IContentHandlerFactory.
	 */
	void registerContentFactory(Class contentClass, IContentHandlerFactory factory);

	void registerClientPackets(IClientPacket... packets);

	void registerServerPackets(IServerPacket... packets);
}
