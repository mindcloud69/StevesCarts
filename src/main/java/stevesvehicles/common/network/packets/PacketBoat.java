package stevesvehicles.common.network.packets;

import java.io.IOException;

import net.minecraft.entity.player.EntityPlayerMP;
import stevesvehicles.api.network.DataReader;
import stevesvehicles.api.network.DataWriter;
import stevesvehicles.api.network.packets.IPacketClient;
import stevesvehicles.api.network.packets.IPacketProvider;
import stevesvehicles.api.network.packets.IPacketServer;
import stevesvehicles.common.modules.ModuleBase;
import stevesvehicles.common.network.PacketType;
import stevesvehicles.common.vehicles.entitys.EntityModularBoat;

public class PacketBoat extends Packet implements IPacketServer{
	
	public PacketBoat() {
	}
	
	@Override
	public IPacketProvider getProvider() {
		return PacketType.BOAT_MOVEMENT;
	}

	@Override
	public void onPacketData(DataReader data, EntityPlayerMP player) throws IOException {
		if (player.getRidingEntity() instanceof EntityModularBoat) {
			((EntityModularBoat) player.getRidingEntity()).onMovementPacket(data);
		}
	}
}
