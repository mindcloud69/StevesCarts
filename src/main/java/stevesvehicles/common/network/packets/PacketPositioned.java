package stevesvehicles.common.network.packets;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import stevesvehicles.api.network.DataReader;
import stevesvehicles.api.network.DataWriter;
import stevesvehicles.api.network.packets.IPacketProvider;
import stevesvehicles.common.blocks.BlockCartAssembler;
import stevesvehicles.common.blocks.ModBlocks;
import stevesvehicles.common.blocks.tileentitys.TileEntityCartAssembler;
import stevesvehicles.common.blocks.tileentitys.assembler.UpgradeContainer;
import stevesvehicles.common.network.PacketType;
import stevesvehicles.common.upgrades.registries.UpgradeRegistry;

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
