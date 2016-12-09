package stevesvehicles.common.network.packets;

public interface IPacketProvider {
	void setPacketClient(IClientPacket packetClient);

	void setPacketServer(IServerPacket packetServer);

	IServerPacket getServerPacket();

	IClientPacket getClientPacket();

	int getPacketID();

	void setPacketID(int packetID);
}
