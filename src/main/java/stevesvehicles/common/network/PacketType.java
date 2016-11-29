package stevesvehicles.common.network;

import stevesvehicles.api.network.packets.IPacketClient;
import stevesvehicles.api.network.packets.IPacketProvider;
import stevesvehicles.api.network.packets.IPacketServer;

public enum PacketType implements IPacketProvider {
	VEHICLE, BLOCK, REGISTRY, BOAT_MOVEMENT, BUOY, GUI_DATA;

	public static final PacketType[] VALUES = values();
	private IPacketServer packetServer;
	private IPacketClient packetClient;
	private int packetID = -1;

	@Override
	public void setPacketClient(IPacketClient packetClient) {
		this.packetClient = packetClient;
	}

	@Override
	public void setPacketServer(IPacketServer packetServer) {
		this.packetServer = packetServer;
	}

	@Override
	public IPacketServer getServerPacket() {
		return packetServer;
	}

	@Override
	public IPacketClient getClientPacket() {
		return packetClient;
	}

	@Override
	public int getPacketID() {
		return packetID;
	}

	@Override
	public void setPacketID(int packetID) {
		if(this.packetID >= 0){
			return;
		}
		this.packetID = packetID;
	}
}
