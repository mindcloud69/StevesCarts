package stevesvehicles.common.network.packets;

import java.io.IOException;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import stevesvehicles.common.network.DataReader;

public interface IClientPacket extends IPacket {
	@SideOnly(Side.CLIENT)
	void onPacketData(DataReader data, EntityPlayer player) throws IOException;
}
