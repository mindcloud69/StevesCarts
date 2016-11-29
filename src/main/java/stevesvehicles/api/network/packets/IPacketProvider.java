package stevesvehicles.api.network.packets;

public interface IPacketProvider {

	void setPacketClient(IPacketClient packetClient);

	void setPacketServer(IPacketServer packetServer);

	IPacketServer getServerPacket();

	IPacketClient getClientPacket();

	int getPacketID();

	void setPacketID(int packetID);
}
