package stevesvehicles.common.network.packets;

import java.io.IOException;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import stevesvehicles.api.network.DataReader;
import stevesvehicles.api.network.DataWriter;
import stevesvehicles.api.network.packets.IPacketProvider;
import stevesvehicles.api.network.packets.IServerPacket;
import stevesvehicles.common.blocks.tileentitys.TileEntityDistributor;
import stevesvehicles.common.network.PacketType;

public class PacketDistributor extends PacketPositioned implements IServerPacket {
	private int activeId;
	private int sideId;
	private boolean bool;

	public PacketDistributor() {
	}

	public PacketDistributor(TileEntityDistributor distributor, int activeId, int sideId, boolean bool) {
		super(distributor.getPos());
	}

	@Override
	protected void writeData(DataWriter data) throws IOException {
		super.writeData(data);
		data.writeByte(activeId);
		data.writeByte(sideId);
		data.writeBoolean(bool);
	}

	@Override
	public void onPacketData(DataReader data, EntityPlayerMP player) throws IOException {
		TileEntity tile = player.world.getTileEntity(getPos());
		if (tile instanceof TileEntityDistributor) {
			TileEntityDistributor distributor = (TileEntityDistributor) tile;
			distributor.readData(data, player);
		}
	}

	@Override
	public IPacketProvider getProvider() {
		return PacketType.DISTRIBUTOR;
	}
}
