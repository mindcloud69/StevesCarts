package vswe.stevesvehicles.client.rendering.models.items;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vswe.stevesvehicles.Constants;
import vswe.stevesvehicles.block.ModBlocks;
import vswe.stevesvehicles.buoy.BuoyType;
import vswe.stevesvehicles.item.ModItems;
import vswe.stevesvehicles.tileentity.detector.DetectorType;

/**
 * Create a new instance of this in your mod
 */
public class ItemModelManager {
	public static ArrayList<TexturedItem> items = new ArrayList<>();
	public static ArrayList<ModeledObject> objects = new ArrayList<>();
	static ModelGenerator modelGenerator;

	/**
	 * Call this in pre-init, doesn't matter if you call it on the server side
	 */
	public static void load() {
		if (FMLCommonHandler.instance().getSide() == Side.CLIENT) {
			modelGenerator = new ModelGenerator();
			ModelLoader.setCustomMeshDefinition(ModItems.modules, new TexturedItemMeshDefinition());
			ModelLoader.setCustomMeshDefinition(Item.getItemFromBlock(ModBlocks.UPGRADE.getBlock()), new TexturedItemMeshDefinition());
			ModelLoader.setCustomMeshDefinition(ModItems.vehicles, new VehicleMeshDefinition());
			for (BuoyType type : BuoyType.values()) {
				registerItemModel(ModItems.buoys, type.ordinal());
			}
			registerItemModel(ModBlocks.CART_ASSEMBLER.getBlock(), 0);
			registerItemModel(ModBlocks.CARGO_MANAGER.getBlock(), 0);
			registerItemModel(ModBlocks.LIQUID_MANAGER.getBlock(), 0);
			for (int i = 0; i < 3; ++i) {
				ModelResourceLocation location = new ModelResourceLocation(Constants.MOD_ID + ":metal_storage", "type=" + i);
				ModelLoader.setCustomModelResourceLocation(ModItems.storage, i, location);
			}
			registerItemModel(ModBlocks.JUNCTION.getBlock(), 0);
			registerItemModel(ModBlocks.ADVANCED_DETECTOR.getBlock(), 0);
			registerItemModel(ModBlocks.MODULE_TOGGLER.getBlock(), 0);
			registerItemModel(ModBlocks.EXTERNAL_DISTRIBUTOR.getBlock(), 0);
			registerItemModel(ModBlocks.DETECTOR_UNIT.getBlock(), 0);
			for (int i = 0; i < 5; ++i) {
				ModelResourceLocation location = new ModelResourceLocation(Constants.MOD_ID + ":detector_unit", "active=false,detectortype=" + DetectorType.getTypeFromInt(i).getName());
				ModelLoader.setCustomModelResourceLocation(ModItems.detectors, i, location);
			}
			MinecraftForge.EVENT_BUS.register(modelGenerator);
		}
	}

	@SideOnly(Side.CLIENT)
	public static void registerItemModel(Item i, int meta) {
		ResourceLocation loc = i.getRegistryName();
		ModelLoader.setCustomModelResourceLocation(i, meta, new ModelResourceLocation(loc, "inventory"));
	}

	@SideOnly(Side.CLIENT)
	public static void registerItemModel(Block b, int meta) {
		registerItemModel(Item.getItemFromBlock(b), meta);
	}

	/**
	 * Use this to register an object to be rendered
	 *
	 * @param object
	 *            the object to load
	 */
	public static void registerItem(TexturedItem object) {
		if (!items.contains(object)) {
			items.add(object);
		}
	}
	
	/**
	 * Use this to register an object to be rendered
	 *
	 * @param object
	 *            the object to load
	 */
	public static void register(ModeledObject object) {
		if (!objects.contains(object)) {
			objects.add(object);
		}
	}
}
