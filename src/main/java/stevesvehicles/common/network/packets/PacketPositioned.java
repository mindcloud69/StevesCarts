package stevesvehicles.common.network.packets;

import java.io.IOException;
import net.minecraft.util.math.BlockPos;
import stevesvehicles.common.network.DataReader;
import stevesvehicles.common.network.DataWriter;

public abstract class PacketPositioned extends Packet {
	private BlockPos pos;

	public PacketPositioned() {
		this.pos = null;
	}

	public PacketPositioned(BlockPos pos) {
		this.pos = pos;
	}

	public BlockPos getPos() {
		return pos;
	}

	@Override
	public void readData(DataReader data) throws IOException {
		int x = data.readInt();
		int y = data.readInt();
		int z = data.readInt();
		pos = new BlockPos(x, y, z);
	}

	@Override
	protected void writeData(DataWriter data) throws IOException {
		data.writeInt(pos.getX());
		data.writeInt(pos.getY());
		data.writeInt(pos.getZ());
	}
}
