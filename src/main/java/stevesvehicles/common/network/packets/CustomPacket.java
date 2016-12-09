package stevesvehicles.common.network.packets;

import java.io.IOException;

import io.netty.buffer.ByteBufOutputStream;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.network.internal.FMLProxyPacket;
import stevesvehicles.common.core.Constants;
import stevesvehicles.common.network.DataReader;
import stevesvehicles.common.network.PacketType;

public class CustomPacket implements IServerPacket {
	private final IPacketProvider id = getProvider();
	private final ByteBufOutputStream buf;

	public CustomPacket(ByteBufOutputStream buf) {
		this.buf = buf;
	}

	@Override
	public final FMLProxyPacket getPacket() {
		return new FMLProxyPacket(new PacketBuffer(buf.buffer()), Constants.MOD_ID);
	}

	@Override
	public void readData(DataReader data) throws IOException {
	}

	@Override
	public IPacketProvider getProvider() {
		return PacketType.CUSTOM;
	}

	@Override
	public void onPacketData(DataReader data, EntityPlayerMP player) throws IOException {
	}
}
