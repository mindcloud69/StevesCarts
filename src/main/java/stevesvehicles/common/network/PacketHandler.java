package stevesvehicles.common.network;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.google.common.collect.MultimapBuilder.SortedSetMultimapBuilder;

import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerPlayer;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.server.management.PlayerChunkMap;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLEventChannel;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientCustomPacketEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ServerCustomPacketEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.internal.FMLProxyPacket;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import stevesvehicles.common.blocks.BlockCartAssembler;
import stevesvehicles.common.blocks.ModBlocks;
import stevesvehicles.common.blocks.tileentitys.TileEntityBase;
import stevesvehicles.common.blocks.tileentitys.TileEntityCartAssembler;
import stevesvehicles.common.container.ContainerBase;
import stevesvehicles.common.container.ContainerBuoy;
import stevesvehicles.common.container.ContainerVehicle;
import stevesvehicles.common.core.Constants;
import stevesvehicles.common.modules.ModuleBase;
import stevesvehicles.common.network.packets.CustomPacket;
import stevesvehicles.common.network.packets.PacketBuoy;
import stevesvehicles.common.network.packets.PacketCartAssembler;
import stevesvehicles.common.network.packets.PacketGuiData;
import stevesvehicles.common.network.packets.PacketVehicle;
import stevesvehicles.api.network.DataReader;
import stevesvehicles.api.network.DataWriter;
import stevesvehicles.api.network.packets.IPacket;
import stevesvehicles.api.network.packets.IPacketClient;
import stevesvehicles.api.network.packets.IPacketProvider;
import stevesvehicles.api.network.packets.IPacketServer;
import stevesvehicles.common.registries.RegistrySynchronizer;
import stevesvehicles.common.upgrades.registries.UpgradeRegistry;
import stevesvehicles.common.vehicles.VehicleBase;
import stevesvehicles.common.vehicles.entitys.EntityModularBoat;
import stevesvehicles.common.vehicles.entitys.IVehicleEntity;

public class PacketHandler {
	private static final List<IPacketProvider> PROVIDERS = new LinkedList<>();
	int ids;

	public PacketHandler() {
		channel.register(this);
		//Client Packets
		registerClientPacket(new PacketCartAssembler());
		registerClientPacket(new PacketBuoy());
		registerClientPacket(new PacketGuiData());
		registerClientPacket(new PacketVehicle());
		//Server Packets
		registerServerPacket(new PacketBuoy());
		registerServerPacket(new PacketVehicle());
	}

	public void registerProvider(IPacketProvider provider){
		PROVIDERS.add(ids++, provider);
		provider.setPacketID(ids);
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onClientPacket(FMLNetworkEvent.ClientCustomPacketEvent event) {
		EntityPlayer player = FMLClientHandler.instance().getClient().player;
		try {
			DataReader dr = new DataReader(event.getPacket().payload());
			PacketType type = dr.readEnum(PacketType.class);
			if (type == PacketType.REGISTRY) {
				RegistrySynchronizer.onPacket(dr);
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
				if (container instanceof ContainerBase) {
					ContainerBase containerBase = (ContainerBase) container;
					TileEntityBase base = containerBase.getTileEntity();
					if (base != null) {
						base.receivePacket(dr, player);
					}
				}
			}
		} catch (Exception ex) {
			System.out.println("The server failed to process a packet.");
			ex.printStackTrace();
		}
	}

	public static VehicleBase getVehicle(int id, World world) {
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

	@SideOnly(Side.CLIENT)
	public static void sendToServer(DataWriter dw) {
		ByteBufOutputStream buf = new ByteBufOutputStream(Unpooled.buffer());
		sendToServer(new CustomPacket(buf));
	}

	public static void sendPacketToPlayer(DataWriter dw, EntityPlayer player) {
		dw.sendToPlayer((EntityPlayerMP) player);
	}

	private final static FMLEventChannel channel = NetworkRegistry.INSTANCE.newEventDrivenChannel(Constants.MOD_ID);

	public static void registerClientPacket(IPacketClient packet) {
		packet.getProvider().setPacketClient(packet);
	}

	public static void registerServerPacket(IPacketServer packet) {
		packet.getProvider().setPacketServer(packet);
	}

	public static void sendToNetwork(IPacketClient packet, BlockPos pos, WorldServer world) {
		if (packet == null) {
			return;
		}
		WorldServer worldServer = world;
		PlayerChunkMap playerManager = worldServer.getPlayerChunkMap();
		int chunkX = pos.getX() >> 4;
		int chunkZ = pos.getZ() >> 4;
		for (Object playerObj : world.playerEntities) {
			if (playerObj instanceof EntityPlayerMP) {
				EntityPlayerMP player = (EntityPlayerMP) playerObj;
				if (playerManager.isPlayerWatchingChunk(player, chunkX, chunkZ)) {
					sendToPlayer(packet, player);
				}
			}
		}
	}

	@SideOnly(Side.CLIENT)
	public static void sendToServer(IPacketServer packet) {
		NetHandlerPlayClient netHandler = Minecraft.getMinecraft().getConnection();
		if (netHandler != null) {
			netHandler.sendPacket(packet.getPacket());
		}
	}

	public static void sendToPlayer(IPacketClient packet, EntityPlayer entityplayer) {
		if (!(entityplayer instanceof EntityPlayerMP) || entityplayer instanceof FakePlayer) {
			return;
		}
		EntityPlayerMP player = (EntityPlayerMP) entityplayer;
		sendPacket(packet.getPacket(), player);
	}

	public static void sendPacket(FMLProxyPacket packet, EntityPlayerMP player) {
		channel.sendTo(packet, player);
	}

	@SubscribeEvent
	public void onPacket(ServerCustomPacketEvent event) {
		DataReader data = getStream(event.getPacket());
		EntityPlayerMP player = ((NetHandlerPlayServer) event.getHandler()).playerEntity;
		try {
			byte packetIdOrdinal = data.readByte();
			IPacketProvider packet = PROVIDERS.get(packetIdOrdinal);
			IPacketServer packetHandler = packet.getServerPacket();
			checkThreadAndEnqueue(packetHandler, data, player, player.getServerWorld());
		} catch (IOException e) {
			//TODO: create Log
			//Log.err("Failed to read packet.", e);
		}
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onPacket(ClientCustomPacketEvent event) {
		DataReader data = getStream(event.getPacket());
		EntityPlayer player = Minecraft.getMinecraft().player;
		try {
			byte packetIdOrdinal = data.readByte();
			IPacketProvider packet = PROVIDERS.get(packetIdOrdinal);
			IPacketClient packetHandler = packet.getClientPacket();
			checkThreadAndEnqueue(packetHandler, data, player, Minecraft.getMinecraft());
		} catch (IOException e) {
			//TODO: create Log
			//Log.err("Failed to read packet.", e);
		}
	}

	private static DataReader getStream(FMLProxyPacket fmlPacket) {
		InputStream is = new ByteBufInputStream(fmlPacket.payload());
		return new DataReader(is);
	}

	@SideOnly(Side.CLIENT)
	private static void checkThreadAndEnqueue(final IPacketClient packet, final DataReader data, final EntityPlayer player, IThreadListener threadListener) {
		if (!threadListener.isCallingFromMinecraftThread()) {
			threadListener.addScheduledTask(new Runnable() {

				@Override
				public void run() {
					try {
						packet.readData(data);
						packet.onPacketData(data, player);
					} catch (IOException e) {
						//TODO: create Log
						//Log.err("Network Error", e);
					}
				}
			});
		}
	}

	private static void checkThreadAndEnqueue(final IPacketServer packet, final DataReader data, final EntityPlayerMP player, IThreadListener threadListener) {
		if (!threadListener.isCallingFromMinecraftThread()) {
			threadListener.addScheduledTask(new Runnable() {

				@Override
				public void run() {
					try {
						packet.readData(data);
						packet.onPacketData(data, player);
					} catch (IOException e) {
						//TODO: create Log
						//Log.err("Network Error", e);
					}
				}
			});
		}
	}
}
