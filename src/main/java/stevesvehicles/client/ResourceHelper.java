package stevesvehicles.client;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import stevesvehicles.common.core.Constants;

public class ResourceHelper {
	public static ResourceLocation getResource(String path) {
		return new ResourceLocation(Constants.MOD_ID, "textures" + path);
	}

	public static ResourceLocation getResourceFromPath(String path) {
		return new ResourceLocation("textures" + path);
	}

	public static void bindResource(ResourceLocation resource) {
		Minecraft.getMinecraft().getTextureManager().bindTexture(resource);
	}
}
