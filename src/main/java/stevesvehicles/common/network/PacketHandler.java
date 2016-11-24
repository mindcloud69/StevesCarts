package stevesvehicles.common.network;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerPlayer;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import stevesvehicles.common.blocks.BlockCartAssembler;
import stevesvehicles.common.blocks.ModBlocks;
import stevesvehicles.common.blocks.tileentitys.TileEntityBase;
import stevesvehicles.common.container.ContainerBase;
import stevesvehicles.common.container.ContainerBuoy;
import stevesvehicles.common.container.ContainerVehicle;
import stevesvehicles.common.modules.ModuleBase;
import stevesvehicles.common.registries.RegistrySynchronizer;
import stevesvehicles.common.vehicles.VehicleBase;
import stevesvehicles.common.vehicles.entitys.EntityModularBoat;
import stevesvehicles.common.vehicles.entitys.IVehicleEntity;

public class PacketHandler {
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onClientPacket(FMLNetworkEvent.ClientCustomPacketEvent event) {
		EntityPlayer player = FMLClientHandler.instance().getClient().player;
		try {
			DataReader dr = new DataReader(event.getPacket().payload());
			PacketType type = dr.readEnum(PacketType.class);
			if (type == PacketType.BLOCK) {
				int x = dr.readSignedInteger();
				int y = dr.readSignedInteger();
				int z = dr.readSignedInteger();
				World world = player.world;
				((BlockCartAssembler) ModBlocks.CART_ASSEMBLER.getBlock()).updateMultiBlock(world, new BlockPos(x, y, z));
			} else if (type == PacketType.VEHICLE) {
				int entityId = dr.readInteger();
				World world = player.world;
				VehicleBase vehicle = getVehicle(entityId, world);
				if (vehicle != null) {
					receivePacketAtVehicle(vehicle, dr, player);
				}
			} else if (type == PacketType.REGISTRY) {
				RegistrySynchronizer.onPacket(dr);
			} else if (type == PacketType.BUOY) {
				Container container = player.openContainer;
				if (container instanceof ContainerBuoy) {
					((ContainerBuoy) container).receiveInfo(dr, false);
				}
			}
		} catch (Exception ex) {
			System.out.println("The client failed to process a packet.");
			ex.printStackTrace();
		}
	}

	@SubscribeEvent
	public void onServerPacket(FMLNetworkEvent.ServerCustomPacketEvent event) {
		EntityPlayer player = ((NetHandlerPlayServer) event.getHandler()).playerEntity;
		try {
			DataReader dr = new DataReader(event.getPacket().payload());
			PacketType type = dr.readEnum(PacketType.class);
			World world = player.world;
			if (type == PacketType.BLOCK || type == PacketType.VEHICLE || type == PacketType.BUOY) {
				Container container = player.openContainer;
				if (container instanceof ContainerPlayer) {
					int entityId = dr.readInteger();
					VehicleBase vehicle = getVehicle(entityId, world);
					if (vehicle != null) {
						receivePacketAtVehicle(vehicle, dr, player);
					}
				} else if (container instanceof ContainerVehicle) {
					ContainerVehicle containerVehicle = (ContainerVehicle) container;
					VehicleBase vehicle = containerVehicle.getVehicle();
					receivePacketAtVehicle(vehicle, dr, player);
				} else if (container instanceof ContainerBuoy) {
					ContainerBuoy containerBuoy = (ContainerBuoy) container;
					containerBuoy.receiveInfo(dr, true);
				} else if (container instanceof ContainerBase) {
					ContainerBase containerBase = (ContainerBase) container;
					TileEntityBase base = containerBase.getTileEntity();
					if (base != null) {
						base.receivePacket(dr, player);
					}
				}
			} else if (type == PacketType.BOAT_MOVEMENT) {
				if (player.getRidingEntity() instanceof EntityModularBoat) {
					((EntityModularBoat) player.getRidingEntity()).onMovementPacket(dr);
				}
			}
		} catch (Exception ex) {
			System.out.println("The server failed to process a packet.");
			ex.printStackTrace();
		}
	}

	private void receivePacketAtVehicle(VehicleBase vehicle, DataReader dr, EntityPlayer player) {
		ModuleBase.delegateReceivedPacket(vehicle, dr, player);
	}

	private VehicleBase getVehicle(int id, World world) {
		Entity entity = world.getEntityByID(id);
		if (entity instanceof IVehicleEntity) {
			return ((IVehicleEntity) entity).getVehicle();
		}
		return null;
	}

	public static DataWriter getDataWriter(PacketType type) {
		DataWriter dw = new DataWriter();
		dw.writeEnum(type);
		return dw;
	}

	public static void sendPacketToServer(DataWriter dw) {
		dw.sendToServer();
	}

	public static void sendPacketToPlayer(DataWriter dw, EntityPlayer player) {
		dw.sendToPlayer((EntityPlayerMP) player);
	}

	public static void sendBlockInfoToClients(World world, byte[] data, int x, int y, int z) {
		DataWriter dw = getDataWriter(PacketType.BLOCK);
		dw.writeInteger(x);
		dw.writeInteger(y);
		dw.writeInteger(z);
		for (byte b : data) {
			dw.writeByte(b);
		}
		dw.sendToAllPlayersAround(world, x, y, z, 64);
	}

	public static void sendBlockInfoToClients(World world, byte[] data, BlockPos pos) {
		sendBlockInfoToClients(world, data, pos.getX(), pos.getY(), pos.getZ());
	}
}
