package stevesvehicles.common.network.packets;

import java.io.IOException;

import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.internal.FMLProxyPacket;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import stevesvehicles.common.container.ContainerVehicle;
import stevesvehicles.common.core.Constants;
import stevesvehicles.common.core.Log;
import stevesvehicles.common.modules.ModuleBase;
import stevesvehicles.common.network.DataReader;
import stevesvehicles.common.network.DataWriter;
import stevesvehicles.common.network.IStreamable;
import stevesvehicles.common.network.PacketHandler;
import stevesvehicles.common.network.PacketType;
import stevesvehicles.common.vehicles.VehicleBase;

public class PacketVehicle implements IServerPacket, IClientPacket {
	public DataWriter dataWriter;

	public PacketVehicle() {
	}

	public PacketVehicle(ModuleBase module, boolean hasInterfaceOpen) throws IOException {
		this.dataWriter = createWriter(module, hasInterfaceOpen);
		module.writeData(dataWriter);
	}

	public PacketVehicle(ModuleBase module, DataWriter dataWriter) throws IOException {
		this.dataWriter = dataWriter;
		module.writeData(dataWriter);
	}

	@Override
	public IPacketProvider getProvider() {
		return PacketType.VEHICLE;
	}

	public static DataWriter createWriter(ModuleBase module, boolean hasInterfaceOpen) throws IOException {
		int entityId;
		if (!hasInterfaceOpen) {
			entityId = module.getVehicle().getEntity().getEntityId();
		} else {
			entityId = -1;
		}
		ByteBufOutputStream buf = new ByteBufOutputStream(Unpooled.buffer());
		DataWriter data = new DataWriter(buf);
		data.writeByte(PacketType.VEHICLE.getPacketID());
		data.writeInt(entityId);
		data.writeInt(module.getPositionId());
		return data;
	}

	@Override
	public final FMLProxyPacket getPacket() {
		try {
			dataWriter.close();
		} catch (IOException e) {
			Log.err("Failed to write packet.", e);
		}
		return new FMLProxyPacket(new PacketBuffer(dataWriter.getOut().buffer()), Constants.MOD_ID);
	}

	@Override
	public void readData(DataReader data) throws IOException {
	}

	@Override
	public void onPacketData(DataReader data, EntityPlayerMP player) throws IOException {
		World world = player.world;
		Container container = player.openContainer;
		if (container instanceof ContainerPlayer) {
			int entityId = data.readInt();
			VehicleBase vehicle = PacketHandler.getVehicle(entityId, world);
			if (vehicle != null) {
				readVehiclePacket(vehicle, data, player);
			}
		} else if (container instanceof ContainerVehicle) {
			ContainerVehicle containerVehicle = (ContainerVehicle) container;
			VehicleBase vehicle = containerVehicle.getVehicle();
			readVehiclePacket(vehicle, data, player);
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void onPacketData(DataReader data, EntityPlayer player) throws IOException {
		int entityId = data.readInt();
		World world = player.world;
		VehicleBase vehicle = PacketHandler.getVehicle(entityId, world);
		if (vehicle != null) {
			readVehiclePacket(vehicle, data, player);
		}
	}

	private void readVehiclePacket(VehicleBase vehicle, DataReader data, EntityPlayer player) throws IOException {
		int id = data.readByte();
		if (id >= 0 && id < vehicle.getModules().size()) {
			ModuleBase module = vehicle.getModules().get(id);
			if (module instanceof IStreamable) {
				((IStreamable) module).readData(data, player);
			}
		}
	}
}
