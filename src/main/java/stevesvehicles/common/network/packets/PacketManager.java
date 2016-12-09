package stevesvehicles.common.network.packets;

import java.io.IOException;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import stevesvehicles.common.blocks.tileentitys.TileEntityManager;
import stevesvehicles.common.blocks.tileentitys.TileEntityManager.PacketId;
import stevesvehicles.common.network.DataReader;
import stevesvehicles.common.network.DataWriter;
import stevesvehicles.common.network.PacketType;

public class PacketManager extends PacketPositioned implements IServerPacket {
	private TileEntityManager.PacketId id;
	private int railId;
	private boolean dif;

	public PacketManager() {
	}

	public PacketManager(TileEntityManager manager, TileEntityManager.PacketId id, int railId, boolean dif) {
		super(manager.getPos());
		this.id = id;
		this.railId = railId;
		this.dif = dif;
	}

	@Override
	protected void writeData(DataWriter data) throws IOException {
		super.writeData(data);
		data.writeEnum(id, PacketId.values());
		data.writeInt(railId);
		data.writeBoolean(dif);
	}

	@Override
	public void onPacketData(DataReader data, EntityPlayerMP player) throws IOException {
		TileEntity tile = player.world.getTileEntity(getPos());
		if (tile instanceof TileEntityManager) {
			TileEntityManager manager = (TileEntityManager) tile;
			manager.readData(data, player);
		}
	}

	@Override
	public IPacketProvider getProvider() {
		return PacketType.MANAGER;
	}
}
