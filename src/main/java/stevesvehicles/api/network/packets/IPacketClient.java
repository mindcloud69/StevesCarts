package stevesvehicles.api.network.packets;

import java.io.IOException;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import stevesvehicles.api.network.DataReader;

public interface IPacketClient extends IPacket {
	@SideOnly(Side.CLIENT)
	void onPacketData(DataReader data, EntityPlayer player) throws IOException;
}
