package stevesvehicles.api.network.packets;

import java.io.IOException;

import net.minecraftforge.fml.common.network.internal.FMLProxyPacket;
import stevesvehicles.api.network.DataReader;

public interface IPacket {
	void readData(DataReader reader) throws IOException;

	FMLProxyPacket getPacket();

	IPacketProvider getProvider();
}
