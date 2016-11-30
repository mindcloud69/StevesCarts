package stevesvehicles.api;

import stevesvehicles.api.modules.container.IModuleContainer;
import stevesvehicles.api.modules.handlers.IContentHandlerFactory;
import stevesvehicles.api.modules.handlers.ModuleHandlerType;
import stevesvehicles.api.network.packets.IPacketClient;
import stevesvehicles.api.network.packets.IPacketServer;

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

	void registerClientPackets(IPacketClient... packets);

	void registerServerPackets(IPacketServer... packets);
}
