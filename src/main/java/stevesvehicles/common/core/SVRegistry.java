package stevesvehicles.common.core;

import stevesvehicles.api.ISVRegistry;
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
import stevesvehicles.common.network.PacketHandler;

public class SVRegistry implements ISVRegistry {

	@Override
	public void registerModuleContainers(IModuleContainer... containers) {
		// TODO ADD

	}

	@Override
	public void registerModuleHandlerTypes(ModuleHandlerType... types) {
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

	@Override
	public IModuleData createModuleData(String unlocalizedName, Class<? extends Module> moduleClass, int modularCost) {
		return null;
	}

	@Override
	public IModuleDataGroup getCombinedGroup(String key, ILocalizedText name, IModuleDataGroup mainGroup, IModuleDataGroup... extraGroups) {
		return null;
	}

	@Override
	public IModuleDataGroup createGroup(String key, ILocalizedText name) {
		return null;
	}

	@Override
	public IModuleDataGroup getGroup(String key) {
		return null;
	}

	@Override
	public void registerModuleTypes(IModuleType... types) {
		
	}
}
