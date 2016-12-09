package stevesvehicles.common.network.packets;

import java.io.IOException;
import java.util.List;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import stevesvehicles.common.blocks.tileentitys.TileEntityActivator;
import stevesvehicles.common.blocks.tileentitys.toggler.TogglerOption;
import stevesvehicles.common.network.DataReader;
import stevesvehicles.common.network.DataWriter;
import stevesvehicles.common.network.PacketType;

public class PacketActivator extends PacketPositioned implements IServerPacket {
	private int option;
	private boolean leftClick;

	public PacketActivator() {
	}

	public PacketActivator(TileEntityActivator tile, int option, boolean leftClick) {
		super(tile.getPos());
		this.option = option;
		this.leftClick = leftClick;
	}

	@Override
	protected void writeData(DataWriter data) throws IOException {
		super.writeData(data);
		data.writeInt(option);
		data.writeBoolean(leftClick);
	}

	@Override
	public void readData(DataReader data) throws IOException {
		super.readData(data);
		option = data.readInt();
		leftClick = data.readBoolean();
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
			if (option >= 0 && option < options.size()) {
				options.get(option).changeOption(leftClick);
			}
		}
	}
}
