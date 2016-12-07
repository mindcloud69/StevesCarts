package stevesvehicles.api.network;

import java.io.IOException;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.internal.FMLProxyPacket;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import stevesvehicles.api.modules.Module;
import stevesvehicles.api.network.packets.IClientPacket;
import stevesvehicles.api.network.packets.IServerPacket;
public interface IPacketHandler {
	void sendToNetwork(IClientPacket packet, BlockPos pos, WorldServer world);

	@SideOnly(Side.CLIENT)
	void sendToServer(IServerPacket packet);

	void sendToPlayer(IClientPacket packet, EntityPlayer entityplayer);

	void sendPacket(FMLProxyPacket packet, EntityPlayerMP player);

	DataWriter createWriter(Module module, boolean hasInterfaceOpen);

	void sendPacketVehicle(Module module, boolean hasInterfaceOpen) throws IOException;

	void sendPacketVehicle(Module module, DataWriter dataWriter) throws IOException;

	void sendPacketVehicle(Module module, boolean hasInterfaceOpen, EntityPlayer entityplayer) throws IOException;

	void sendPacketVehicle(Module module, DataWriter dataWriter, EntityPlayer entityplayer) throws IOException;
}
