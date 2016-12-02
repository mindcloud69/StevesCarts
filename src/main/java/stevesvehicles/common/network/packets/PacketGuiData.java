package stevesvehicles.common.network.packets;

import java.io.IOException;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.tileentity.TileEntity;
import stevesvehicles.api.network.DataReader;
import stevesvehicles.api.network.DataWriter;
import stevesvehicles.api.network.packets.IClientPacket;
import stevesvehicles.api.network.packets.IPacketProvider;
import stevesvehicles.common.blocks.tileentitys.TileEntityBase;
import stevesvehicles.common.network.PacketType;

public class PacketGuiData extends PacketPositioned implements IClientPacket {
	public TileEntityBase tileEntity;
	public Container container;

	public PacketGuiData() {
	}

	public PacketGuiData(TileEntityBase tileEntity, Container container) {
		super(tileEntity.getPos());
		this.tileEntity = tileEntity;
		this.container = container;
	}

	@Override
	protected void writeData(DataWriter data) throws IOException {
		super.writeData(data);
		tileEntity.updateGuiData(data, container);
	}

	@Override
	public void readData(DataReader data) throws IOException {
		super.readData(data);
	}

	@Override
	public IPacketProvider getProvider() {
		return PacketType.GUI_DATA;
	}

	@Override
	public void onPacketData(DataReader data, EntityPlayer player) throws IOException {
		TileEntity tileEntity = player.world.getTileEntity(getPos());
		if (tileEntity instanceof TileEntityBase) {
			TileEntityBase baseTile = (TileEntityBase) tileEntity;
			baseTile.receiveGuiData(data);
		}
	}
}
