package stevesvehicles.common.network;

import stevesvehicles.api.network.packets.IClientPacket;
import stevesvehicles.api.network.packets.IPacketProvider;
import stevesvehicles.api.network.packets.IServerPacket;

public enum PacketType implements IPacketProvider {
	VEHICLE, REGISTRY, BUOY, GUI_DATA, CUSTOM, STREAMABLE, ACTIVATOR, UPGRADES, ASSEMBLER, MANAGER, DETECTOR, DISTRIBUTOR;
	public static final PacketType[] VALUES = values();
	private IServerPacket packetServer;
	private IClientPacket packetClient;
	private int packetID = -1;

	@Override
	public void setPacketClient(IClientPacket packetClient) {
		this.packetClient = packetClient;
	}

	@Override
	public void setPacketServer(IServerPacket packetServer) {
		this.packetServer = packetServer;
	}

	@Override
	public IServerPacket getServerPacket() {
		return packetServer;
	}

	@Override
	public IClientPacket getClientPacket() {
		return packetClient;
	}

	@Override
	public int getPacketID() {
		return packetID;
	}

	@Override
	public void setPacketID(int packetID) {
		if (this.packetID >= 0) {
			return;
		}
		this.packetID = packetID;
	}
}
