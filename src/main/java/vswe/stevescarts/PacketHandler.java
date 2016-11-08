package vswe.stevescarts;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerPlayer;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.internal.FMLProxyPacket;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vswe.stevescarts.blocks.BlockCartAssembler;
import vswe.stevescarts.blocks.ModBlocks;
import vswe.stevescarts.blocks.tileentities.TileEntityBase;
import vswe.stevescarts.containers.ContainerBase;
import vswe.stevescarts.containers.ContainerMinecart;
import vswe.stevescarts.entitys.MinecartModular;
import vswe.stevescarts.modules.ModuleBase;

public class PacketHandler {
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onClientPacket(final FMLNetworkEvent.ClientCustomPacketEvent event) {
		final EntityPlayer player = FMLClientHandler.instance().getClient().thePlayer;
		int idForCrash = -1;
		try {
			final byte[] bytes = event.getPacket().payload().array();
			final ByteArrayDataInput reader = ByteStreams.newDataInput(bytes);
			final int id = idForCrash = reader.readByte();
			if (id == -1) {
				final int x = reader.readInt();
				final int y = reader.readInt();
				final int z = reader.readInt();
				final int len = bytes.length - 13;
				final byte[] data = new byte[len];
				for (int i = 0; i < len; ++i) {
					data[i] = reader.readByte();
				}
				final World world = player.worldObj;
				((BlockCartAssembler) ModBlocks.CART_ASSEMBLER.getBlock()).updateMultiBlock(world, new BlockPos(x, y, z));
			} else {
				final int entityid = reader.readInt();
				final int len2 = bytes.length - 5;
				final byte[] data2 = new byte[len2];
				for (int j = 0; j < len2; ++j) {
					data2[j] = reader.readByte();
				}
				final World world2 = player.worldObj;
				final MinecartModular cart = this.getCart(entityid, world2);
				if (cart != null) {
					this.receivePacketAtCart(cart, id, data2, player);
				}
			}
		} catch (Exception ex) {
			System.out.println("The client failed to process a packet with " + ((idForCrash == -1) ? "unknown id" : ("id " + idForCrash)));
		}
	}

	@SubscribeEvent
	public void onServerPacket(final FMLNetworkEvent.ServerCustomPacketEvent event) {
		final EntityPlayer player = ((NetHandlerPlayServer) event.getHandler()).playerEntity;
		int idForCrash = -1;
		try {
			final byte[] bytes = event.getPacket().payload().array();
			final ByteArrayDataInput reader = ByteStreams.newDataInput(bytes);
			final int id = idForCrash = reader.readByte();
			final World world = player.worldObj;
			if (player.openContainer instanceof ContainerPlayer) {
				final int entityid = reader.readInt();
				final int len = bytes.length - 5;
				final byte[] data = new byte[len];
				for (int i = 0; i < len; ++i) {
					data[i] = reader.readByte();
				}
				final MinecartModular cart = this.getCart(entityid, world);
				if (cart != null) {
					this.receivePacketAtCart(cart, id, data, player);
				}
			} else {
				final int len2 = bytes.length - 1;
				final byte[] data2 = new byte[len2];
				for (int j = 0; j < len2; ++j) {
					data2[j] = reader.readByte();
				}
				final Container con = player.openContainer;
				if (con instanceof ContainerMinecart) {
					final ContainerMinecart conMC = (ContainerMinecart) con;
					final MinecartModular cart2 = conMC.cart;
					this.receivePacketAtCart(cart2, id, data2, player);
				} else if (con instanceof ContainerBase) {
					final ContainerBase conBase = (ContainerBase) con;
					final TileEntityBase base = conBase.getTileEntity();
					if (base != null) {
						base.receivePacket(id, data2, player);
					}
				}
			}
		} catch (Exception ex) {
			System.out.println("The server failed to process a packet with " + ((idForCrash == -1) ? "unknown id" : ("id " + idForCrash)));
		}
	}

	private void receivePacketAtCart(final MinecartModular cart, final int id, final byte[] data, final EntityPlayer player) {
		for (final ModuleBase module : cart.getModules()) {
			if (id >= module.getPacketStart() && id < module.getPacketStart() + module.totalNumberOfPackets()) {
				module.delegateReceivedPacket(id - module.getPacketStart(), data, player);
				break;
			}
		}
	}

	private MinecartModular getCart(final int ID, final World world) {
		for (final Object e : world.loadedEntityList) {
			if (e instanceof Entity && ((Entity) e).getEntityId() == ID && e instanceof MinecartModular) {
				return (MinecartModular) e;
			}
		}
		return null;
	}

	public static void sendPacket(final int id, final byte[] extraData) {
		final ByteArrayOutputStream bs = new ByteArrayOutputStream();
		final DataOutputStream ds = new DataOutputStream(bs);
		try {
			ds.writeByte((byte) id);
			for (final byte b : extraData) {
				ds.writeByte(b);
			}
		} catch (IOException ex) {}
		StevesCarts.packetHandler.sendToServer(createPacket(bs.toByteArray()));
	}

	private static FMLProxyPacket createPacket(final byte[] bytes) {
		final ByteBuf buf = Unpooled.copiedBuffer(bytes);
		return new FMLProxyPacket(new PacketBuffer(buf), "SC2");
	}

	public static void sendPacket(final MinecartModular cart, final int id, final byte[] extraData) {
		final ByteArrayOutputStream bs = new ByteArrayOutputStream();
		final DataOutputStream ds = new DataOutputStream(bs);
		try {
			ds.writeByte((byte) id);
			ds.writeInt(cart.getEntityId());
			for (final byte b : extraData) {
				ds.writeByte(b);
			}
		} catch (IOException ex) {}
		StevesCarts.packetHandler.sendToServer(createPacket(bs.toByteArray()));
	}

	public static void sendPacketToPlayer(final int id, final byte[] data, final EntityPlayer player, final MinecartModular cart) {
		final ByteArrayOutputStream bs = new ByteArrayOutputStream();
		final DataOutputStream ds = new DataOutputStream(bs);
		try {
			ds.writeByte((byte) id);
			ds.writeInt(cart.getEntityId());
			for (final byte b : data) {
				ds.writeByte(b);
			}
		} catch (IOException ex) {}
		StevesCarts.packetHandler.sendTo(createPacket(bs.toByteArray()), (EntityPlayerMP) player);
	}

	public static void sendBlockInfoToClients(final World world, final byte[] data, final BlockPos pos) {
		final ByteArrayOutputStream bs = new ByteArrayOutputStream();
		final DataOutputStream ds = new DataOutputStream(bs);
		try {
			ds.writeByte(-1);
			ds.writeInt(pos.getX());
			ds.writeInt(pos.getY());
			ds.writeInt(pos.getZ());
			for (final byte b : data) {
				ds.writeByte(b);
			}
		} catch (IOException ex) {}
		StevesCarts.packetHandler.sendToAllAround(createPacket(bs.toByteArray()), new NetworkRegistry.TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 64.0));
	}
}
