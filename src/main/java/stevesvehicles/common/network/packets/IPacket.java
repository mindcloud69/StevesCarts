package stevesvehicles.common.network.packets;

import java.io.IOException;

import net.minecraftforge.fml.common.network.internal.FMLProxyPacket;
import stevesvehicles.common.network.DataReader;

public interface IPacket {
	void readData(DataReader reader) throws IOException;

	FMLProxyPacket getPacket();

	IPacketProvider getProvider();
}
