package stevesvehicles.common.network.packets;

import java.io.IOException;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import stevesvehicles.api.network.DataReader;
import stevesvehicles.api.network.DataWriter;
import stevesvehicles.api.network.packets.IClientPacket;
import stevesvehicles.api.network.packets.IPacketProvider;
import stevesvehicles.api.network.packets.IServerPacket;
import stevesvehicles.common.container.ContainerBuoy;
import stevesvehicles.common.network.PacketType;

public class PacketBuoy extends Packet implements IClientPacket, IServerPacket {
	private int entityId;
	private boolean next;

	public PacketBuoy() {
	}

	public PacketBuoy(int entityId, boolean next) {
		this.entityId = entityId;
		this.next = next;
	}

	@Override
	protected void writeData(DataWriter data) throws IOException {
		super.writeData(data);
		data.writeInt(entityId);
		data.writeBoolean(next);
	}

	@Override
	public IPacketProvider getProvider() {
		return PacketType.BUOY;
	}

	@Override
	public void onPacketData(DataReader data, EntityPlayerMP player) throws IOException {
		Container container = player.openContainer;
		if (container instanceof ContainerBuoy) {
			ContainerBuoy containerBuoy = (ContainerBuoy) player.openContainer;
			containerBuoy.receiveInfo(data, true);
		}
	}

	@Override
	public void onPacketData(DataReader data, EntityPlayer player) throws IOException {
		Container container = player.openContainer;
		if (container instanceof ContainerBuoy) {
			ContainerBuoy containerBuoy = (ContainerBuoy) player.openContainer;
			containerBuoy.receiveInfo(data, false);
		}
	}
}
