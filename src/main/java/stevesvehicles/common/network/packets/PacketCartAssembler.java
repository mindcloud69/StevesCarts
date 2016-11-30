package stevesvehicles.common.network.packets;

import java.io.IOException;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import stevesvehicles.api.network.DataReader;
import stevesvehicles.api.network.DataWriter;
import stevesvehicles.api.network.packets.IPacketProvider;
import stevesvehicles.api.network.packets.IPacketServer;
import stevesvehicles.common.blocks.tileentitys.TileEntityCartAssembler;
import stevesvehicles.common.network.PacketType;

public class PacketCartAssembler extends PacketPositioned implements IPacketServer {

	private byte packetID;
	private short param;

	public PacketCartAssembler() {
	}

	public PacketCartAssembler(TileEntityCartAssembler assembler, byte packetID, short param) {
		super(assembler.getPos());
		this.packetID = packetID;
		this.param = param;
	}

	@Override
	protected void writeData(DataWriter data) throws IOException {
		super.writeData(data);
		data.writeByte(packetID);
		if(packetID == 1){
			data.writeByte(param);
		}else if(packetID == 2){
			data.writeShort(param);
		}
	}

	@Override
	public void onPacketData(DataReader data, EntityPlayerMP player) throws IOException {
		TileEntity tile = player.world.getTileEntity(getPos());
		if(tile instanceof TileEntityCartAssembler){
			TileEntityCartAssembler assembler = (TileEntityCartAssembler) tile;
			assembler.receivePacket(data, player);
		}
	}

	@Override
	public IPacketProvider getProvider() {
		return PacketType.ASSEMBLER;
	}
}
