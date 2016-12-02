package stevesvehicles.common.network.packets;

import java.io.IOException;
import java.util.List;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import stevesvehicles.api.network.DataReader;
import stevesvehicles.api.network.DataWriter;
import stevesvehicles.api.network.packets.IPacketProvider;
import stevesvehicles.api.network.packets.IServerPacket;
import stevesvehicles.common.blocks.tileentitys.TileEntityDetector;
import stevesvehicles.common.blocks.tileentitys.detector.LogicObject;
import stevesvehicles.common.network.PacketType;

public class PacketDetector extends PacketPositioned implements IServerPacket {
	private List<LogicObject> objects;
	private int id;

	public PacketDetector() {
	}

	public PacketDetector(TileEntityDetector detector, List<LogicObject> objects) {
		super(detector.getPos());
		this.objects = objects;
	}

	public PacketDetector(TileEntityDetector detector, int id) {
		super(detector.getPos());
		this.id = id;
	}

	@Override
	protected void writeData(DataWriter data) throws IOException {
		super.writeData(data);
		if (objects != null) {
			data.writeBoolean(true);
			data.writeByte(objects.size());
			for (LogicObject object : objects) {
				data.writeByte(object.getParent().getId());
				data.writeByte(object.getType());
				data.writeShort(object.getData());
			}
		} else {
			data.writeBoolean(false);
			data.writeInt(id);
		}
	}

	@Override
	public void onPacketData(DataReader data, EntityPlayerMP player) throws IOException {
		TileEntity tile = player.world.getTileEntity(getPos());
		if (tile instanceof TileEntityDetector) {
			TileEntityDetector detector = (TileEntityDetector) tile;
			detector.handlePacket(data, player);
		}
	}

	@Override
	public IPacketProvider getProvider() {
		return PacketType.DETECTOR;
	}
}
