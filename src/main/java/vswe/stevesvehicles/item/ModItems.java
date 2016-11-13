package vswe.stevesvehicles.item;

import static vswe.stevesvehicles.item.ComponentTypes.ADVANCED_PCB;
import static vswe.stevesvehicles.item.ComponentTypes.ADVANCED_SOLAR_PANEL;
import static vswe.stevesvehicles.item.ComponentTypes.BASKET;
import static vswe.stevesvehicles.item.ComponentTypes.BLADE_ARM;
import static vswe.stevesvehicles.item.ComponentTypes.BLANK_UPGRADE;
import static vswe.stevesvehicles.item.ComponentTypes.BLUE_PIGMENT;
import static vswe.stevesvehicles.item.ComponentTypes.BURNING_EASTER_EGG;
import static vswe.stevesvehicles.item.ComponentTypes.CHEST_LOCK;
import static vswe.stevesvehicles.item.ComponentTypes.CHOCOLATE_EASTER_EGG;
import static vswe.stevesvehicles.item.ComponentTypes.CLEANING_CORE;
import static vswe.stevesvehicles.item.ComponentTypes.CLEANING_FAN;
import static vswe.stevesvehicles.item.ComponentTypes.CLEANING_TUBE;
import static vswe.stevesvehicles.item.ComponentTypes.DYNAMITE;
import static vswe.stevesvehicles.item.ComponentTypes.EMPTY_DISK;
import static vswe.stevesvehicles.item.ComponentTypes.ENHANCED_GALGADORIAN_METAL;
import static vswe.stevesvehicles.item.ComponentTypes.ENTITY_ANALYZER;
import static vswe.stevesvehicles.item.ComponentTypes.ENTITY_SCANNER;
import static vswe.stevesvehicles.item.ComponentTypes.EXPLOSIVE_EASTER_EGG;
import static vswe.stevesvehicles.item.ComponentTypes.EYE_OF_GALGADOR;
import static vswe.stevesvehicles.item.ComponentTypes.FUSE;
import static vswe.stevesvehicles.item.ComponentTypes.GALGADORIAN_METAL;
import static vswe.stevesvehicles.item.ComponentTypes.GALGADORIAN_SAW_BLADE;
import static vswe.stevesvehicles.item.ComponentTypes.GALGADORIAN_WHEELS;
import static vswe.stevesvehicles.item.ComponentTypes.GLASS_O_MAGIC;
import static vswe.stevesvehicles.item.ComponentTypes.GLISTERING_EASTER_EGG;
import static vswe.stevesvehicles.item.ComponentTypes.GRAPHICAL_INTERFACE;
import static vswe.stevesvehicles.item.ComponentTypes.GREEN_PIGMENT;
import static vswe.stevesvehicles.item.ComponentTypes.HARDENED_MESH;
import static vswe.stevesvehicles.item.ComponentTypes.HARDENED_SAW_BLADE;
import static vswe.stevesvehicles.item.ComponentTypes.IRON_BLADE;
import static vswe.stevesvehicles.item.ComponentTypes.IRON_WHEELS;
import static vswe.stevesvehicles.item.ComponentTypes.LARGE_LUMP_OF_GALGADOR;
import static vswe.stevesvehicles.item.ComponentTypes.LIQUID_CLEANING_CORE;
import static vswe.stevesvehicles.item.ComponentTypes.LIQUID_CLEANING_TUBE;
import static vswe.stevesvehicles.item.ComponentTypes.LUMP_OF_GALGADOR;
import static vswe.stevesvehicles.item.ComponentTypes.PIPE;
import static vswe.stevesvehicles.item.ComponentTypes.RAW_HANDLE;
import static vswe.stevesvehicles.item.ComponentTypes.RAW_HARDENER;
import static vswe.stevesvehicles.item.ComponentTypes.RED_GIFT_RIBBON;
import static vswe.stevesvehicles.item.ComponentTypes.RED_PIGMENT;
import static vswe.stevesvehicles.item.ComponentTypes.REFINED_HANDLE;
import static vswe.stevesvehicles.item.ComponentTypes.REFINED_HARDENER;
import static vswe.stevesvehicles.item.ComponentTypes.REINFORCED_METAL;
import static vswe.stevesvehicles.item.ComponentTypes.REINFORCED_WHEELS;
import static vswe.stevesvehicles.item.ComponentTypes.SAW_BLADE;
import static vswe.stevesvehicles.item.ComponentTypes.SHOOTING_STATION;
import static vswe.stevesvehicles.item.ComponentTypes.SIMPLE_PCB;
import static vswe.stevesvehicles.item.ComponentTypes.SOCK;
import static vswe.stevesvehicles.item.ComponentTypes.SOLAR_PANEL;
import static vswe.stevesvehicles.item.ComponentTypes.SPEED_HANDLE;
import static vswe.stevesvehicles.item.ComponentTypes.STABILIZED_METAL;
import static vswe.stevesvehicles.item.ComponentTypes.TANK_VALVE;
import static vswe.stevesvehicles.item.ComponentTypes.TRI_TORCH;
import static vswe.stevesvehicles.item.ComponentTypes.WARM_HAT;
import static vswe.stevesvehicles.item.ComponentTypes.WHEEL;
import static vswe.stevesvehicles.item.ComponentTypes.WOODEN_WHEELS;
import static vswe.stevesvehicles.item.ComponentTypes.WOOD_CUTTING_CORE;
import static vswe.stevesvehicles.item.ComponentTypes.YELLOW_GIFT_RIBBON;

import java.util.HashMap;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.registry.GameRegistry;
import vswe.stevesvehicles.StevesVehicles;
import vswe.stevesvehicles.block.ModBlocks;
import vswe.stevesvehicles.module.data.ModuleData;
import vswe.stevesvehicles.module.data.registry.ModuleRegistry;
import vswe.stevesvehicles.tileentity.detector.DetectorType;

public final class ModItems {
	public static ItemBuoy buoys;
	public static ItemVehicles vehicles;
	public static ItemCartComponent component;
	public static ItemVehicleModule modules;
	public static ItemUpgrade upgrades;
	public static ItemBlockStorage storage;
	public static ItemBlockDetector detectors;
	private static final String VEHICLE_NAME = "ModularVehicle";
	private static final String COMPONENTS_NAME = "ModuleComponents";
	private static final String MODULES_NAME = "CartModule";
	private static final String BUOY_NAME = "Buoy";
	private static HashMap<String, Boolean> validModules = new HashMap<>();

	public static void preBlockInit(Configuration config) {
		(vehicles = new ItemVehicles()).setUnlocalizedName(StevesVehicles.localStart + VEHICLE_NAME);
		component = new ItemCartComponent();
		modules = new ItemVehicleModule();
		buoys = new ItemBuoy();
		GameRegistry.registerItem(vehicles, VEHICLE_NAME);
		GameRegistry.registerItem(component, COMPONENTS_NAME);
		GameRegistry.registerItem(modules, MODULES_NAME);
		GameRegistry.registerItem(buoys, BUOY_NAME);
		for (ModuleData module : ModuleRegistry.getAllModules()) {
			if (!module.getIsLocked()) {
				validModules.put(module.getFullRawUnlocalizedName(), config.get("EnabledModules", module.getName().replace(" ", "").replace(":", "_"), module.getEnabledByDefault()).getBoolean(true));
			}
		}
		for (int i = 0; i < ItemCartComponent.size(); i++) {
			ItemStack subComponent = new ItemStack(component, 1, i);
			// GameRegistry.registerCustomItemStack(subComponent.getUnlocalizedName(),
			// subComponent);
		}
	}

	public static void postBlockInit(Configuration config) {
		detectors = (ItemBlockDetector) new ItemStack(ModBlocks.DETECTOR_UNIT.getBlock()).getItem();
		upgrades = (ItemUpgrade) new ItemStack(ModBlocks.UPGRADE.getBlock()).getItem();
		storage = (ItemBlockStorage) new ItemStack(ModBlocks.STORAGE.getBlock()).getItem();
		for (int i = 0; i < ItemBlockStorage.blocks.length; i++) {
			ItemStack storage = new ItemStack(ModItems.storage, 1, i);
			// GameRegistry.registerCustomItemStack(storage.getUnlocalizedName(),
			// storage);
		}
		for (DetectorType type : DetectorType.VALUES) {
			ItemStack stack = new ItemStack(detectors, 1, type.getMeta());
			// GameRegistry.registerCustomItemStack(stack.getUnlocalizedName(),
			// stack);
		}
	}

	private static final String PLANK = "plankWood";
	private static final String WOOD = "logWood";
	private static final String RED = "dyeRed";
	private static final String GREEN = "dyeGreen";
	private static final String BLUE = "dyeBlue";
	private static final String ORANGE = "dyeOrange";
	private static final String YELLOW = "dyeYellow";
	private static final String SAPLING = "treeSapling";

	public static void addRecipes() {
		for (ModuleData module : ModuleRegistry.getAllModules()) {
			if (!module.getIsLocked() && validModules.get(module.getFullRawUnlocalizedName())) {
				module.loadRecipes();
			}
		}
		WOODEN_WHEELS.addShapedRecipe(null, Items.STICK, null, Items.STICK, PLANK, Items.STICK, null, Items.STICK, null);
		IRON_WHEELS.addShapedRecipe(null, Items.STICK, null, Items.STICK, Items.IRON_INGOT, Items.STICK, null, Items.STICK, null);
		RED_PIGMENT.addShapedRecipe(null, Items.GLOWSTONE_DUST, null, RED, RED, RED, null, Items.GLOWSTONE_DUST, null);
		GREEN_PIGMENT.addShapedRecipe(null, Items.GLOWSTONE_DUST, null, GREEN, GREEN, GREEN, null, Items.GLOWSTONE_DUST, null);
		BLUE_PIGMENT.addShapedRecipe(null, Items.GLOWSTONE_DUST, null, BLUE, BLUE, BLUE, null, Items.GLOWSTONE_DUST, null);
		GLASS_O_MAGIC.addShapedRecipe(Blocks.GLASS_PANE, Items.FERMENTED_SPIDER_EYE, Blocks.GLASS_PANE, Blocks.GLASS_PANE, Items.REDSTONE, Blocks.GLASS_PANE, Blocks.GLASS_PANE, Blocks.GLASS_PANE, Blocks.GLASS_PANE);
		FUSE.addShapedRecipeWithSizeAndCount(1, 3, 12, Items.STRING, Items.STRING, Items.STRING);
		DYNAMITE.addShapedRecipeWithSize(1, 3, FUSE, Items.GUNPOWDER, Items.GUNPOWDER);
		SIMPLE_PCB.addShapedRecipe(Items.IRON_INGOT, Items.REDSTONE, Items.IRON_INGOT, Items.REDSTONE, Items.GOLD_INGOT, Items.REDSTONE, Items.IRON_INGOT, Items.REDSTONE, Items.IRON_INGOT);
		SIMPLE_PCB.addShapedRecipe(Items.REDSTONE, Items.IRON_INGOT, Items.REDSTONE, Items.IRON_INGOT, Items.GOLD_INGOT, Items.IRON_INGOT, Items.REDSTONE, Items.IRON_INGOT, Items.REDSTONE);
		GRAPHICAL_INTERFACE.addShapedRecipe(Items.GOLD_INGOT, Items.DIAMOND, Items.GOLD_INGOT, Blocks.GLASS_PANE, SIMPLE_PCB, Blocks.GLASS_PANE, Items.REDSTONE, Blocks.GLASS_PANE, Items.REDSTONE);
		RAW_HANDLE.addShapedRecipe(null, null, Items.IRON_INGOT, null, Items.IRON_INGOT, null, Items.IRON_INGOT, null, null);
		FurnaceRecipes.instance().addSmeltingRecipe(RAW_HANDLE.getItemStack(), REFINED_HANDLE.getItemStack(), 0F);
		SPEED_HANDLE.addShapedRecipe(null, null, BLUE, Items.GOLD_INGOT, REFINED_HANDLE, null, Items.REDSTONE, Items.GOLD_INGOT, null);
		WHEEL.addShapedRecipe(Items.IRON_INGOT, Items.STICK, Items.IRON_INGOT, Items.STICK, Items.IRON_INGOT, Items.STICK, null, Items.STICK, null);
		SAW_BLADE.addShapedRecipeWithSize(3, 1, Items.IRON_INGOT, Items.IRON_INGOT, Items.DIAMOND);
		ADVANCED_PCB.addShapedRecipe(Items.REDSTONE, Items.IRON_INGOT, Items.REDSTONE, SIMPLE_PCB, Items.IRON_INGOT, SIMPLE_PCB, Items.REDSTONE, Items.IRON_INGOT, Items.REDSTONE);
		WOOD_CUTTING_CORE.addShapedRecipe(SAPLING, SAPLING, SAPLING, SAPLING, ADVANCED_PCB, SAPLING, SAPLING, SAPLING, SAPLING);
		RAW_HARDENER.addShapelessRecipeWithCount(4, Items.DIAMOND, Blocks.OBSIDIAN, Blocks.OBSIDIAN, Blocks.OBSIDIAN, Blocks.OBSIDIAN);
		FurnaceRecipes.instance().addSmeltingRecipe(RAW_HARDENER.getItemStack(), REFINED_HARDENER.getItemStack(), 0F);
		HARDENED_MESH.addShapedRecipe(Blocks.IRON_BARS, REFINED_HARDENER, Blocks.IRON_BARS, REFINED_HARDENER, Blocks.IRON_BARS, REFINED_HARDENER, Blocks.IRON_BARS, REFINED_HARDENER, Blocks.IRON_BARS);
		STABILIZED_METAL.addShapedRecipeWithSizeAndCount(3, 3, 5, Items.IRON_INGOT, HARDENED_MESH, Items.IRON_INGOT, Items.IRON_INGOT, Items.IRON_INGOT, Items.IRON_INGOT, REFINED_HARDENER, REFINED_HARDENER, REFINED_HARDENER);
		FurnaceRecipes.instance().addSmeltingRecipe(STABILIZED_METAL.getItemStack(), REINFORCED_METAL.getItemStack(), 0F);
		REINFORCED_WHEELS.addShapedRecipe(null, Items.IRON_INGOT, null, Items.IRON_INGOT, REINFORCED_METAL, Items.IRON_INGOT, null, Items.IRON_INGOT, null);
		PIPE.addShapedRecipeWithSize(3, 2, Blocks.STONE, Blocks.STONE, Blocks.STONE, Items.IRON_INGOT, null, null);
		SHOOTING_STATION.addShapedRecipe(Items.REDSTONE, null, Items.REDSTONE, Items.REDSTONE, Items.GOLD_INGOT, Items.REDSTONE, Blocks.DISPENSER, SIMPLE_PCB, Blocks.DISPENSER);
		ENTITY_SCANNER.addShapedRecipe(Items.GOLD_INGOT, SIMPLE_PCB, Items.GOLD_INGOT, Items.REDSTONE, ADVANCED_PCB, Items.REDSTONE, Items.REDSTONE, null, Items.REDSTONE);
		ENTITY_ANALYZER.addShapedRecipe(Items.IRON_INGOT, Items.REDSTONE, Items.IRON_INGOT, Items.IRON_INGOT, SIMPLE_PCB, Items.IRON_INGOT, Items.IRON_INGOT, Items.IRON_INGOT, Items.IRON_INGOT);
		EMPTY_DISK.addShapedRecipeWithSize(1, 2, Items.REDSTONE, SIMPLE_PCB);
		TRI_TORCH.addShapedRecipeWithSize(3, 1, Blocks.TORCH, Blocks.TORCH, Blocks.TORCH);
		CHEST_LOCK.addShapedRecipeWithSizeAndCount(1, 2, 8, Items.IRON_INGOT, Blocks.STONE);
		CHEST_LOCK.addShapedRecipeWithSizeAndCount(1, 2, 8, Blocks.STONE, Items.IRON_INGOT);
		CLEANING_FAN.addShapedRecipe(Blocks.IRON_BARS, Items.REDSTONE, Blocks.IRON_BARS, Items.REDSTONE, null, Items.REDSTONE, Blocks.IRON_BARS, Items.REDSTONE, Blocks.IRON_BARS);
		CLEANING_CORE.addShapedRecipe(CLEANING_FAN, Items.IRON_INGOT, CLEANING_FAN, CLEANING_TUBE, CLEANING_TUBE, CLEANING_TUBE, Items.IRON_INGOT, CLEANING_TUBE, Items.IRON_INGOT);
		CLEANING_TUBE.addShapedRecipeWithSizeAndCount(3, 3, 2, ORANGE, Items.IRON_INGOT, ORANGE, ORANGE, Items.IRON_INGOT, ORANGE, ORANGE, Items.IRON_INGOT, ORANGE);
		SOLAR_PANEL.addShapelessRecipe(Items.GLOWSTONE_DUST, Items.GLOWSTONE_DUST, Items.REDSTONE, Items.IRON_INGOT);
		EYE_OF_GALGADOR.addShapedRecipe(Items.MAGMA_CREAM, Items.FERMENTED_SPIDER_EYE, Items.MAGMA_CREAM, Items.GHAST_TEAR, Items.ENDER_EYE, Items.GHAST_TEAR, Items.MAGMA_CREAM, Items.FERMENTED_SPIDER_EYE, Items.MAGMA_CREAM);
		LUMP_OF_GALGADOR.addShapedRecipeWithSizeAndCount(3, 3, 2, Items.GLOWSTONE_DUST, Blocks.DIAMOND_BLOCK, Items.GLOWSTONE_DUST, EYE_OF_GALGADOR, Items.GLOWSTONE_DUST, EYE_OF_GALGADOR, STABILIZED_METAL, EYE_OF_GALGADOR, STABILIZED_METAL);
		FurnaceRecipes.instance().addSmeltingRecipe(LUMP_OF_GALGADOR.getItemStack(), GALGADORIAN_METAL.getItemStack(), 0F);
		LARGE_LUMP_OF_GALGADOR.addShapedRecipe(LUMP_OF_GALGADOR, LUMP_OF_GALGADOR, LUMP_OF_GALGADOR, LUMP_OF_GALGADOR, LUMP_OF_GALGADOR, LUMP_OF_GALGADOR, LUMP_OF_GALGADOR, LUMP_OF_GALGADOR, LUMP_OF_GALGADOR);
		FurnaceRecipes.instance().addSmeltingRecipe(LARGE_LUMP_OF_GALGADOR.getItemStack(), ENHANCED_GALGADORIAN_METAL.getItemStack(), 0F);
		RED_GIFT_RIBBON.addShapedRecipe(Items.STRING, Items.STRING, Items.STRING, Items.STRING, RED, Items.STRING, Items.STRING, Items.STRING, Items.STRING);
		YELLOW_GIFT_RIBBON.addShapedRecipe(Items.STRING, Items.STRING, Items.STRING, Items.STRING, YELLOW, Items.STRING, Items.STRING, Items.STRING, Items.STRING);
		ItemStack redWool = new ItemStack(Blocks.WOOL, 1, 14);
		ItemStack whiteWool = new ItemStack(Blocks.WOOL, 1, 0);
		WARM_HAT.addShapedRecipe(null, redWool, whiteWool, redWool, Items.EMERALD, redWool, redWool, redWool, redWool);
		SOCK.addShapedRecipe(redWool, redWool, Items.COOKIE, redWool, redWool, Items.MILK_BUCKET, redWool, redWool, redWool);
		ADVANCED_SOLAR_PANEL.addShapedRecipe(SOLAR_PANEL, null, SOLAR_PANEL, Items.IRON_INGOT, SIMPLE_PCB, Items.IRON_INGOT, SOLAR_PANEL, null, SOLAR_PANEL);
		ADVANCED_SOLAR_PANEL.addShapedRecipe(SOLAR_PANEL, Items.IRON_INGOT, SOLAR_PANEL, null, SIMPLE_PCB, null, SOLAR_PANEL, Items.IRON_INGOT, SOLAR_PANEL);
		BLANK_UPGRADE.addShapedRecipeWithSizeAndCount(3, 3, 2, Items.IRON_INGOT, Items.IRON_INGOT, Items.IRON_INGOT, Items.REDSTONE, Blocks.OBSIDIAN, Items.REDSTONE, Items.IRON_INGOT, SIMPLE_PCB, Items.IRON_INGOT);
		TANK_VALVE.addShapedRecipeWithSizeAndCount(3, 3, 8, null, Items.IRON_INGOT, null, Items.IRON_INGOT, Blocks.IRON_BARS, Items.IRON_INGOT, null, Items.IRON_INGOT, null);
		LIQUID_CLEANING_CORE.addShapedRecipe(CLEANING_FAN, Items.IRON_INGOT, CLEANING_FAN, LIQUID_CLEANING_TUBE, LIQUID_CLEANING_TUBE, LIQUID_CLEANING_TUBE, Items.IRON_INGOT, LIQUID_CLEANING_TUBE, Items.IRON_INGOT);
		LIQUID_CLEANING_TUBE.addShapedRecipeWithSizeAndCount(3, 3, 2, GREEN, Items.IRON_INGOT, GREEN, GREEN, Items.IRON_INGOT, GREEN, GREEN, Items.IRON_INGOT, GREEN);
		EXPLOSIVE_EASTER_EGG.addShapedRecipeWithSizeAndCount(3, 3, 16, Items.GUNPOWDER, Items.GUNPOWDER, Items.GUNPOWDER, Items.GUNPOWDER, Items.EGG, Items.GUNPOWDER, Items.GUNPOWDER, GREEN, Items.GUNPOWDER);
		BURNING_EASTER_EGG.addShapedRecipeWithSizeAndCount(3, 3, 16, Items.BLAZE_POWDER, Items.BLAZE_ROD, Items.BLAZE_POWDER, Items.BLAZE_POWDER, Items.EGG, Items.BLAZE_POWDER, RED, Items.MAGMA_CREAM, YELLOW);
		GLISTERING_EASTER_EGG.addShapedRecipeWithSizeAndCount(3, 3, 16, Items.GOLD_NUGGET, Items.GOLD_NUGGET, Items.GOLD_NUGGET, Items.GOLD_NUGGET, Items.EGG, Items.GOLD_NUGGET, Items.GOLD_NUGGET, BLUE, Items.GOLD_NUGGET);
		ItemStack chocolate = new ItemStack(Items.DYE, 1, 3);
		CHOCOLATE_EASTER_EGG.addShapedRecipeWithSizeAndCount(3, 3, 16, chocolate, Items.SUGAR, chocolate, chocolate, Items.EGG, chocolate, chocolate, Items.SUGAR, chocolate);
		BASKET.addShapedRecipe(Items.STICK, Items.STICK, Items.STICK, Items.STICK, null, Items.STICK, PLANK, PLANK, PLANK);
		HARDENED_SAW_BLADE.addShapedRecipeWithSize(3, 1, Items.IRON_INGOT, Items.IRON_INGOT, REINFORCED_METAL);
		GALGADORIAN_SAW_BLADE.addShapedRecipeWithSize(3, 1, Items.IRON_INGOT, Items.IRON_INGOT, GALGADORIAN_METAL);
		GALGADORIAN_WHEELS.addShapedRecipe(null, REINFORCED_METAL, null, REINFORCED_METAL, GALGADORIAN_METAL, REINFORCED_METAL, null, REINFORCED_METAL, null);
		IRON_BLADE.addShapedRecipeWithSizeAndCount(3, 3, 4, null, Items.SHEARS, null, Items.IRON_INGOT, Items.IRON_INGOT, Items.IRON_INGOT, null, Items.IRON_INGOT, null);
		BLADE_ARM.addShapedRecipe(IRON_BLADE, null, BLADE_ARM, null, Items.IRON_INGOT, null, IRON_BLADE, null, IRON_BLADE);
		ItemBlockStorage.loadRecipes();
	}

	private ModItems() {
	}
}
