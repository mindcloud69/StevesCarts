package stevesvehicles.api.network;

import java.io.IOException;

import net.minecraft.entity.player.EntityPlayer;

public interface IStreamable {

	void writeData(DataWriter data) throws IOException;

	void readData(DataReader data, EntityPlayer player) throws IOException;
}
