package stevesvehicles.common.network;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import io.netty.buffer.ByteBufInputStream;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.server.management.PlayerChunkMap;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLEventChannel;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientCustomPacketEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ServerCustomPacketEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.internal.FMLProxyPacket;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import stevesvehicles.common.core.Constants;
import stevesvehicles.common.network.packets.PacketBuoy;
import stevesvehicles.common.network.packets.PacketUpgrades;
import stevesvehicles.common.network.packets.PacketGuiData;
import stevesvehicles.common.network.packets.PacketVehicle;
import stevesvehicles.api.network.DataReader;
import stevesvehicles.api.network.packets.IPacketClient;
import stevesvehicles.api.network.packets.IPacketProvider;
import stevesvehicles.api.network.packets.IPacketServer;
import stevesvehicles.common.vehicles.VehicleBase;
import stevesvehicles.common.vehicles.entitys.IVehicleEntity;

public class PacketHandler {
	private final static FMLEventChannel channel = NetworkRegistry.INSTANCE.newEventDrivenChannel(Constants.MOD_ID);
	private final static List<IPacketProvider> PROVIDERS = new LinkedList<>();
	private static int ids;

	public PacketHandler() {
		channel.register(this);
		// Client Packets
		registerClientPacket(new PacketUpgrades());
		registerClientPacket(new PacketBuoy());
		registerClientPacket(new PacketGuiData());
		registerClientPacket(new PacketVehicle());
		// Server Packets
		registerServerPacket(new PacketBuoy());
		registerServerPacket(new PacketVehicle());
	}

	public void registerProvider(IPacketProvider provider) {
		PROVIDERS.add(ids++, provider);
		provider.setPacketID(ids);
	}

	public static VehicleBase getVehicle(int id, World world) {
		Entity entity = world.getEntityByID(id);
		if (entity instanceof IVehicleEntity) {
			return ((IVehicleEntity) entity).getVehicle();
		}
		return null;
	}

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
			// TODO: create Log
			// Log.err("Failed to read packet.", e);
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
			// TODO: create Log
			// Log.err("Failed to read packet.", e);
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
						// TODO: create Log
						// Log.err("Network Error", e);
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
						// TODO: create Log
						// Log.err("Network Error", e);
					}
				}
			});
		}
	}
}
