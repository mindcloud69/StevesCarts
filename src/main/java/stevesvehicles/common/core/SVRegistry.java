package stevesvehicles.common.core;

import stevesvehicles.api.ISVRegistry;
import stevesvehicles.api.modules.container.IModuleContainer;
import stevesvehicles.api.modules.handlers.IContentHandlerFactory;
import stevesvehicles.api.modules.handlers.ModuleHandlerType;
import stevesvehicles.api.network.packets.IClientPacket;
import stevesvehicles.api.network.packets.IServerPacket;
import stevesvehicles.common.network.PacketHandler;

public class SVRegistry implements ISVRegistry {

	@Override
	public void registerModuleContainer(IModuleContainer container) {
		// TODO ADD

	}

	@Override
	public void registerModuleHandlerType(ModuleHandlerType type) {
		// TODO ADD

	}

	@Override
	public void registerContentFactory(Class contentClass, IContentHandlerFactory factory) {
		// TODO ADD

	}

	@Override
	public void registerClientPackets(IClientPacket... packets) {
		for(IClientPacket packet : packets){
			PacketHandler.registerClientPacket(packet);
		}
	}

	@Override
	public void registerServerPackets(IServerPacket... packets) {
		for(IServerPacket packet : packets){
			PacketHandler.registerServerPacket(packet);
		}
	}
}
