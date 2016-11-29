package stevesvehicles.common.network.packets;

import java.io.IOException;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import stevesvehicles.api.network.DataReader;
import stevesvehicles.api.network.packets.IPacketClient;
import stevesvehicles.api.network.packets.IPacketProvider;
import stevesvehicles.api.network.packets.IPacketServer;
import stevesvehicles.common.container.ContainerBuoy;
import stevesvehicles.common.network.PacketType;

public class PacketBuoy extends Packet implements IPacketClient, IPacketServer {

	@Override
	public IPacketProvider getProvider() {
		return PacketType.BUOY;
	}

	@Override
	public void onPacketData(DataReader data, EntityPlayerMP player) throws IOException {
		Container container = player.openContainer;
		if(container instanceof ContainerBuoy){
			ContainerBuoy containerBuoy = (ContainerBuoy) player.openContainer;
			containerBuoy.receiveInfo(data, true);
		}
	}

	@Override
	public void onPacketData(DataReader data, EntityPlayer player) throws IOException {
		Container container = player.openContainer;
		if(container instanceof ContainerBuoy){
			ContainerBuoy containerBuoy = (ContainerBuoy) player.openContainer;
			containerBuoy.receiveInfo(data, true);
		}
	}
}
