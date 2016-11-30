package stevesvehicles.common.network.packets;

import java.io.IOException;
import java.util.List;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import stevesvehicles.api.network.DataReader;
import stevesvehicles.api.network.DataWriter;
import stevesvehicles.api.network.packets.IPacketProvider;
import stevesvehicles.api.network.packets.IPacketServer;
import stevesvehicles.common.blocks.tileentitys.TileEntityActivator;
import stevesvehicles.common.blocks.tileentitys.toggler.TogglerOption;
import stevesvehicles.common.network.PacketType;

public class PacketActivator extends PacketPositioned implements IPacketServer {
	private int option;
	private boolean isActive;

	public PacketActivator(TileEntityActivator tile, int option, boolean isActive) {
		super(tile.getPos());
		this.option = option;
		this.isActive = isActive;
	}

	@Override
	protected void writeData(DataWriter data) throws IOException {
		super.writeData(data);
		data.writeInt(option);
		data.writeBoolean(isActive);
	}

	@Override
	public void readData(DataReader data) throws IOException {
		super.readData(data);
		option = data.readInt();
		isActive = data.readBoolean();
	}

	@Override
	public IPacketProvider getProvider() {
		return PacketType.ACTIVATOR;
	}

	@Override
	public void onPacketData(DataReader data, EntityPlayerMP player) throws IOException {
		TileEntity tile = player.world.getTileEntity(getPos());
		if (tile instanceof TileEntityActivator) {
			TileEntityActivator activator = (TileEntityActivator) tile;
			List<TogglerOption> options = activator.getOptions();
			boolean leftClick = data.readBoolean();
			int optionId = data.readByte();
			if (optionId >= 0 && optionId < options.size()) {
				options.get(optionId).changeOption(leftClick);
			}
		}
	}
}
