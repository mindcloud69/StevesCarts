package stevesvehicles.common.network.packets;

import java.io.IOException;

import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.network.internal.FMLProxyPacket;
import stevesvehicles.common.core.Constants;
import stevesvehicles.common.network.DataReader;
import stevesvehicles.common.network.DataWriter;

public abstract class Packet implements IPacket {
	private final IPacketProvider id = getProvider();

	@Override
	public final FMLProxyPacket getPacket() {
		ByteBufOutputStream buf = new ByteBufOutputStream(Unpooled.buffer());
		DataWriter data = new DataWriter(buf);
		try {
			data.writeByte(id.getPacketID());
			writeData(data);
		} catch (IOException e) {
			// TODO: create Log
			// Log.err("Failed to write packet.", e);
		}
		return new FMLProxyPacket(new PacketBuffer(buf.buffer()), Constants.MOD_ID);
	}

	protected void writeData(DataWriter data) throws IOException {
	}

	@Override
	public void readData(DataReader data) throws IOException {
	}
}
