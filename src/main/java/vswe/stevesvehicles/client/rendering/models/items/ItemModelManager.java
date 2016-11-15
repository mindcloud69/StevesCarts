package vswe.stevesvehicles.client.rendering.models.items;

import java.util.ArrayList;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;

/**
 * Create a new instance of this in your mod
 */
public class ItemModelManager {
	public static ArrayList<TexturedItem> items = new ArrayList<>();
	static ModelGenerator modelGenerator;

	/**
	 * Call this in pre-init, doesn't matter if you call it on the server side
	 */
	public static void load() {
		if (FMLCommonHandler.instance().getSide() == Side.CLIENT) {
			modelGenerator = new ModelGenerator();
			MinecraftForge.EVENT_BUS.register(modelGenerator);
		}
	}

	/**
	 * Use this to register an object to be rendered
	 *
	 * @param object the object to load
	 */
	public static void registerItem(TexturedItem object) {
		if (!items.contains(object)) {
			items.add(object);
		}
	}

}
