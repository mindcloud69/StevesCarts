package stevesvehicles.api.network.packets;

import java.io.IOException;

import net.minecraft.entity.player.EntityPlayerMP;
import stevesvehicles.api.network.DataReader;

public interface IServerPacket extends IPacket {
	void onPacketData(DataReader data, EntityPlayerMP player) throws IOException;
}
