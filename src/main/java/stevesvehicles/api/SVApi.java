package stevesvehicles.api;

import net.minecraft.item.ItemStack;
import stevesvehicles.api.modules.container.IModuleContainer;
import stevesvehicles.api.network.IPacketHandler;

public class SVApi {

	public static IPacketHandler packetHandler;

	public static IModuleContainer getMatchingContainer(ItemStack stack){
		return null;
	}
}
