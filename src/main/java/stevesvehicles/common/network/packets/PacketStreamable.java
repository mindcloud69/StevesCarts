package stevesvehicles.common.network.packets;

import java.io.IOException;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import stevesvehicles.api.network.DataReader;
import stevesvehicles.api.network.DataWriter;
import stevesvehicles.api.network.IStreamable;
import stevesvehicles.api.network.packets.IPacketProvider;
import stevesvehicles.api.network.packets.IPacketServer;
import stevesvehicles.common.network.PacketType;

public class PacketStreamable extends Packet implements IPacketServer {

	public Object object;
	public IStreamable streamable;

	public PacketStreamable() {
	}

	public PacketStreamable(IStreamable streamable, BlockPos pos) {
		this.streamable = streamable;
		this.object = pos;
	}

	public PacketStreamable(IStreamable streamable, int entityID) {
		this.streamable = streamable;
		this.object = new Integer(entityID);
	}

	@Override
	protected void writeData(DataWriter data) throws IOException {
		if(object instanceof BlockPos){
			data.writeInt(0);
			BlockPos pos = (BlockPos) object;
			data.writeInt(pos.getX());
			data.writeInt(pos.getY());
			data.writeInt(pos.getZ());
		}else if(object instanceof Integer){
			data.writeInt(1);
			data.writeInt(((Integer) object).intValue());
		}
		streamable.writeData(data);
	}

	@Override
	public IPacketProvider getProvider() {
		return PacketType.STREAMABLE;
	}

	@Override
	public void onPacketData(DataReader data, EntityPlayerMP player) throws IOException {
		World world = player.world;
		int type = data.readInt();
		if(type == 0){
			BlockPos pos = new BlockPos(data.readInt(), data.readInt(), data.readInt());
			TileEntity tile = world.getTileEntity(pos);
			if(streamable instanceof IStreamable){
				streamable = (IStreamable) tile;
			}
		}else if(type == 1){
			int entityID = data.readInt();
			Entity entity = world.getEntityByID(entityID);
			if(entity instanceof IStreamable){
				streamable = (IStreamable) entity;
			}
		}
		if(streamable != null){
			streamable.readData(data, player);
		}
	}
}
