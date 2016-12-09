package stevesvehicles.common.network.packets;

import java.io.IOException;

import net.minecraft.entity.player.EntityPlayerMP;
import stevesvehicles.common.network.DataReader;

public interface IServerPacket extends IPacket {
	void onPacketData(DataReader data, EntityPlayerMP player) throws IOException;
}
