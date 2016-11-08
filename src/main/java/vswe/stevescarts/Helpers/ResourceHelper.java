package vswe.stevescarts.Helpers;

import java.util.HashMap;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ResourceHelper {
	private static HashMap<String, ResourceLocation> resources;
	private static HashMap<String, ResourceLocation> pathResources;

	public static ResourceLocation getResource(final String path) {
		return new ResourceLocation("stevescarts", "textures" + path);
	}

	public static ResourceLocation getResourceFromPath(final String path) {
		return new ResourceLocation("textures" + path);
	}

	public static void bindResource(final ResourceLocation resource) {
		if (resource != null) {
			Minecraft.getMinecraft().getTextureManager().bindTexture(resource);
		}
	}

	public static void bindResource(final String path) {
		if (ResourceHelper.resources.containsKey(path)) {
			bindResource(ResourceHelper.resources.get(path));
		} else {
			final ResourceLocation resource = getResource(path);
			ResourceHelper.resources.put(path, resource);
			bindResource(resource);
		}
	}

	public static void bindResourcePath(final String path) {
		if (ResourceHelper.pathResources.containsKey(path)) {
			bindResource(ResourceHelper.pathResources.get(path));
		} else {
			final ResourceLocation resource = getResourceFromPath(path);
			ResourceHelper.pathResources.put(path, resource);
			bindResource(resource);
		}
	}

	static {
		ResourceHelper.resources = new HashMap<String, ResourceLocation>();
		ResourceHelper.pathResources = new HashMap<String, ResourceLocation>();
	}
}
