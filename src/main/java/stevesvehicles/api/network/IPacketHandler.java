package stevesvehicles.api.network;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.internal.FMLProxyPacket;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import stevesvehicles.api.network.packets.IPacketClient;
import stevesvehicles.api.network.packets.IPacketServer;

public interface IPacketHandler {
	void sendToNetwork(IPacketClient packet, BlockPos pos, WorldServer world);

	@SideOnly(Side.CLIENT)
	void sendToServer(IPacketServer packet);

	void sendToPlayer(IPacketClient packet, EntityPlayer entityplayer);

	void sendPacket(FMLProxyPacket packet, EntityPlayerMP player);
}
