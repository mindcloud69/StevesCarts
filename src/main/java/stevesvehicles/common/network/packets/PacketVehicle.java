package stevesvehicles.common.network.packets;

import java.io.IOException;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import stevesvehicles.api.network.DataReader;
import stevesvehicles.api.network.DataWriter;
import stevesvehicles.api.network.IStreamable;
import stevesvehicles.api.network.packets.IPacketClient;
import stevesvehicles.api.network.packets.IPacketProvider;
import stevesvehicles.api.network.packets.IPacketServer;
import stevesvehicles.common.blocks.tileentitys.TileEntityBase;
import stevesvehicles.common.container.ContainerBase;
import stevesvehicles.common.container.ContainerVehicle;
import stevesvehicles.common.modules.ModuleBase;
import stevesvehicles.common.network.PacketHandler;
import stevesvehicles.common.network.PacketType;
import stevesvehicles.common.vehicles.VehicleBase;

public class PacketVehicle extends Packet implements IPacketServer, IPacketClient {

	public int entityId;
	public int positionId;
	
	public PacketVehicle() {
		this.entityId = -1;
		this.positionId = 0;
	}
	
	public PacketVehicle(ModuleBase module, boolean hasInterfaceOpen) {
		if(!hasInterfaceOpen){
			this.entityId = module.getVehicle().getEntity().getEntityId();
		}else{
			this.entityId = -1;
		}
		this.positionId = module.getPositionId();
	}
	
	@Override
	protected void writeData(DataWriter data) throws IOException {
		data.writeInt(entityId);
		data.writeInt(positionId);
	}
	
	@Override
	public IPacketProvider getProvider() {
		return PacketType.VEHICLE;
	}

	@Override
	public void onPacketData(DataReader data, EntityPlayerMP player) throws IOException {
		World world = player.world;
		Container container = player.openContainer;
		if (container instanceof ContainerPlayer) {
			int entityId = data.readInt();
			VehicleBase vehicle = PacketHandler.getVehicle(entityId, world);
			if (vehicle != null) {
				receivePacketAtVehicle(vehicle, data, player);
			}
		} else if (container instanceof ContainerVehicle) {
			ContainerVehicle containerVehicle = (ContainerVehicle) container;
			VehicleBase vehicle = containerVehicle.getVehicle();
			receivePacketAtVehicle(vehicle, data, player);
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void onPacketData(DataReader data, EntityPlayer player) throws IOException {
		int entityId = data.readInt();
		World world = player.world;
		VehicleBase vehicle = PacketHandler.getVehicle(entityId, world);
		if (vehicle != null) {
			receivePacketAtVehicle(vehicle, data, player);
		}
	}
	
	private void receivePacketAtVehicle(VehicleBase vehicle, DataReader data, EntityPlayer player) throws IOException {
		int id = data.readByte();
		if (id >= 0 && id < vehicle.getModules().size()) {
			ModuleBase module = vehicle.getModules().get(id);
			if(module instanceof IStreamable){
				((IStreamable) module).readData(data, player);
			}
		}
	}
}
