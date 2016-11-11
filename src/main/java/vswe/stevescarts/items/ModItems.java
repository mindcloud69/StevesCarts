package vswe.stevescarts.items;

import java.util.HashMap;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.registry.GameRegistry;
import vswe.stevescarts.blocks.ModBlocks;
import vswe.stevescarts.helpers.ComponentTypes;
import vswe.stevescarts.helpers.DetectorType;
import vswe.stevescarts.helpers.RecipeHelper;
import vswe.stevescarts.modules.data.ModuleData;
import vswe.stevescarts.upgrades.AssemblerUpgrade;

public final class ModItems {
	public static ItemCarts carts;
	public static ItemCartComponent component;
	public static ItemCartModule modules;
	public static ItemUpgrade upgrades;
	public static ItemBlockStorage storages;
	public static ItemBlockDetector detectors;
	private static final String CART_NAME = "ModularCart";
	private static final String COMPONENTS_NAME = "ModuleComponents";
	private static final String MODULES_NAME = "CartModule";
	private static HashMap<Byte, Boolean> validModules;

	public static void preBlockInit(final Configuration config) {
		(ModItems.carts = new ItemCarts()).setUnlocalizedName("SC2:ModularCart");
		ModItems.component = new ItemCartComponent();
		ModItems.modules = new ItemCartModule();
		GameRegistry.registerItem(ModItems.carts, "ModularCart");
		GameRegistry.registerItem(ModItems.component, "ModuleComponents");
		GameRegistry.registerItem(ModItems.modules, "CartModule");
		ModuleData.init();
		for (final ModuleData module : ModuleData.getList().values()) {
			if (!module.getIsLocked()) {
				ModItems.validModules.put(module.getID(), config.get("EnabledModules", module.getName().replace(" ", "").replace(":", "_"), module.getEnabledByDefault()).getBoolean(true));
			}
		}
		for (int i = 0; i < ItemCartComponent.size(); ++i) {
			final ItemStack subcomponent = new ItemStack(ModItems.component, 1, i);
			//GameRegistry.registerCustomItemStack(subcomponent.getUnlocalizedName(), subcomponent);
		}
		for (final ModuleData module : ModuleData.getList().values()) {
			final ItemStack submodule = new ItemStack(ModItems.modules, 1, module.getID());
			//GameRegistry.registerCustomItemStack(submodule.getUnlocalizedName(), submodule);
		}
	}

	public static void postBlockInit(final Configuration config) {
		ModItems.detectors = (ItemBlockDetector) new ItemStack(ModBlocks.DETECTOR_UNIT.getBlock()).getItem();
		ModItems.upgrades = (ItemUpgrade) new ItemStack(ModBlocks.UPGRADE.getBlock()).getItem();
		ModItems.storages = (ItemBlockStorage) new ItemStack(ModBlocks.STORAGE.getBlock()).getItem();
		for (int i = 0; i < ItemBlockStorage.blocks.length; ++i) {
			final ItemStack storage = new ItemStack(ModItems.storages, 1, i);
			//	GameRegistry.registerCustomItemStack(storage.getUnlocalizedName(), storage);
		}
		for (final AssemblerUpgrade upgrade : AssemblerUpgrade.getUpgradesList()) {
			final ItemStack upgradeStack = new ItemStack(ModItems.upgrades, 1, upgrade.getId());
			//GameRegistry.registerCustomItemStack(upgradeStack.getUnlocalizedName(), upgradeStack);
		}
		for (final DetectorType type : DetectorType.values()) {
			final ItemStack stack = new ItemStack(ModItems.detectors, 1, type.getMeta());
			//GameRegistry.registerCustomItemStack(stack.getUnlocalizedName(), stack);
		}
	}

	public static void addRecipes() {
		for (final ModuleData module : ModuleData.getList().values()) {
			final ItemStack submodule = new ItemStack(ModItems.modules, 1, module.getID());
			if (!module.getIsLocked() && ModItems.validModules.get(module.getID())) {
				module.loadRecipe();
			}
		}
		final String planks = "plankWood";
		final String wood = "logWood";
		final String red = "dyeRed";
		final String green = "dyeGreen";
		final String blue = "dyeBlue";
		final String orange = "dyeOrange";
		final String yellow = "dyeYellow";
		RecipeHelper.addRecipe(ComponentTypes.WOODEN_WHEELS.getItemStack(), new Object[][] { { null, Items.STICK, null }, { Items.STICK, planks, Items.STICK }, { null, Items.STICK, null } });
		RecipeHelper.addRecipe(ComponentTypes.IRON_WHEELS.getItemStack(), new Object[][] { { null, Items.STICK, null }, { Items.STICK, Items.IRON_INGOT, Items.STICK }, { null, Items.STICK, null } });
		RecipeHelper.addRecipe(ComponentTypes.RED_PIGMENT.getItemStack(), new Object[][] { { null, Items.GLOWSTONE_DUST, null }, { red, red, red }, { null, Items.GLOWSTONE_DUST, null } });
		RecipeHelper.addRecipe(ComponentTypes.GREEN_PIGMENT.getItemStack(), new Object[][] { { null, Items.GLOWSTONE_DUST, null }, { green, green, green }, { null, Items.GLOWSTONE_DUST, null } });
		RecipeHelper.addRecipe(ComponentTypes.BLUE_PIGMENT.getItemStack(), new Object[][] { { null, Items.GLOWSTONE_DUST, null }, { blue, blue, blue }, { null, Items.GLOWSTONE_DUST, null } });
		RecipeHelper.addRecipe(ComponentTypes.GLASS_O_MAGIC.getItemStack(), new Object[][] { { Blocks.GLASS_PANE, Items.FERMENTED_SPIDER_EYE, Blocks.GLASS_PANE },
			{ Blocks.GLASS_PANE, Items.REDSTONE, Blocks.GLASS_PANE }, { Blocks.GLASS_PANE, Blocks.GLASS_PANE, Blocks.GLASS_PANE } });
		RecipeHelper.addRecipe(ComponentTypes.FUSE.getItemStack(12), new Object[][] { { Items.STRING }, { Items.STRING }, { Items.STRING } });
		RecipeHelper.addRecipe(ComponentTypes.DYNAMITE.getItemStack(), new Object[][] { { ComponentTypes.FUSE.getItemStack() }, { Items.GUNPOWDER }, { Items.GUNPOWDER } });
		RecipeHelper.addRecipe(ComponentTypes.SIMPLE_PCB.getItemStack(), new Object[][] { { Items.IRON_INGOT, Items.REDSTONE, Items.IRON_INGOT }, { Items.REDSTONE, Items.GOLD_INGOT, Items.REDSTONE },
			{ Items.IRON_INGOT, Items.REDSTONE, Items.IRON_INGOT } });
		RecipeHelper.addRecipe(ComponentTypes.SIMPLE_PCB.getItemStack(), new Object[][] { { Items.REDSTONE, Items.IRON_INGOT, Items.REDSTONE },
			{ Items.IRON_INGOT, Items.GOLD_INGOT, Items.IRON_INGOT }, { Items.REDSTONE, Items.IRON_INGOT, Items.REDSTONE } });
		RecipeHelper.addRecipe(ComponentTypes.GRAPHICAL_INTERFACE.getItemStack(), new Object[][] { { Items.GOLD_INGOT, Items.DIAMOND, Items.GOLD_INGOT },
			{ Blocks.GLASS_PANE, ComponentTypes.SIMPLE_PCB.getItemStack(), Blocks.GLASS_PANE }, { Items.REDSTONE, Blocks.GLASS_PANE, Items.REDSTONE } });
		RecipeHelper.addRecipe(ComponentTypes.RAW_HANDLE.getItemStack(), new Object[][] { { null, null, Items.IRON_INGOT }, { null, Items.IRON_INGOT, null }, { Items.IRON_INGOT, null, null } });
		FurnaceRecipes.instance().addSmeltingRecipe(ComponentTypes.RAW_HANDLE.getItemStack(), ComponentTypes.REFINED_HANDLE.getItemStack(), 0.0f);
		RecipeHelper.addRecipe(ComponentTypes.SPEED_HANDLE.getItemStack(), new Object[][] { { null, null, blue }, { Items.GOLD_INGOT, ComponentTypes.REFINED_HANDLE.getItemStack(), null },
			{ Items.REDSTONE, Items.GOLD_INGOT, null } });
		RecipeHelper.addRecipe(ComponentTypes.WHEEL.getItemStack(), new Object[][] { { Items.IRON_INGOT, Items.STICK, Items.IRON_INGOT }, { Items.STICK, Items.IRON_INGOT, Items.STICK },
			{ null, Items.STICK, null } });
		RecipeHelper.addRecipe(ComponentTypes.SAW_BLADE.getItemStack(), new Object[][] { { Items.IRON_INGOT, Items.IRON_INGOT, Items.DIAMOND } });
		RecipeHelper.addRecipe(ComponentTypes.ADVANCED_PCB.getItemStack(), new Object[][] { { Items.REDSTONE, Items.IRON_INGOT, Items.REDSTONE },
			{ ComponentTypes.SIMPLE_PCB.getItemStack(), Items.IRON_INGOT, ComponentTypes.SIMPLE_PCB.getItemStack() }, { Items.REDSTONE, Items.IRON_INGOT, Items.REDSTONE } });
		RecipeHelper.addRecipe(ComponentTypes.WOOD_CUTTING_CORE.getItemStack(), new Object[][] { { "treeSapling", "treeSapling", "treeSapling" },
			{ "treeSapling", ComponentTypes.ADVANCED_PCB.getItemStack(), "treeSapling" }, { "treeSapling", "treeSapling", "treeSapling" } });
		RecipeHelper.addRecipe(ComponentTypes.RAW_HARDENER.getItemStack(2), new Object[][] { { Blocks.OBSIDIAN, null, Blocks.OBSIDIAN }, { null, Items.DIAMOND, null },
			{ Blocks.OBSIDIAN, null, Blocks.OBSIDIAN } });
		FurnaceRecipes.instance().addSmeltingRecipe(ComponentTypes.RAW_HARDENER.getItemStack(), ComponentTypes.REFINED_HARDENER.getItemStack(), 0.0f);
		RecipeHelper.addRecipe(ComponentTypes.HARDENED_MESH.getItemStack(), new Object[][] { { Blocks.IRON_BARS, ComponentTypes.REFINED_HARDENER.getItemStack(), Blocks.IRON_BARS },
			{ ComponentTypes.REFINED_HARDENER.getItemStack(), Blocks.IRON_BARS, ComponentTypes.REFINED_HARDENER.getItemStack() },
			{ Blocks.IRON_BARS, ComponentTypes.REFINED_HARDENER.getItemStack(), Blocks.IRON_BARS } });
		RecipeHelper.addRecipe(ComponentTypes.STABILIZED_METAL.getItemStack(5), new Object[][] { { Items.IRON_INGOT, ComponentTypes.HARDENED_MESH.getItemStack(), Items.IRON_INGOT },
			{ Items.IRON_INGOT, Items.IRON_INGOT, Items.IRON_INGOT },
			{ ComponentTypes.REFINED_HARDENER.getItemStack(), ComponentTypes.REFINED_HARDENER.getItemStack(), ComponentTypes.REFINED_HARDENER.getItemStack() } });
		FurnaceRecipes.instance().addSmeltingRecipe(ComponentTypes.STABILIZED_METAL.getItemStack(), ComponentTypes.REINFORCED_METAL.getItemStack(), 0.0f);
		RecipeHelper.addRecipe(ComponentTypes.REINFORCED_WHEELS.getItemStack(), new Object[][] { { null, Items.IRON_INGOT, null },
			{ Items.IRON_INGOT, ComponentTypes.REINFORCED_METAL.getItemStack(), Items.IRON_INGOT }, { null, Items.IRON_INGOT, null } });
		RecipeHelper.addRecipe(ComponentTypes.PIPE.getItemStack(), new Object[][] { { Blocks.STONE, Blocks.STONE, Blocks.STONE }, { Items.IRON_INGOT, null, null } });
		RecipeHelper.addRecipe(ComponentTypes.SHOOTING_STATION.getItemStack(), new Object[][] { { Items.REDSTONE, null, Items.REDSTONE }, { Items.REDSTONE, Items.GOLD_INGOT, Items.REDSTONE },
			{ Blocks.DISPENSER, ComponentTypes.SIMPLE_PCB.getItemStack(), Blocks.DISPENSER } });
		RecipeHelper.addRecipe(ComponentTypes.ENTITY_SCANNER.getItemStack(), new Object[][] { { Items.GOLD_INGOT, ComponentTypes.SIMPLE_PCB.getItemStack(), Items.GOLD_INGOT },
			{ Items.REDSTONE, ComponentTypes.ADVANCED_PCB.getItemStack(), Items.REDSTONE }, { Items.REDSTONE, null, Items.REDSTONE } });
		RecipeHelper.addRecipe(ComponentTypes.ENTITY_ANALYZER.getItemStack(), new Object[][] { { Items.IRON_INGOT, Items.REDSTONE, Items.IRON_INGOT },
			{ Items.IRON_INGOT, ComponentTypes.SIMPLE_PCB.getItemStack(), Items.IRON_INGOT }, { Items.IRON_INGOT, Items.IRON_INGOT, Items.IRON_INGOT } });
		RecipeHelper.addRecipe(ComponentTypes.EMPTY_DISK.getItemStack(), new Object[][] { { Items.REDSTONE }, { ComponentTypes.SIMPLE_PCB.getItemStack() } });
		RecipeHelper.addRecipe(ComponentTypes.TRI_TORCH.getItemStack(), new Object[][] { { Blocks.TORCH, Blocks.TORCH, Blocks.TORCH } });
		RecipeHelper.addRecipe(ComponentTypes.CHEST_PANE.getItemStack(32), new Object[][] { { planks, planks, planks }, { wood, planks, wood }, { planks, planks, planks } });
		RecipeHelper.addRecipe(ComponentTypes.LARGE_CHEST_PANE.getItemStack(), new Object[][] { { ComponentTypes.CHEST_PANE.getItemStack(), ComponentTypes.CHEST_PANE.getItemStack() },
			{ ComponentTypes.CHEST_PANE.getItemStack(), ComponentTypes.CHEST_PANE.getItemStack() } });
		RecipeHelper.addRecipe(ComponentTypes.HUGE_CHEST_PANE.getItemStack(), new Object[][] {
			{ ComponentTypes.CHEST_PANE.getItemStack(), ComponentTypes.CHEST_PANE.getItemStack(), ComponentTypes.CHEST_PANE.getItemStack() },
			{ ComponentTypes.CHEST_PANE.getItemStack(), ComponentTypes.CHEST_PANE.getItemStack(), ComponentTypes.CHEST_PANE.getItemStack() },
			{ ComponentTypes.CHEST_PANE.getItemStack(), ComponentTypes.CHEST_PANE.getItemStack(), ComponentTypes.CHEST_PANE.getItemStack() } });
		RecipeHelper.addRecipe(ComponentTypes.CHEST_LOCK.getItemStack(8), new Object[][] { { Items.IRON_INGOT }, { Blocks.STONE } });
		RecipeHelper.addRecipe(ComponentTypes.CHEST_LOCK.getItemStack(8), new Object[][] { { Blocks.STONE }, { Items.IRON_INGOT } });
		RecipeHelper.addRecipe(ComponentTypes.IRON_PANE.getItemStack(8), new Object[][] {
			{ ComponentTypes.CHEST_PANE.getItemStack(), ComponentTypes.CHEST_PANE.getItemStack(), ComponentTypes.CHEST_PANE.getItemStack() },
			{ ComponentTypes.CHEST_PANE.getItemStack(), Items.IRON_INGOT, ComponentTypes.CHEST_PANE.getItemStack() },
			{ ComponentTypes.CHEST_PANE.getItemStack(), ComponentTypes.CHEST_PANE.getItemStack(), ComponentTypes.CHEST_PANE.getItemStack() } });
		RecipeHelper.addRecipe(ComponentTypes.LARGE_IRON_PANE.getItemStack(), new Object[][] { { ComponentTypes.IRON_PANE.getItemStack(), ComponentTypes.IRON_PANE.getItemStack() },
			{ ComponentTypes.IRON_PANE.getItemStack(), ComponentTypes.IRON_PANE.getItemStack() } });
		RecipeHelper.addRecipe(ComponentTypes.HUGE_IRON_PANE.getItemStack(), new Object[][] {
			{ ComponentTypes.IRON_PANE.getItemStack(), ComponentTypes.IRON_PANE.getItemStack(), ComponentTypes.IRON_PANE.getItemStack() },
			{ ComponentTypes.IRON_PANE.getItemStack(), ComponentTypes.IRON_PANE.getItemStack(), ComponentTypes.IRON_PANE.getItemStack() },
			{ ComponentTypes.IRON_PANE.getItemStack(), ComponentTypes.IRON_PANE.getItemStack(), ComponentTypes.IRON_PANE.getItemStack() } });
		RecipeHelper.addRecipe(ComponentTypes.DYNAMIC_PANE.getItemStack(), new Object[][] { { ComponentTypes.IRON_PANE.getItemStack() }, { Items.REDSTONE } });
		RecipeHelper.addRecipe(ComponentTypes.DYNAMIC_PANE.getItemStack(), new Object[][] { { Items.REDSTONE }, { ComponentTypes.IRON_PANE.getItemStack() } });
		RecipeHelper.addRecipe(ComponentTypes.LARGE_DYNAMIC_PANE.getItemStack(), new Object[][] { { null, ComponentTypes.DYNAMIC_PANE.getItemStack(), null },
			{ ComponentTypes.DYNAMIC_PANE.getItemStack(), Items.REDSTONE, ComponentTypes.DYNAMIC_PANE.getItemStack() }, { null, ComponentTypes.DYNAMIC_PANE.getItemStack(), null } });
		RecipeHelper.addRecipe(ComponentTypes.HUGE_DYNAMIC_PANE.getItemStack(), new Object[][] {
			{ ComponentTypes.DYNAMIC_PANE.getItemStack(), ComponentTypes.DYNAMIC_PANE.getItemStack(), ComponentTypes.DYNAMIC_PANE.getItemStack() },
			{ ComponentTypes.DYNAMIC_PANE.getItemStack(), ComponentTypes.SIMPLE_PCB.getItemStack(), ComponentTypes.DYNAMIC_PANE.getItemStack() },
			{ ComponentTypes.DYNAMIC_PANE.getItemStack(), ComponentTypes.DYNAMIC_PANE.getItemStack(), ComponentTypes.DYNAMIC_PANE.getItemStack() } });
		RecipeHelper.addRecipe(ComponentTypes.CLEANING_FAN.getItemStack(), new Object[][] { { Blocks.IRON_BARS, Items.REDSTONE, Blocks.IRON_BARS }, { Items.REDSTONE, null, Items.REDSTONE },
			{ Blocks.IRON_BARS, Items.REDSTONE, Blocks.IRON_BARS } });
		RecipeHelper.addRecipe(ComponentTypes.CLEANING_CORE.getItemStack(), new Object[][] {
			{ ComponentTypes.CLEANING_FAN.getItemStack(), Items.IRON_INGOT, ComponentTypes.CLEANING_FAN.getItemStack() },
			{ ComponentTypes.CLEANING_TUBE.getItemStack(), ComponentTypes.CLEANING_TUBE.getItemStack(), ComponentTypes.CLEANING_TUBE.getItemStack() },
			{ Items.IRON_INGOT, ComponentTypes.CLEANING_TUBE.getItemStack(), Items.IRON_INGOT } });
		RecipeHelper.addRecipe(ComponentTypes.CLEANING_TUBE.getItemStack(2), new Object[][] { { orange, Items.IRON_INGOT, orange }, { orange, Items.IRON_INGOT, orange },
			{ orange, Items.IRON_INGOT, orange } });
		RecipeHelper.addRecipe(ComponentTypes.SOLAR_PANEL.getItemStack(), new Object[][] { { Items.GLOWSTONE_DUST, Items.REDSTONE }, { Items.IRON_INGOT, Items.GLOWSTONE_DUST } });
		RecipeHelper.addRecipe(ComponentTypes.EYE_OF_GALGADOR.getItemStack(), new Object[][] { { Items.MAGMA_CREAM, Items.FERMENTED_SPIDER_EYE, Items.MAGMA_CREAM },
			{ Items.GHAST_TEAR, Items.ENDER_EYE, Items.GHAST_TEAR }, { Items.MAGMA_CREAM, Items.FERMENTED_SPIDER_EYE, Items.MAGMA_CREAM } });
		RecipeHelper.addRecipe(ComponentTypes.LUMP_OF_GALGADOR.getItemStack(2), new Object[][] { { Items.GLOWSTONE_DUST, Blocks.DIAMOND_BLOCK, Items.GLOWSTONE_DUST },
			{ ComponentTypes.EYE_OF_GALGADOR.getItemStack(), Items.GLOWSTONE_DUST, ComponentTypes.EYE_OF_GALGADOR.getItemStack() },
			{ ComponentTypes.STABILIZED_METAL.getItemStack(), ComponentTypes.EYE_OF_GALGADOR.getItemStack(), ComponentTypes.STABILIZED_METAL.getItemStack() } });
		FurnaceRecipes.instance().addSmeltingRecipe(ComponentTypes.LUMP_OF_GALGADOR.getItemStack(), ComponentTypes.GALGADORIAN_METAL.getItemStack(), 0.0f);
		RecipeHelper.addRecipe(ComponentTypes.LARGE_LUMP_OF_GALGADOR.getItemStack(), new Object[][] {
			{ ComponentTypes.LUMP_OF_GALGADOR.getItemStack(), ComponentTypes.LUMP_OF_GALGADOR.getItemStack(), ComponentTypes.LUMP_OF_GALGADOR.getItemStack() },
			{ ComponentTypes.LUMP_OF_GALGADOR.getItemStack(), ComponentTypes.LUMP_OF_GALGADOR.getItemStack(), ComponentTypes.LUMP_OF_GALGADOR.getItemStack() },
			{ ComponentTypes.LUMP_OF_GALGADOR.getItemStack(), ComponentTypes.LUMP_OF_GALGADOR.getItemStack(), ComponentTypes.LUMP_OF_GALGADOR.getItemStack() } });
		FurnaceRecipes.instance().addSmeltingRecipe(ComponentTypes.LARGE_LUMP_OF_GALGADOR.getItemStack(), ComponentTypes.ENHANCED_GALGADORIAN_METAL.getItemStack(), 0.0f);
		RecipeHelper.addRecipe(ComponentTypes.RED_GIFT_RIBBON.getItemStack(), new Object[][] { { Items.STRING, Items.STRING, Items.STRING }, { Items.STRING, red, Items.STRING },
			{ Items.STRING, Items.STRING, Items.STRING } });
		RecipeHelper.addRecipe(ComponentTypes.YELLOW_GIFT_RIBBON.getItemStack(), new Object[][] { { Items.STRING, Items.STRING, Items.STRING }, { Items.STRING, yellow, Items.STRING },
			{ Items.STRING, Items.STRING, Items.STRING } });
		RecipeHelper.addRecipe(ComponentTypes.WARM_HAT.getItemStack(), new Object[][] { { null, new ItemStack(Blocks.WOOL, 1, 14), new ItemStack(Blocks.WOOL, 1, 0) },
			{ new ItemStack(Blocks.WOOL, 1, 14), Items.DIAMOND, new ItemStack(Blocks.WOOL, 1, 14) },
			{ new ItemStack(Blocks.WOOL, 1, 14), new ItemStack(Blocks.WOOL, 1, 14), new ItemStack(Blocks.WOOL, 1, 14) } });
		RecipeHelper.addRecipe(ComponentTypes.SOCK.getItemStack(), new Object[][] { { new ItemStack(Blocks.WOOL, 1, 14), new ItemStack(Blocks.WOOL, 1, 14), Items.COOKIE },
			{ new ItemStack(Blocks.WOOL, 1, 14), new ItemStack(Blocks.WOOL, 1, 14), Items.MILK_BUCKET },
			{ new ItemStack(Blocks.WOOL, 1, 14), new ItemStack(Blocks.WOOL, 1, 14), new ItemStack(Blocks.WOOL, 1, 14) } });
		RecipeHelper.addRecipe(ComponentTypes.ADVANCED_SOLAR_PANEL.getItemStack(), new Object[][] { { ComponentTypes.SOLAR_PANEL.getItemStack(), null, ComponentTypes.SOLAR_PANEL.getItemStack() },
			{ Items.IRON_INGOT, ComponentTypes.SIMPLE_PCB.getItemStack(), Items.IRON_INGOT }, { ComponentTypes.SOLAR_PANEL.getItemStack(), null, ComponentTypes.SOLAR_PANEL.getItemStack() } });
		RecipeHelper.addRecipe(ComponentTypes.ADVANCED_SOLAR_PANEL.getItemStack(), new Object[][] {
			{ ComponentTypes.SOLAR_PANEL.getItemStack(), Items.IRON_INGOT, ComponentTypes.SOLAR_PANEL.getItemStack() }, { null, ComponentTypes.SIMPLE_PCB.getItemStack(), null },
			{ ComponentTypes.SOLAR_PANEL.getItemStack(), Items.IRON_INGOT, ComponentTypes.SOLAR_PANEL.getItemStack() } });
		RecipeHelper.addRecipe(ComponentTypes.BLANK_UPGRADE.getItemStack(2), new Object[][] { { Items.IRON_INGOT, Items.IRON_INGOT, Items.IRON_INGOT },
			{ ComponentTypes.REINFORCED_METAL.getItemStack(), Items.REDSTONE, ComponentTypes.REINFORCED_METAL.getItemStack() },
			{ Items.IRON_INGOT, ComponentTypes.ADVANCED_PCB.getItemStack(), Items.IRON_INGOT } });
		RecipeHelper.addRecipe(ComponentTypes.TANK_VALVE.getItemStack(8), new Object[][] { { null, Items.IRON_INGOT, null }, { Items.IRON_INGOT, Blocks.IRON_BARS, Items.IRON_INGOT },
			{ null, Items.IRON_INGOT, null } });
		RecipeHelper.addRecipe(ComponentTypes.TANK_PANE.getItemStack(32), new Object[][] { { Blocks.GLASS_PANE, Blocks.GLASS_PANE, Blocks.GLASS_PANE },
			{ Blocks.GLASS, Blocks.GLASS_PANE, Blocks.GLASS }, { Blocks.GLASS_PANE, Blocks.GLASS_PANE, Blocks.GLASS_PANE } });
		RecipeHelper.addRecipe(ComponentTypes.LARGE_TANK_PANE.getItemStack(), new Object[][] { { ComponentTypes.TANK_PANE.getItemStack(), ComponentTypes.TANK_PANE.getItemStack() },
			{ ComponentTypes.TANK_PANE.getItemStack(), ComponentTypes.TANK_PANE.getItemStack() } });
		RecipeHelper.addRecipe(ComponentTypes.HUGE_TANK_PANE.getItemStack(), new Object[][] {
			{ ComponentTypes.TANK_PANE.getItemStack(), ComponentTypes.TANK_PANE.getItemStack(), ComponentTypes.TANK_PANE.getItemStack() },
			{ ComponentTypes.TANK_PANE.getItemStack(), ComponentTypes.TANK_PANE.getItemStack(), ComponentTypes.TANK_PANE.getItemStack() },
			{ ComponentTypes.TANK_PANE.getItemStack(), ComponentTypes.TANK_PANE.getItemStack(), ComponentTypes.TANK_PANE.getItemStack() } });
		RecipeHelper.addRecipe(ComponentTypes.LIQUID_CLEANING_CORE.getItemStack(), new Object[][] {
			{ ComponentTypes.CLEANING_FAN.getItemStack(), Items.IRON_INGOT, ComponentTypes.CLEANING_FAN.getItemStack() },
			{ ComponentTypes.LIQUID_CLEANING_TUBE.getItemStack(), ComponentTypes.LIQUID_CLEANING_TUBE.getItemStack(), ComponentTypes.LIQUID_CLEANING_TUBE.getItemStack() },
			{ Items.IRON_INGOT, ComponentTypes.LIQUID_CLEANING_TUBE.getItemStack(), Items.IRON_INGOT } });
		RecipeHelper.addRecipe(ComponentTypes.LIQUID_CLEANING_TUBE.getItemStack(2), new Object[][] { { green, Items.IRON_INGOT, green }, { green, Items.IRON_INGOT, green },
			{ green, Items.IRON_INGOT, green } });
		RecipeHelper.addRecipe(ComponentTypes.EXPLOSIVE_EASTER_EGG.getItemStack(), new Object[][] { { Items.GUNPOWDER, Items.GUNPOWDER, Items.GUNPOWDER },
			{ Items.GUNPOWDER, Items.EGG, Items.GUNPOWDER }, { Items.GUNPOWDER, green, Items.GUNPOWDER } });
		RecipeHelper.addRecipe(ComponentTypes.BURNING_EASTER_EGG.getItemStack(), new Object[][] { { Items.BLAZE_POWDER, Items.BLAZE_ROD, Items.BLAZE_POWDER },
			{ Items.BLAZE_POWDER, Items.EGG, Items.BLAZE_POWDER }, { red, Items.MAGMA_CREAM, yellow } });
		RecipeHelper.addRecipe(ComponentTypes.GLISTERING_EASTER_EGG.getItemStack(), new Object[][] { { Items.GOLD_NUGGET, Items.GOLD_NUGGET, Items.GOLD_NUGGET },
			{ Items.GOLD_NUGGET, Items.EGG, Items.GOLD_NUGGET }, { Items.GOLD_NUGGET, blue, Items.GOLD_NUGGET } });
		final ItemStack chocolate = new ItemStack(Items.DYE, 1, 3);
		RecipeHelper.addRecipe(ComponentTypes.CHOCOLATE_EASTER_EGG.getItemStack(), new Object[][] { { chocolate, Items.SUGAR, chocolate }, { chocolate, Items.EGG, chocolate },
			{ chocolate, Items.SUGAR, chocolate } });
		RecipeHelper.addRecipe(ComponentTypes.BASKET.getItemStack(), new Object[][] { { Items.STICK, Items.STICK, Items.STICK }, { Items.STICK, null, Items.STICK }, { planks, planks, planks } });
		for (int i = 0; i < 4; ++i) {
			RecipeHelper.addRecipe(new ItemStack(Blocks.PLANKS, 2, i), new Object[][] { { ItemCartComponent.getWood(i, true) } });
			RecipeHelper.addRecipe(new ItemStack(Items.STICK, 2), new Object[][] { { ItemCartComponent.getWood(i, false) } });
		}
		RecipeHelper.addRecipe(ComponentTypes.HARDENED_SAW_BLADE.getItemStack(), new Object[][] { { Items.IRON_INGOT, Items.IRON_INGOT, ComponentTypes.REINFORCED_METAL.getItemStack() } });
		RecipeHelper.addRecipe(ComponentTypes.GALGADORIAN_SAW_BLADE.getItemStack(), new Object[][] { { Items.IRON_INGOT, Items.IRON_INGOT, ComponentTypes.GALGADORIAN_METAL.getItemStack() } });
		RecipeHelper.addRecipe(ComponentTypes.GALGADORIAN_WHEELS.getItemStack(), new Object[][] { { null, ComponentTypes.REINFORCED_METAL.getItemStack(), null },
			{ ComponentTypes.REINFORCED_METAL.getItemStack(), ComponentTypes.GALGADORIAN_METAL.getItemStack(), ComponentTypes.REINFORCED_METAL.getItemStack() },
			{ null, ComponentTypes.REINFORCED_METAL.getItemStack(), null } });
		RecipeHelper.addRecipe(ComponentTypes.IRON_BLADE.getItemStack(4), new Object[][] { { null, Items.SHEARS, null }, { Items.IRON_INGOT, Items.IRON_INGOT, Items.IRON_INGOT },
			{ null, Items.IRON_INGOT, null } });
		RecipeHelper.addRecipe(ComponentTypes.BLADE_ARM.getItemStack(), new Object[][] { { ComponentTypes.IRON_BLADE.getItemStack(), null, ComponentTypes.IRON_BLADE.getItemStack() },
			{ null, Items.IRON_INGOT, null }, { ComponentTypes.IRON_BLADE.getItemStack(), null, ComponentTypes.IRON_BLADE.getItemStack() } });
		ItemBlockStorage.loadRecipes();
	}

	static {
		ModItems.validModules = new HashMap<>();
	}
}
