package vswe.stevescarts.modules.data;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vswe.stevescarts.Constants;
import vswe.stevescarts.StevesCarts;
import vswe.stevescarts.entitys.EntityMinecartModular;
import vswe.stevescarts.helpers.*;
import vswe.stevescarts.items.ModItems;
import vswe.stevescarts.models.*;
import vswe.stevescarts.models.engines.ModelEngineFrame;
import vswe.stevescarts.models.engines.ModelEngineInside;
import vswe.stevescarts.models.engines.ModelSolarPanelBase;
import vswe.stevescarts.models.engines.ModelSolarPanelHeads;
import vswe.stevescarts.models.pig.ModelPigHead;
import vswe.stevescarts.models.pig.ModelPigHelmet;
import vswe.stevescarts.models.pig.ModelPigTail;
import vswe.stevescarts.models.realtimers.ModelGun;
import vswe.stevescarts.models.storages.chests.*;
import vswe.stevescarts.models.storages.tanks.ModelAdvancedTank;
import vswe.stevescarts.models.storages.tanks.ModelFrontTank;
import vswe.stevescarts.models.storages.tanks.ModelSideTanks;
import vswe.stevescarts.models.storages.tanks.ModelTopTank;
import vswe.stevescarts.models.workers.ModelLiquidDrainer;
import vswe.stevescarts.models.workers.ModelRailer;
import vswe.stevescarts.models.workers.ModelTorchplacer;
import vswe.stevescarts.models.workers.ModelTrackRemover;
import vswe.stevescarts.models.workers.tools.ModelDrill;
import vswe.stevescarts.models.workers.tools.ModelFarmer;
import vswe.stevescarts.models.workers.tools.ModelWoodCutter;
import vswe.stevescarts.modules.ModuleBase;
import vswe.stevescarts.modules.addons.*;
import vswe.stevescarts.modules.addons.mobdetectors.*;
import vswe.stevescarts.modules.addons.plants.ModuleNetherwart;
import vswe.stevescarts.modules.addons.plants.ModulePlantSize;
import vswe.stevescarts.modules.addons.projectiles.*;
import vswe.stevescarts.modules.engines.*;
import vswe.stevescarts.modules.hull.*;
import vswe.stevescarts.modules.realtimers.*;
import vswe.stevescarts.modules.storages.ModuleStorage;
import vswe.stevescarts.modules.storages.chests.*;
import vswe.stevescarts.modules.storages.tanks.*;
import vswe.stevescarts.modules.workers.*;
import vswe.stevescarts.modules.workers.tools.*;

import javax.annotation.Nonnull;
import java.util.*;

public class ModuleData {
	private static HashMap<Byte, ModuleData> moduleList;
	private static Class[] moduleGroups;
	private static Localization.MODULE_INFO[] moduleGroupNames;
	private byte id;
	private Class<? extends ModuleBase> moduleClass;
	private String name;
	private int modularCost;
	private int groupID;
	private ArrayList<SIDE> renderingSides;
	private boolean allowDuplicate;
	private ArrayList<ModuleData> nemesis;
	private ArrayList<ModuleDataGroup> requirement;
	private ModuleData parent;
	private boolean isValid;
	private boolean isLocked;
	private boolean defaultLock;
	private boolean hasRecipe;
	private ArrayList<Localization.MODULE_INFO> message;
	private HashMap<String, ModelCartbase> models;
	private HashMap<String, ModelCartbase> modelsPlaceholder;
	private ArrayList<String> removedModels;
	private float modelMult;
	private boolean useExtraData;
	private byte extraDataDefaultValue;
	private ArrayList<Object[][]> recipes;
	private static final int MAX_MESSAGE_ROW_LENGTH = 30;
	private String icon;

	public static HashMap<Byte, ModuleData> getList() {
		return ModuleData.moduleList;
	}

	public static Collection<ModuleData> getModules() {
		return getList().values();
	}

	public static void init() {
		final String planks = "plankWood";
		final String wood = "logWood";
		@Nonnull
		ItemStack woodSingleSlab = new ItemStack(Blocks.WOODEN_SLAB, 1, -1);
		@Nonnull
		ItemStack bonemeal = new ItemStack(Items.DYE, 1, 15);
		ModuleData.moduleGroups = new Class[] { ModuleHull.class, ModuleEngine.class, ModuleTool.class, ModuleStorage.class, ModuleAddon.class };
		ModuleData.moduleGroupNames = new Localization.MODULE_INFO[] { Localization.MODULE_INFO.HULL_CATEGORY, Localization.MODULE_INFO.ENGINE_CATEGORY, Localization.MODULE_INFO.TOOL_CATEGORY,
			Localization.MODULE_INFO.STORAGE_CATEGORY, Localization.MODULE_INFO.ADDON_CATEGORY, Localization.MODULE_INFO.ATTACHMENT_CATEGORY };
		ModuleData.moduleList = new HashMap<>();
		final ModuleDataGroup engineGroup = new ModuleDataGroup(Localization.MODULE_INFO.ENGINE_GROUP);
		final ModuleData coalStandard = new ModuleData(0, "Coal Engine", ModuleCoalStandard.class, 15).addRecipe(new Object[][] { { Items.IRON_INGOT, Items.IRON_INGOT, Items.IRON_INGOT },
			{ Items.IRON_INGOT, Blocks.FURNACE, Items.IRON_INGOT }, { Blocks.PISTON, null, Blocks.PISTON } });
		final ModuleData coalTiny = new ModuleData(44, "Tiny Coal Engine", ModuleCoalTiny.class, 2).addRecipe(new Object[][] { { Items.IRON_INGOT, Blocks.FURNACE, Items.IRON_INGOT },
			{ null, Blocks.PISTON, null } });
		addNemesis(coalTiny, coalStandard);
		final ModuleData solar1 = new ModuleData(1, "Solar Engine", ModuleSolarStandard.class, 20).addSides(new SIDE[] { SIDE.CENTER, SIDE.TOP }).removeModel("Top").addRecipe(new Object[][] {
			{ Items.IRON_INGOT, ComponentTypes.SOLAR_PANEL.getItemStack(), Items.IRON_INGOT },
			{ ComponentTypes.SOLAR_PANEL.getItemStack(), ComponentTypes.ADVANCED_PCB.getItemStack(), ComponentTypes.SOLAR_PANEL.getItemStack() },
			{ Blocks.PISTON, ComponentTypes.SOLAR_PANEL.getItemStack(), Blocks.PISTON } });
		final ModuleData solar2 = new ModuleData(45, "Basic Solar Engine", ModuleSolarBasic.class, 12).addSides(new SIDE[] { SIDE.CENTER, SIDE.TOP }).removeModel("Top").addRecipe(new Object[][] {
			{ ComponentTypes.SOLAR_PANEL.getItemStack(), Items.IRON_INGOT, ComponentTypes.SOLAR_PANEL.getItemStack() },
			{ Items.IRON_INGOT, ComponentTypes.SIMPLE_PCB.getItemStack(), Items.IRON_INGOT }, { null, Blocks.PISTON, null } });
		final ModuleData compactsolar = new ModuleData(56, "Compact Solar Engine", ModuleSolarCompact.class, 32).addSides(new SIDE[] { SIDE.RIGHT, SIDE.LEFT }).addRecipe(new Object[][] {
			{ ComponentTypes.ADVANCED_SOLAR_PANEL.getItemStack(), Items.IRON_INGOT, ComponentTypes.ADVANCED_SOLAR_PANEL.getItemStack() },
			{ ComponentTypes.ADVANCED_PCB.getItemStack(), Items.REDSTONE, ComponentTypes.ADVANCED_PCB.getItemStack() }, { Blocks.PISTON, Items.IRON_INGOT, Blocks.PISTON } });
		new ModuleData(2, "Side Chests", ModuleSideChests.class, 3).addSides(new SIDE[] { SIDE.RIGHT, SIDE.LEFT }).addRecipe(new Object[][] {
			{ ComponentTypes.HUGE_CHEST_PANE.getItemStack(), ComponentTypes.CHEST_PANE.getItemStack(), ComponentTypes.HUGE_CHEST_PANE.getItemStack() },
			{ ComponentTypes.LARGE_CHEST_PANE.getItemStack(), ComponentTypes.CHEST_LOCK.getItemStack(), ComponentTypes.LARGE_CHEST_PANE.getItemStack() },
			{ ComponentTypes.HUGE_CHEST_PANE.getItemStack(), ComponentTypes.CHEST_PANE.getItemStack(), ComponentTypes.HUGE_CHEST_PANE.getItemStack() } });
		new ModuleData(3, "Top Chest", ModuleTopChest.class, 6).addSide(SIDE.TOP).addRecipe(new Object[][] {
			{ ComponentTypes.HUGE_CHEST_PANE.getItemStack(), ComponentTypes.HUGE_CHEST_PANE.getItemStack(), ComponentTypes.HUGE_CHEST_PANE.getItemStack() },
			{ ComponentTypes.CHEST_PANE.getItemStack(), ComponentTypes.CHEST_LOCK.getItemStack(), ComponentTypes.CHEST_PANE.getItemStack() },
			{ ComponentTypes.HUGE_CHEST_PANE.getItemStack(), ComponentTypes.HUGE_CHEST_PANE.getItemStack(), ComponentTypes.HUGE_CHEST_PANE.getItemStack() } });
		final ModuleData frontChest = new ModuleData(4, "Front Chest", ModuleFrontChest.class, 5).addSide(SIDE.FRONT).addRecipe(new Object[][] {
			{ ComponentTypes.CHEST_PANE.getItemStack(), ComponentTypes.LARGE_CHEST_PANE.getItemStack(), ComponentTypes.CHEST_PANE.getItemStack() },
			{ ComponentTypes.CHEST_PANE.getItemStack(), ComponentTypes.CHEST_LOCK.getItemStack(), ComponentTypes.CHEST_PANE.getItemStack() },
			{ ComponentTypes.LARGE_CHEST_PANE.getItemStack(), ComponentTypes.LARGE_CHEST_PANE.getItemStack(), ComponentTypes.LARGE_CHEST_PANE.getItemStack() } });
		new ModuleData(5, "Internal Storage", ModuleInternalStorage.class, 25).setAllowDuplicate().addRecipe(new Object[][] {
			{ ComponentTypes.CHEST_PANE.getItemStack(), ComponentTypes.CHEST_PANE.getItemStack(), ComponentTypes.CHEST_PANE.getItemStack() },
			{ ComponentTypes.CHEST_PANE.getItemStack(), ComponentTypes.CHEST_LOCK.getItemStack(), ComponentTypes.CHEST_PANE.getItemStack() },
			{ ComponentTypes.CHEST_PANE.getItemStack(), ComponentTypes.CHEST_PANE.getItemStack(), ComponentTypes.CHEST_PANE.getItemStack() } });
		new ModuleData(6, "Extracting Chests", ModuleExtractingChests.class, 75).addSides(new SIDE[] { SIDE.CENTER, SIDE.RIGHT, SIDE.LEFT }).addRecipe(new Object[][] {
			{ ComponentTypes.HUGE_IRON_PANE.getItemStack(), ComponentTypes.HUGE_IRON_PANE.getItemStack(), ComponentTypes.HUGE_IRON_PANE.getItemStack() },
			{ ComponentTypes.LARGE_IRON_PANE.getItemStack(), ComponentTypes.CHEST_LOCK.getItemStack(), ComponentTypes.LARGE_IRON_PANE.getItemStack() },
			{ ComponentTypes.HUGE_DYNAMIC_PANE.getItemStack(), ComponentTypes.LARGE_DYNAMIC_PANE.getItemStack(), ComponentTypes.HUGE_DYNAMIC_PANE.getItemStack() } });
		new ModuleData(7, "Torch Placer", ModuleTorch.class, 14).addSides(new SIDE[] { SIDE.RIGHT, SIDE.LEFT }).addRecipe(new Object[][] {
			{ ComponentTypes.TRI_TORCH.getItemStack(), null, ComponentTypes.TRI_TORCH.getItemStack() }, { Items.IRON_INGOT, null, Items.IRON_INGOT },
			{ Items.IRON_INGOT, Items.IRON_INGOT, Items.IRON_INGOT } });
		final ModuleData drill = new ModuleDataTool(8, "Basic Drill", ModuleDrillDiamond.class, 10, false).addSide(SIDE.FRONT).addRecipe(new Object[][] { { Items.IRON_INGOT, Items.DIAMOND, null },
			{ null, Items.IRON_INGOT, Items.DIAMOND }, { Items.IRON_INGOT, Items.DIAMOND, null } });
		final ModuleData ironDrill = new ModuleDataTool(42, "Iron Drill", ModuleDrillIron.class, 3, false).addSide(SIDE.FRONT).addRecipe(new Object[][] { { Items.IRON_INGOT, Items.IRON_INGOT, null },
			{ null, Items.IRON_INGOT, Items.IRON_INGOT }, { Items.IRON_INGOT, Items.IRON_INGOT, null } });
		final ModuleData hardeneddrill = new ModuleDataTool(43, "Hardened Drill", ModuleDrillHardened.class, 45, false).addSide(SIDE.FRONT).addRecipe(new Object[][] {
			{ ComponentTypes.HARDENED_MESH.getItemStack(), ComponentTypes.REINFORCED_METAL.getItemStack(), null },
			{ Blocks.DIAMOND_BLOCK, drill.getItemStack(), ComponentTypes.REINFORCED_METAL.getItemStack() },
			{ ComponentTypes.HARDENED_MESH.getItemStack(), ComponentTypes.REINFORCED_METAL.getItemStack(), null } });
		final ModuleData galgdrill = new ModuleDataTool(9, "Galgadorian Drill", ModuleDrillGalgadorian.class, 150, true).addSide(SIDE.FRONT).addRecipe(new Object[][] {
			{ ComponentTypes.GALGADORIAN_METAL.getItemStack(), ComponentTypes.ENHANCED_GALGADORIAN_METAL.getItemStack(), null },
			{ Blocks.DIAMOND_BLOCK, hardeneddrill.getItemStack(), ComponentTypes.ENHANCED_GALGADORIAN_METAL.getItemStack() },
			{ ComponentTypes.GALGADORIAN_METAL.getItemStack(), ComponentTypes.ENHANCED_GALGADORIAN_METAL.getItemStack(), null } });
		final ModuleDataGroup drillGroup = new ModuleDataGroup(Localization.MODULE_INFO.DRILL_GROUP);
		drillGroup.add(drill);
		drillGroup.add(ironDrill);
		drillGroup.add(hardeneddrill);
		drillGroup.add(galgdrill);
		final ModuleData railer = new ModuleData(10, "Railer", ModuleRailer.class, 3).addRecipe(new Object[][] { { Blocks.STONE, Blocks.STONE, Blocks.STONE },
			{ Items.IRON_INGOT, Blocks.RAIL, Items.IRON_INGOT }, { Blocks.STONE, Blocks.STONE, Blocks.STONE } });
		final ModuleData largerailer = new ModuleData(11, "Large Railer", ModuleRailerLarge.class, 17).addRecipe(new Object[][] { { Items.IRON_INGOT, Items.IRON_INGOT, Items.IRON_INGOT },
			{ railer.getItemStack(), Blocks.RAIL, railer.getItemStack() }, { Items.IRON_INGOT, Items.IRON_INGOT, Items.IRON_INGOT } });
		addNemesis(railer, largerailer);
		new ModuleData(12, "Bridge Builder", ModuleBridge.class, 14).addRecipe(new Object[][] { { null, Items.REDSTONE, null },
			{ Blocks.BRICK_BLOCK, ComponentTypes.SIMPLE_PCB.getItemStack(), Blocks.BRICK_BLOCK }, { null, Blocks.PISTON, null } });
		new ModuleData(13, "Track Remover", ModuleRemover.class, 8).addSides(new SIDE[] { SIDE.TOP, SIDE.BACK }).addRecipe(new Object[][] { { Items.IRON_INGOT, Items.IRON_INGOT, Items.IRON_INGOT },
			{ Items.IRON_INGOT, null, Items.IRON_INGOT }, { Items.IRON_INGOT, null, null } });
		final ModuleDataGroup farmerGroup = new ModuleDataGroup(Localization.MODULE_INFO.FARMER_GROUP);
		final ModuleData farmerbasic = new ModuleDataTool(14, "Basic Farmer", ModuleFarmerDiamond.class, 36, false).addSide(SIDE.FRONT).addRecipe(new Object[][] {
			{ Items.DIAMOND, Items.DIAMOND, Items.DIAMOND }, { null, Items.IRON_INGOT, null },
			{ ComponentTypes.SIMPLE_PCB.getItemStack(), Items.GOLD_INGOT, ComponentTypes.SIMPLE_PCB.getItemStack() } });
		final ModuleData farmergalg = new ModuleDataTool(84, "Galgadorian Farmer", ModuleFarmerGalgadorian.class, 55, true).addSide(SIDE.FRONT).addRecipe(new Object[][] {
			{ ComponentTypes.GALGADORIAN_METAL.getItemStack(), ComponentTypes.GALGADORIAN_METAL.getItemStack(), ComponentTypes.GALGADORIAN_METAL.getItemStack() },
			{ null, ComponentTypes.REINFORCED_METAL.getItemStack(), null },
			{ ComponentTypes.ADVANCED_PCB.getItemStack(), Items.GOLD_INGOT, ComponentTypes.ADVANCED_PCB.getItemStack() } }).addRecipe(new Object[][] {
			{ ComponentTypes.GALGADORIAN_METAL.getItemStack(), ComponentTypes.GALGADORIAN_METAL.getItemStack(), ComponentTypes.GALGADORIAN_METAL.getItemStack() },
			{ null, farmerbasic.getItemStack(), null }, { null, ComponentTypes.SIMPLE_PCB.getItemStack(), null } });
		farmerGroup.add(farmerbasic);
		farmerGroup.add(farmergalg);
		final ModuleDataGroup woodcutterGroup = new ModuleDataGroup(Localization.MODULE_INFO.CUTTER_GROUP);
		final ModuleData woodcutter = new ModuleDataTool(15, "Basic Wood Cutter", ModuleWoodcutterDiamond.class, 34, false).addSide(SIDE.FRONT).addRecipe(new Object[][] {
			{ ComponentTypes.SAW_BLADE.getItemStack(), ComponentTypes.SAW_BLADE.getItemStack(), ComponentTypes.SAW_BLADE.getItemStack() },
			{ ComponentTypes.SAW_BLADE.getItemStack(), Items.IRON_INGOT, ComponentTypes.SAW_BLADE.getItemStack() }, { null, ComponentTypes.WOOD_CUTTING_CORE.getItemStack(), null } });
		final ModuleData woodcutterHardened = new ModuleDataTool(79, "Hardened Wood Cutter", ModuleWoodcutterHardened.class, 65, false).addSide(SIDE.FRONT).addRecipe(new Object[][] {
			{ ComponentTypes.HARDENED_SAW_BLADE.getItemStack(), ComponentTypes.HARDENED_SAW_BLADE.getItemStack(), ComponentTypes.HARDENED_SAW_BLADE.getItemStack() },
			{ ComponentTypes.HARDENED_SAW_BLADE.getItemStack(), Items.DIAMOND, ComponentTypes.HARDENED_SAW_BLADE.getItemStack() },
			{ null, ComponentTypes.WOOD_CUTTING_CORE.getItemStack(), null } }).addRecipe(new Object[][] {
			{ ComponentTypes.REINFORCED_METAL.getItemStack(), ComponentTypes.REINFORCED_METAL.getItemStack(), ComponentTypes.REINFORCED_METAL.getItemStack() },
			{ ComponentTypes.REINFORCED_METAL.getItemStack(), Items.IRON_INGOT, ComponentTypes.REINFORCED_METAL.getItemStack() }, { null, woodcutter.getItemStack(), null } });
		final ModuleData woodcutterGalgadorian = new ModuleDataTool(80, "Galgadorian Wood Cutter", ModuleWoodcutterGalgadorian.class, 120, true).addSide(SIDE.FRONT).addRecipe(new Object[][] {
			{ ComponentTypes.GALGADORIAN_SAW_BLADE.getItemStack(), ComponentTypes.GALGADORIAN_SAW_BLADE.getItemStack(), ComponentTypes.GALGADORIAN_SAW_BLADE.getItemStack() },
			{ ComponentTypes.GALGADORIAN_SAW_BLADE.getItemStack(), ComponentTypes.REINFORCED_METAL.getItemStack(), ComponentTypes.GALGADORIAN_SAW_BLADE.getItemStack() },
			{ null, ComponentTypes.WOOD_CUTTING_CORE.getItemStack(), null } }).addRecipe(new Object[][] {
			{ ComponentTypes.GALGADORIAN_METAL.getItemStack(), ComponentTypes.GALGADORIAN_METAL.getItemStack(), ComponentTypes.GALGADORIAN_METAL.getItemStack() },
			{ ComponentTypes.GALGADORIAN_METAL.getItemStack(), Items.IRON_INGOT, ComponentTypes.GALGADORIAN_METAL.getItemStack() }, { null, woodcutterHardened.getItemStack(), null } });
		woodcutterGroup.add(woodcutter);
		woodcutterGroup.add(woodcutterHardened);
		woodcutterGroup.add(woodcutterGalgadorian);
		final ModuleDataGroup tankGroup = new ModuleDataGroup(Localization.MODULE_INFO.TANK_GROUP);
		new ModuleData(16, "Hydrator", ModuleHydrater.class, 6).addRequirement(tankGroup).addRecipe(new Object[][] { { Items.IRON_INGOT, Items.GLASS_BOTTLE, Items.IRON_INGOT },
			{ null, Blocks.OAK_FENCE, null } });
		new ModuleData(18, "Fertilizer", ModuleFertilizer.class, 10).addRecipe(new Object[][] { { bonemeal, null, bonemeal }, { Items.GLASS_BOTTLE, Items.LEATHER, Items.GLASS_BOTTLE },
			{ Items.LEATHER, ComponentTypes.SIMPLE_PCB.getItemStack(), Items.LEATHER } });
		new ModuleData(19, "Height Controller", ModuleHeightControl.class, 20).addRecipe(new Object[][] { { null, Items.COMPASS, null },
			{ Items.PAPER, ComponentTypes.SIMPLE_PCB.getItemStack(), Items.PAPER }, { Items.PAPER, Items.PAPER, Items.PAPER } });
		final ModuleData liquidsensors = new ModuleData(20, "Liquid Sensors", ModuleLiquidSensors.class, 27).addRequirement(drillGroup).addRecipe(new Object[][] {
			{ Items.REDSTONE, null, Items.REDSTONE }, { Items.LAVA_BUCKET, Items.DIAMOND, Items.WATER_BUCKET }, { Items.IRON_INGOT, ComponentTypes.ADVANCED_PCB.getItemStack(), Items.IRON_INGOT } });
		final ModuleData seat = new ModuleData(25, "Seat", ModuleSeat.class, 3).addSides(new SIDE[] { SIDE.CENTER, SIDE.TOP }).addRecipe(new Object[][] { { null, planks }, { null, planks },
			{ woodSingleSlab, planks } });
		new ModuleData(26, "Brake Handle", ModuleBrake.class, 12).addSide(SIDE.RIGHT).addParent(seat).addRecipe(new Object[][] { { null, null, new ItemStack(Items.DYE, 1, 1) },
			{ Items.IRON_INGOT, ComponentTypes.REFINED_HANDLE.getItemStack(), null }, { Items.REDSTONE, Items.IRON_INGOT, null } });
		new ModuleData(27, "Advanced Control System", ModuleAdvControl.class, 38).addSide(SIDE.RIGHT).addParent(seat).addRecipe(new Object[][] {
			{ null, ComponentTypes.GRAPHICAL_INTERFACE.getItemStack(), null }, { Items.REDSTONE, ComponentTypes.WHEEL.getItemStack(), Items.REDSTONE },
			{ Items.IRON_INGOT, Items.IRON_INGOT, ComponentTypes.SPEED_HANDLE.getItemStack() } });
		final ModuleDataGroup detectorGroup = new ModuleDataGroup(Localization.MODULE_INFO.ENTITY_GROUP);
		final ModuleData shooter = new ModuleData(28, "Shooter", ModuleShooter.class, 15).addSide(SIDE.TOP).addRecipe(new Object[][] {
			{ ComponentTypes.PIPE.getItemStack(), ComponentTypes.PIPE.getItemStack(), ComponentTypes.PIPE.getItemStack() },
			{ ComponentTypes.PIPE.getItemStack(), ComponentTypes.SHOOTING_STATION.getItemStack(), ComponentTypes.PIPE.getItemStack() },
			{ ComponentTypes.PIPE.getItemStack(), ComponentTypes.PIPE.getItemStack(), ComponentTypes.PIPE.getItemStack() } });
		final ModuleData advshooter = new ModuleData(29, "Advanced Shooter", ModuleShooterAdv.class, 50).addSide(SIDE.TOP).addRequirement(detectorGroup).addRecipe(new Object[][] {
			{ null, ComponentTypes.ENTITY_SCANNER.getItemStack(), null }, { null, ComponentTypes.SHOOTING_STATION.getItemStack(), ComponentTypes.PIPE.getItemStack() },
			{ Items.IRON_INGOT, ComponentTypes.ENTITY_ANALYZER.getItemStack(), Items.IRON_INGOT } });
		final ModuleDataGroup shooterGroup = new ModuleDataGroup(Localization.MODULE_INFO.SHOOTER_GROUP);
		shooterGroup.add(shooter);
		shooterGroup.add(advshooter);
		final ModuleData animal = new ModuleData(21, "Entity Detector: Animal", ModuleAnimal.class, 1).addParent(advshooter).addRecipe(new Object[][] { { Items.PORKCHOP },
			{ ComponentTypes.EMPTY_DISK.getItemStack() } });
		final ModuleData player = new ModuleData(22, "Entity Detector: Player", ModulePlayer.class, 7).addParent(advshooter).addRecipe(new Object[][] { { Items.DIAMOND },
			{ ComponentTypes.EMPTY_DISK.getItemStack() } });
		final ModuleData villager = new ModuleData(23, "Entity Detector: Villager", ModuleVillager.class, 1).addParent(advshooter).addRecipe(new Object[][] { { Items.EMERALD },
			{ ComponentTypes.EMPTY_DISK.getItemStack() } });
		final ModuleData monster = new ModuleData(24, "Entity Detector: Monster", ModuleMonster.class, 1).addParent(advshooter).addRecipe(new Object[][] { { Items.SLIME_BALL },
			{ ComponentTypes.EMPTY_DISK.getItemStack() } });
		final ModuleData bats = new ModuleData(48, "Entity Detector: Bat", ModuleBat.class, 1).addParent(advshooter).addRecipe(new Object[][] { { Blocks.PUMPKIN },
			{ ComponentTypes.EMPTY_DISK.getItemStack() } });
		if (!Constants.isHalloween) {
			bats.lock();
		}
		detectorGroup.add(animal);
		detectorGroup.add(player);
		detectorGroup.add(villager);
		detectorGroup.add(monster);
		detectorGroup.add(bats);
		final ModuleData cleaner = new ModuleData(30, "Cleaning Machine", ModuleCleaner.class, 23).addSide(SIDE.CENTER).addRecipe(new Object[][] {
			{ ComponentTypes.CLEANING_TUBE.getItemStack(), ComponentTypes.CLEANING_CORE.getItemStack(), ComponentTypes.CLEANING_TUBE.getItemStack() },
			{ ComponentTypes.CLEANING_TUBE.getItemStack(), null, ComponentTypes.CLEANING_TUBE.getItemStack() },
			{ ComponentTypes.CLEANING_TUBE.getItemStack(), null, ComponentTypes.CLEANING_TUBE.getItemStack() } });
		addNemesis(frontChest, cleaner);
		new ModuleData(31, "Dynamite Carrier", ModuleDynamite.class, 3).addSide(SIDE.TOP).addRecipe(new Object[][] { { null, ComponentTypes.DYNAMITE.getItemStack(), null },
			{ ComponentTypes.DYNAMITE.getItemStack(), Items.FLINT_AND_STEEL, ComponentTypes.DYNAMITE.getItemStack() }, { null, ComponentTypes.DYNAMITE.getItemStack(), null } });
		new ModuleData(32, "Divine Shield", ModuleShield.class, 60).addRecipe(new Object[][] { { Blocks.OBSIDIAN, ComponentTypes.REFINED_HARDENER.getItemStack(), Blocks.OBSIDIAN },
			{ ComponentTypes.REFINED_HARDENER.getItemStack(), Blocks.DIAMOND_BLOCK, ComponentTypes.REFINED_HARDENER.getItemStack() },
			{ Blocks.OBSIDIAN, ComponentTypes.REFINED_HARDENER.getItemStack(), Blocks.OBSIDIAN } });
		final ModuleData melter = new ModuleData(33, "Melter", ModuleMelter.class, 10).addRecipe(new Object[][] { { Blocks.NETHER_BRICK, Blocks.GLOWSTONE, Blocks.NETHER_BRICK },
			{ Items.GLOWSTONE_DUST, Blocks.FURNACE, Items.GLOWSTONE_DUST }, { Blocks.NETHER_BRICK, Blocks.GLOWSTONE, Blocks.NETHER_BRICK } });
		final ModuleData extrememelter = new ModuleData(34, "Extreme Melter", ModuleMelterExtreme.class, 19).addRecipe(new Object[][] { { Blocks.NETHER_BRICK, Blocks.OBSIDIAN, Blocks.NETHER_BRICK },
			{ melter.getItemStack(), Items.LAVA_BUCKET, melter.getItemStack() }, { Blocks.NETHER_BRICK, Blocks.OBSIDIAN, Blocks.NETHER_BRICK } });
		addNemesis(melter, extrememelter);
		new ModuleData(36, "Invisibility Core", ModuleInvisible.class, 21).addRecipe(new Object[][] { { null, ComponentTypes.GLASS_O_MAGIC.getItemStack(), null },
			{ ComponentTypes.GLASS_O_MAGIC.getItemStack(), Items.ENDER_EYE, ComponentTypes.GLASS_O_MAGIC.getItemStack() }, { null, Items.GOLDEN_CARROT, null } });
		new ModuleDataHull(37, "Wooden Hull", ModuleWood.class).setCapacity(50).setEngineMax(1).setAddonMax(0).setComplexityMax(15).addRecipe(new Object[][] { { planks, null, planks },
			{ planks, planks, planks }, { ComponentTypes.WOODEN_WHEELS.getItemStack(), null, ComponentTypes.WOODEN_WHEELS.getItemStack() } });
		new ModuleDataHull(38, "Standard Hull", ModuleStandard.class).setCapacity(200).setEngineMax(3).setAddonMax(6).setComplexityMax(50).addRecipe(new Object[][] {
			{ Items.IRON_INGOT, null, Items.IRON_INGOT }, { Items.IRON_INGOT, Items.IRON_INGOT, Items.IRON_INGOT },
			{ ComponentTypes.IRON_WHEELS.getItemStack(), null, ComponentTypes.IRON_WHEELS.getItemStack() } });
		final ModuleData reinfhull = new ModuleDataHull(39, "Reinforced Hull", ModuleReinforced.class).setCapacity(500).setEngineMax(5).setAddonMax(12).setComplexityMax(150).addRecipe(new Object[][] {
			{ ComponentTypes.REINFORCED_METAL.getItemStack(), null, ComponentTypes.REINFORCED_METAL.getItemStack() },
			{ ComponentTypes.REINFORCED_METAL.getItemStack(), ComponentTypes.REINFORCED_METAL.getItemStack(), ComponentTypes.REINFORCED_METAL.getItemStack() },
			{ ComponentTypes.REINFORCED_WHEELS.getItemStack(), null, ComponentTypes.REINFORCED_WHEELS.getItemStack() } });
		final ModuleData pumpkinhull = new ModuleDataHull(47, "Pumpkin chariot", ModulePumpkin.class).setCapacity(40).setEngineMax(1).setAddonMax(0).setComplexityMax(15).addRecipe(new Object[][] {
			{ planks, null, planks }, { planks, Blocks.PUMPKIN, planks }, { ComponentTypes.WOODEN_WHEELS.getItemStack(), null, ComponentTypes.WOODEN_WHEELS.getItemStack() } });
		if (!Constants.isHalloween) {
			pumpkinhull.lock();
		}
		new ModuleDataHull(62, "Mechanical Pig", ModulePig.class).setCapacity(150).setEngineMax(2).setAddonMax(4).setComplexityMax(50).addSide(SIDE.FRONT).addRecipe(new Object[][] {
			{ Items.PORKCHOP, null, Items.PORKCHOP }, { Items.PORKCHOP, Items.PORKCHOP, Items.PORKCHOP },
			{ ComponentTypes.IRON_WHEELS.getItemStack(), null, ComponentTypes.IRON_WHEELS.getItemStack() } }).addMessage(Localization.MODULE_INFO.PIG_MESSAGE);
		new ModuleDataHull(76, "Creative Hull", ModuleCheatHull.class).setCapacity(10000).setEngineMax(5).setAddonMax(12).setComplexityMax(150);
		new ModuleDataHull(81, "Galgadorian Hull", ModuleGalgadorian.class).setCapacity(1000).setEngineMax(5).setAddonMax(12).setComplexityMax(150).addRecipe(new Object[][] {
			{ ComponentTypes.GALGADORIAN_METAL.getItemStack(), null, ComponentTypes.GALGADORIAN_METAL.getItemStack() },
			{ ComponentTypes.GALGADORIAN_METAL.getItemStack(), ComponentTypes.GALGADORIAN_METAL.getItemStack(), ComponentTypes.GALGADORIAN_METAL.getItemStack() },
			{ ComponentTypes.GALGADORIAN_WHEELS.getItemStack(), null, ComponentTypes.GALGADORIAN_WHEELS.getItemStack() } });
		StevesCarts.tabsSC2.setIcon(reinfhull.getItemStack());
		StevesCarts.tabsSC2Components.setIcon(ComponentTypes.REINFORCED_WHEELS.getItemStack());
		new ModuleData(40, "Note Sequencer", ModuleNote.class, 30).addSides(new SIDE[] { SIDE.RIGHT, SIDE.LEFT }).addRecipe(new Object[][] { { Blocks.NOTEBLOCK, null, Blocks.NOTEBLOCK },
			{ Blocks.NOTEBLOCK, Blocks.JUKEBOX, Blocks.NOTEBLOCK }, { planks, Items.REDSTONE, planks } });
		final ModuleData colorizer = new ModuleData(41, "Colorizer", ModuleColorizer.class, 15).addRecipe(new Object[][] {
			{ ComponentTypes.RED_PIGMENT.getItemStack(), ComponentTypes.GREEN_PIGMENT.getItemStack(), ComponentTypes.BLUE_PIGMENT.getItemStack() },
			{ Items.IRON_INGOT, Items.REDSTONE, Items.IRON_INGOT }, { null, Items.IRON_INGOT, null } });
		final ModuleData colorRandomizer = new ModuleData(101, "Color Randomizer", ModuleColorRandomizer.class, 20).addRecipe(new Object[][] { { colorizer.getItemStack() },
			{ ComponentTypes.SIMPLE_PCB.getItemStack() } });
		addNemesis(colorizer, colorRandomizer);
		new ModuleData(49, "Chunk Loader", ModuleChunkLoader.class, 84).addRecipe(new Object[][] { { null, Items.ENDER_PEARL, null },
			{ ComponentTypes.SIMPLE_PCB.getItemStack(), Items.IRON_INGOT, ComponentTypes.SIMPLE_PCB.getItemStack() },
			{ ComponentTypes.REINFORCED_METAL.getItemStack(), ComponentTypes.ADVANCED_PCB.getItemStack(), ComponentTypes.REINFORCED_METAL.getItemStack() } });
		final ModuleData gift = new ModuleData(50, "Gift Storage", ModuleGiftStorage.class, 12) {
			@Override
			public String getModuleInfoText(final byte b) {
				if (b == 0) {
					return Localization.MODULE_INFO.STORAGE_EMPTY.translate();
				}
				return Localization.MODULE_INFO.GIFT_STORAGE_FULL.translate();
			}

			@Override
			public String getCartInfoText(final String name, final byte b) {
				if (b == 0) {
					return Localization.MODULE_INFO.STORAGE_EMPTY.translate() + " " + name;
				}
				return Localization.MODULE_INFO.STORAGE_FULL.translate() + " " + name;
			}
		}.addSides(new SIDE[] { SIDE.RIGHT, SIDE.LEFT }).useExtraData((byte) 1).addRecipe(new Object[][] {
			{ ComponentTypes.YELLOW_GIFT_RIBBON.getItemStack(), null, ComponentTypes.RED_GIFT_RIBBON.getItemStack() },
			{ ComponentTypes.RED_WRAPPING_PAPER.getItemStack(), ComponentTypes.CHEST_LOCK.getItemStack(), ComponentTypes.GREEN_WRAPPING_PAPER.getItemStack() },
			{ ComponentTypes.RED_WRAPPING_PAPER.getItemStack(), ComponentTypes.STUFFED_SOCK.getItemStack(), ComponentTypes.GREEN_WRAPPING_PAPER.getItemStack() } });
		if (!Constants.isChristmas) {
			gift.lock();
		}
		new ModuleData(51, "Projectile: Potion", ModulePotion.class, 10).addRequirement(shooterGroup).addRecipe(new Object[][] { { Items.GLASS_BOTTLE },
			{ ComponentTypes.EMPTY_DISK.getItemStack() } });
		new ModuleData(52, "Projectile: Fire Charge", ModuleFireball.class, 10).lockByDefault().addRequirement(shooterGroup).addRecipe(new Object[][] { { Items.FIRE_CHARGE },
			{ ComponentTypes.EMPTY_DISK.getItemStack() } });
		new ModuleData(53, "Projectile: Egg", ModuleEgg.class, 10).addRequirement(shooterGroup).addRecipe(new Object[][] { { Items.EGG }, { ComponentTypes.EMPTY_DISK.getItemStack() } });
		final ModuleData snowballshooter = new ModuleData(54, "Projectile: Snowball", ModuleSnowball.class, 10).addRequirement(shooterGroup).addRecipe(new Object[][] { { Items.SNOWBALL },
			{ ComponentTypes.EMPTY_DISK.getItemStack() } });
		if (!Constants.isChristmas) {
			snowballshooter.lock();
		}
		final ModuleData cake = new ModuleData(90, "Projectile: Cake", ModuleCake.class, 10).addRequirement(shooterGroup).lock().addRecipe(new Object[][] { { Items.CAKE },
			{ ComponentTypes.EMPTY_DISK.getItemStack() } });
		final ModuleData snowgenerator = new ModuleData(55, "Freezer", ModuleSnowCannon.class, 24).addRecipe(new Object[][] { { Blocks.SNOW, Items.WATER_BUCKET, Blocks.SNOW },
			{ Items.WATER_BUCKET, ComponentTypes.SIMPLE_PCB.getItemStack(), Items.WATER_BUCKET }, { Blocks.SNOW, Items.WATER_BUCKET, Blocks.SNOW } });
		if (!Constants.isChristmas) {
			snowgenerator.lock();
		}
		addNemesis(snowgenerator, melter);
		addNemesis(snowgenerator, extrememelter);
		final ModuleData cage = new ModuleData(57, "Cage", ModuleCage.class, 7).addSides(new SIDE[] { SIDE.TOP, SIDE.CENTER }).addRecipe(new Object[][] {
			{ Blocks.OAK_FENCE, Blocks.OAK_FENCE, Blocks.OAK_FENCE },
			{ Blocks.OAK_FENCE, ComponentTypes.SIMPLE_PCB.getItemStack(), Blocks.OAK_FENCE }, { Blocks.OAK_FENCE, Blocks.OAK_FENCE, Blocks.OAK_FENCE } });
		new ModuleData(58, "Crop: Nether Wart", ModuleNetherwart.class, 20).addRequirement(farmerGroup).addRecipe(new Object[][] { { Items.NETHER_WART },
			{ ComponentTypes.EMPTY_DISK.getItemStack() } });
		new ModuleData(59, "Firework display", ModuleFirework.class, 45).addRecipe(new Object[][] { { Blocks.OAK_FENCE, Blocks.DISPENSER, Blocks.OAK_FENCE },
			{ Blocks.CRAFTING_TABLE, ComponentTypes.FUSE.getItemStack(), Blocks.CRAFTING_TABLE },
			{ ComponentTypes.SIMPLE_PCB.getItemStack(), Items.FLINT_AND_STEEL, ComponentTypes.SIMPLE_PCB.getItemStack() } });
		final ModuleData cheatengine = new ModuleData(61, "Creative Engine", ModuleCheatEngine.class, 1);
		final ModuleData internalTank = new ModuleData(63, "Internal SCTank", ModuleInternalTank.class, 37).setAllowDuplicate().addRecipe(new Object[][] {
			{ ComponentTypes.TANK_PANE.getItemStack(), ComponentTypes.TANK_PANE.getItemStack(), ComponentTypes.TANK_PANE.getItemStack() },
			{ ComponentTypes.TANK_PANE.getItemStack(), ComponentTypes.TANK_VALVE.getItemStack(), ComponentTypes.TANK_PANE.getItemStack() },
			{ ComponentTypes.TANK_PANE.getItemStack(), ComponentTypes.TANK_PANE.getItemStack(), ComponentTypes.TANK_PANE.getItemStack() } });
		final ModuleData sideTank = new ModuleData(64, "Side Tanks", ModuleSideTanks.class, 10).addSides(new SIDE[] { SIDE.RIGHT, SIDE.LEFT }).addRecipe(new Object[][] {
			{ ComponentTypes.HUGE_TANK_PANE.getItemStack(), ComponentTypes.TANK_PANE.getItemStack(), ComponentTypes.HUGE_TANK_PANE.getItemStack() },
			{ ComponentTypes.LARGE_TANK_PANE.getItemStack(), ComponentTypes.TANK_VALVE.getItemStack(), ComponentTypes.LARGE_TANK_PANE.getItemStack() },
			{ ComponentTypes.HUGE_TANK_PANE.getItemStack(), ComponentTypes.TANK_PANE.getItemStack(), ComponentTypes.HUGE_TANK_PANE.getItemStack() } });
		final ModuleData topTank = new ModuleData(65, "Top SCTank", ModuleTopTank.class, 22).addSide(SIDE.TOP).addRecipe(new Object[][] {
			{ ComponentTypes.HUGE_TANK_PANE.getItemStack(), ComponentTypes.HUGE_TANK_PANE.getItemStack(), ComponentTypes.HUGE_TANK_PANE.getItemStack() },
			{ ComponentTypes.TANK_PANE.getItemStack(), ComponentTypes.TANK_VALVE.getItemStack(), ComponentTypes.TANK_PANE.getItemStack() },
			{ ComponentTypes.HUGE_TANK_PANE.getItemStack(), ComponentTypes.HUGE_TANK_PANE.getItemStack(), ComponentTypes.HUGE_TANK_PANE.getItemStack() } });
		final ModuleData advancedTank = new ModuleData(66, "Advanced SCTank", ModuleAdvancedTank.class, 54).addSides(new SIDE[] { SIDE.TOP, SIDE.CENTER }).addRecipe(new Object[][] {
			{ ComponentTypes.HUGE_TANK_PANE.getItemStack(), ComponentTypes.HUGE_TANK_PANE.getItemStack(), ComponentTypes.HUGE_TANK_PANE.getItemStack() },
			{ ComponentTypes.HUGE_TANK_PANE.getItemStack(), ComponentTypes.TANK_VALVE.getItemStack(), ComponentTypes.HUGE_TANK_PANE.getItemStack() },
			{ ComponentTypes.HUGE_TANK_PANE.getItemStack(), ComponentTypes.HUGE_TANK_PANE.getItemStack(), ComponentTypes.HUGE_TANK_PANE.getItemStack() } });
		final ModuleData frontTank = new ModuleData(67, "Front SCTank", ModuleFrontTank.class, 15).addSide(SIDE.FRONT).addRecipe(new Object[][] {
			{ ComponentTypes.TANK_PANE.getItemStack(), ComponentTypes.LARGE_TANK_PANE.getItemStack(), ComponentTypes.TANK_PANE.getItemStack() },
			{ ComponentTypes.TANK_PANE.getItemStack(), ComponentTypes.TANK_VALVE.getItemStack(), ComponentTypes.TANK_PANE.getItemStack() },
			{ ComponentTypes.LARGE_TANK_PANE.getItemStack(), ComponentTypes.LARGE_TANK_PANE.getItemStack(), ComponentTypes.LARGE_TANK_PANE.getItemStack() } });
		final ModuleData creativeTank = new ModuleData(72, "Creative SCTank", ModuleCheatTank.class, 1).setAllowDuplicate().addMessage(Localization.MODULE_INFO.OCEAN_MESSAGE);
		final ModuleData topTankOpen = new ModuleData(73, "Open SCTank", ModuleOpenTank.class, 31).addSide(SIDE.TOP).addRecipe(new Object[][] {
			{ ComponentTypes.TANK_PANE.getItemStack(), null, ComponentTypes.TANK_PANE.getItemStack() },
			{ ComponentTypes.TANK_PANE.getItemStack(), ComponentTypes.TANK_VALVE.getItemStack(), ComponentTypes.TANK_PANE.getItemStack() },
			{ ComponentTypes.HUGE_TANK_PANE.getItemStack(), ComponentTypes.HUGE_TANK_PANE.getItemStack(), ComponentTypes.HUGE_TANK_PANE.getItemStack() } });
		addNemesis(frontTank, cleaner);
		tankGroup.add(internalTank).add(sideTank).add(topTank).add(advancedTank).add(frontTank).add(creativeTank).add(topTankOpen);
		new ModuleData(68, "Incinerator", ModuleIncinerator.class, 23).addRequirement(tankGroup).addRequirement(drillGroup).addRecipe(new Object[][] {
			{ Blocks.NETHER_BRICK, Blocks.NETHER_BRICK, Blocks.NETHER_BRICK }, { Blocks.OBSIDIAN, Blocks.FURNACE, Blocks.OBSIDIAN },
			{ Blocks.NETHER_BRICK, Blocks.NETHER_BRICK, Blocks.NETHER_BRICK } });
		final ModuleData thermal0 = new ModuleData(69, "Thermal Engine", ModuleThermalStandard.class, 28).addRequirement(tankGroup).addRecipe(new Object[][] {
			{ Blocks.NETHER_BRICK, Blocks.NETHER_BRICK, Blocks.NETHER_BRICK }, { Blocks.OBSIDIAN, Blocks.FURNACE, Blocks.OBSIDIAN }, { Blocks.PISTON, null, Blocks.PISTON } });
		final ModuleData thermal2 = new ModuleData(70, "Advanced Thermal Engine", ModuleThermalAdvanced.class, 58).addRequirement(tankGroup.copy(2)).addRecipe(new Object[][] {
			{ Blocks.NETHER_BRICK, Blocks.NETHER_BRICK, Blocks.NETHER_BRICK },
			{ ComponentTypes.REINFORCED_METAL.getItemStack(), thermal0.getItemStack(), ComponentTypes.REINFORCED_METAL.getItemStack() }, { Blocks.PISTON, null, Blocks.PISTON } });
		addNemesis(thermal0, thermal2);
		final ModuleData cleanerliquid = new ModuleData(71, "Liquid Cleaner", ModuleLiquidDrainer.class, 30).addSide(SIDE.CENTER).addParent(liquidsensors).addRequirement(tankGroup).addRecipe(new Object[][] {
			{ ComponentTypes.LIQUID_CLEANING_TUBE.getItemStack(), ComponentTypes.LIQUID_CLEANING_CORE.getItemStack(), ComponentTypes.LIQUID_CLEANING_TUBE.getItemStack() },
			{ ComponentTypes.LIQUID_CLEANING_TUBE.getItemStack(), null, ComponentTypes.LIQUID_CLEANING_TUBE.getItemStack() },
			{ ComponentTypes.LIQUID_CLEANING_TUBE.getItemStack(), null, ComponentTypes.LIQUID_CLEANING_TUBE.getItemStack() } });
		addNemesis(frontTank, cleanerliquid);
		addNemesis(frontChest, cleanerliquid);
		@Nonnull
		ItemStack yellowWool = new ItemStack(Blocks.WOOL, 1, 4);
		final ModuleData eggBasket = new ModuleData(74, "Egg Basket", ModuleEggBasket.class, 14) {
			@Override
			public String getModuleInfoText(final byte b) {
				if (b == 0) {
					return Localization.MODULE_INFO.STORAGE_EMPTY.translate();
				}
				return Localization.MODULE_INFO.EGG_STORAGE_FULL.translate();
			}

			@Override
			public String getCartInfoText(final String name, final byte b) {
				if (b == 0) {
					return Localization.MODULE_INFO.STORAGE_EMPTY.translate() + " " + name;
				}
				return Localization.MODULE_INFO.STORAGE_FULL.translate() + " " + name;
			}
		}.addSide(SIDE.TOP).useExtraData((byte) 1).addRecipe(new Object[][] { { yellowWool, yellowWool, yellowWool },
			{ ComponentTypes.EXPLOSIVE_EASTER_EGG.getItemStack(), ComponentTypes.CHEST_LOCK.getItemStack(), ComponentTypes.BURNING_EASTER_EGG.getItemStack() },
			{ ComponentTypes.GLISTERING_EASTER_EGG.getItemStack(), ComponentTypes.BASKET.getItemStack(), ComponentTypes.CHOCOLATE_EASTER_EGG.getItemStack() } });
		if (!Constants.isEaster) {
			eggBasket.lock();
		}
		final ModuleData intelligence = new ModuleData(75, "Drill Intelligence", ModuleDrillIntelligence.class, 21).addRequirement(drillGroup).addRecipe(new Object[][] {
			{ Items.GOLD_INGOT, Items.GOLD_INGOT, Items.GOLD_INGOT }, { Items.IRON_INGOT, ComponentTypes.ADVANCED_PCB.getItemStack(), Items.IRON_INGOT },
			{ ComponentTypes.ADVANCED_PCB.getItemStack(), Items.REDSTONE, ComponentTypes.ADVANCED_PCB.getItemStack() } });
		new ModuleData(77, "Power Observer", ModulePowerObserver.class, 12).addRequirement(engineGroup).addRecipe(new Object[][] { { null, Blocks.PISTON, null },
			{ Items.IRON_INGOT, Items.REDSTONE, Items.IRON_INGOT }, { Items.REDSTONE, ComponentTypes.ADVANCED_PCB.getItemStack(), Items.REDSTONE } });
		engineGroup.add(coalTiny);
		engineGroup.add(coalStandard);
		engineGroup.add(solar2);
		engineGroup.add(solar1);
		engineGroup.add(thermal0);
		engineGroup.add(thermal2);
		engineGroup.add(compactsolar);
		engineGroup.add(cheatengine);
		final ModuleDataGroup toolGroup = ModuleDataGroup.getCombinedGroup(Localization.MODULE_INFO.TOOL_GROUP, drillGroup, woodcutterGroup);
		toolGroup.add(farmerGroup);
		final ModuleDataGroup enchantableGroup = ModuleDataGroup.getCombinedGroup(Localization.MODULE_INFO.TOOL_OR_SHOOTER_GROUP, toolGroup, shooterGroup);
		new ModuleData(82, "Enchanter", ModuleEnchants.class, 72).addRequirement(enchantableGroup).addRecipe(new Object[][] { { null, ComponentTypes.GALGADORIAN_METAL.getItemStack(), null },
			{ Items.BOOK, Blocks.ENCHANTING_TABLE, Items.BOOK }, { Items.REDSTONE, ComponentTypes.ADVANCED_PCB.getItemStack(), Items.REDSTONE } });
		new ModuleData(83, "Ore Extractor", ModuleOreTracker.class, 80).addRequirement(drillGroup).addRecipe(new Object[][] { { Blocks.REDSTONE_TORCH, null, Blocks.REDSTONE_TORCH },
			{ ComponentTypes.EYE_OF_GALGADOR.getItemStack(), ComponentTypes.GALGADORIAN_METAL.getItemStack(), ComponentTypes.EYE_OF_GALGADOR.getItemStack() },
			{ Items.QUARTZ, ComponentTypes.ADVANCED_PCB.getItemStack(), Items.QUARTZ } });
		final ModuleData flowerremover = new ModuleData(85, "Lawn Mower", ModuleFlowerRemover.class, 38).addSides(new SIDE[] { SIDE.RIGHT, SIDE.LEFT }).addRecipe(new Object[][] {
			{ ComponentTypes.BLADE_ARM.getItemStack(), null, ComponentTypes.BLADE_ARM.getItemStack() }, { null, ComponentTypes.SIMPLE_PCB.getItemStack(), null },
			{ ComponentTypes.BLADE_ARM.getItemStack(), null, ComponentTypes.BLADE_ARM.getItemStack() } });
		new ModuleData(86, "Milker", ModuleMilker.class, 26).addParent(cage).addRecipe(new Object[][] { { Items.WHEAT, Items.WHEAT, Items.WHEAT },
			{ ComponentTypes.SIMPLE_PCB.getItemStack(), Items.BUCKET, ComponentTypes.SIMPLE_PCB.getItemStack() }, { null, ComponentTypes.SIMPLE_PCB.getItemStack(), null } });
		final ModuleData crafter = new ModuleData(87, "Crafter", ModuleCrafter.class, 22).setAllowDuplicate().addRecipe(new Object[][] { { ComponentTypes.SIMPLE_PCB.getItemStack() },
			{ Blocks.CRAFTING_TABLE } });
		//		new ModuleData(88, "Tree: Exotic", DefaultTreeModule.class, 30).addRequirement(woodcutterGroup).addRecipe(new Object[][] { { Items.GLOWSTONE_DUST, null, Items.GLOWSTONE_DUST },
		//			{ Items.REDSTONE, Blocks.SAPLING, Items.REDSTONE }, { ComponentTypes.SIMPLE_PCB.getItemStack(), ComponentTypes.EMPTY_DISK.getItemStack(), ComponentTypes.SIMPLE_PCB.getItemStack() } });
		new ModuleData(89, "Planter Range Extender", ModulePlantSize.class, 20).addRequirement(woodcutterGroup).addRecipe(new Object[][] {
			{ Items.REDSTONE, ComponentTypes.ADVANCED_PCB.getItemStack(), Items.REDSTONE }, { null, Blocks.SAPLING, null },
			{ ComponentTypes.SIMPLE_PCB.getItemStack(), Blocks.SAPLING, ComponentTypes.SIMPLE_PCB.getItemStack() } });
		new ModuleData(78, "Steve's Arcade", ModuleArcade.class, 10).addParent(seat).addRecipe(new Object[][] { { null, Blocks.GLASS_PANE, null },
			{ planks, ComponentTypes.SIMPLE_PCB.getItemStack(), planks }, { Items.REDSTONE, planks, Items.REDSTONE } });
		final ModuleData smelter = new ModuleData(91, "Smelter", ModuleSmelter.class, 22).setAllowDuplicate().addRecipe(new Object[][] { { ComponentTypes.SIMPLE_PCB.getItemStack() },
			{ Blocks.FURNACE } });
		new ModuleData(92, "Advanced Crafter", ModuleCrafterAdv.class, 42).setAllowDuplicate().addRecipe(new Object[][] { { null, Items.DIAMOND, null },
			{ null, ComponentTypes.ADVANCED_PCB.getItemStack(), null }, { ComponentTypes.SIMPLE_PCB.getItemStack(), crafter.getItemStack(), ComponentTypes.SIMPLE_PCB.getItemStack() } });
		new ModuleData(93, "Advanced Smelter", ModuleSmelterAdv.class, 42).setAllowDuplicate().addRecipe(new Object[][] { { null, Items.DIAMOND, null },
			{ null, ComponentTypes.ADVANCED_PCB.getItemStack(), null }, { ComponentTypes.SIMPLE_PCB.getItemStack(), smelter.getItemStack(), ComponentTypes.SIMPLE_PCB.getItemStack() } });
		new ModuleData(94, "Information Provider", ModuleLabel.class, 12).addRecipe(new Object[][] { { Blocks.GLASS_PANE, Blocks.GLASS_PANE, Blocks.GLASS_PANE },
			{ Items.IRON_INGOT, Items.GLOWSTONE_DUST, Items.IRON_INGOT }, { ComponentTypes.SIMPLE_PCB.getItemStack(), Items.SIGN, ComponentTypes.SIMPLE_PCB.getItemStack() } });
		new ModuleData(95, "Experience Bank", ModuleExperience.class, 36).addRecipe(new Object[][] { { null, Items.REDSTONE, null }, { Items.GLOWSTONE_DUST, Items.EMERALD, Items.GLOWSTONE_DUST },
			{ ComponentTypes.SIMPLE_PCB.getItemStack(), Items.CAULDRON, ComponentTypes.SIMPLE_PCB.getItemStack() } });
		new ModuleData(96, "Creative Incinerator", ModuleCreativeIncinerator.class, 1).addRequirement(drillGroup);
		new ModuleData(97, "Creative Supplies", ModuleCreativeSupplies.class, 1);
		new ModuleData(99, "Cake Server", ModuleCakeServer.class, 10).addSide(SIDE.TOP).addMessage(Localization.MODULE_INFO.ALPHA_MESSAGE).addRecipe(new Object[][] { { null, Items.CAKE, null },
			{ "slabWood", "slabWood", "slabWood" }, { null, ComponentTypes.SIMPLE_PCB.getItemStack(), null } });
		final ModuleData trickOrTreat = new ModuleData(100, "Trick-or-Treat Cake Server", ModuleCakeServerDynamite.class, 15).addSide(SIDE.TOP).addRecipe(new Object[][] { { null, Items.CAKE, null },
			{ "slabWood", "slabWood", "slabWood" }, { ComponentTypes.DYNAMITE.getItemStack(), ComponentTypes.SIMPLE_PCB.getItemStack(), ComponentTypes.DYNAMITE.getItemStack() } });
		if (!Constants.isHalloween) {
			trickOrTreat.lock();
		}
	}

	@SideOnly(Side.CLIENT)
	public static void initModels() {
		ModuleData.moduleList.get((byte) 0).addModel("Engine", new ModelEngineFrame()).addModel("Fire", new ModelEngineInside());
		ModuleData.moduleList.get((byte) 44).addModel("Engine", new ModelEngineFrame()).addModel("Fire", new ModelEngineInside());
		ModuleData.moduleList.get((byte) 1).addModel("SolarPanelBase", new ModelSolarPanelBase()).addModel("SolarPanels", new ModelSolarPanelHeads(4));
		ModuleData.moduleList.get((byte) 45).addModel("SolarPanelBase", new ModelSolarPanelBase()).addModel("SolarPanels", new ModelSolarPanelHeads(2));
		ModuleData.moduleList.get((byte) 56).addModel("SolarPanelSide", new ModelCompactSolarPanel());
		ModuleData.moduleList.get((byte) 2).addModel("SideChest", new ModelSideChests());
		ModuleData.moduleList.get((byte) 3).removeModel("Top").addModel("TopChest", new ModelTopChest());
		ModuleData.moduleList.get((byte) 4).addModel("FrontChest", new ModelFrontChest()).setModelMult(0.68f);
		ModuleData.moduleList.get((byte) 6).addModel("SideChest", new ModelExtractingChests());
		ModuleData.moduleList.get((byte) 7).addModel("Torch", new ModelTorchplacer());
		ModuleData.moduleList.get((byte) 8).addModel("Drill", new ModelDrill(ResourceHelper.getResource("/models/drillModelDiamond.png"))).addModel("Plate", new ModelToolPlate());
		ModuleData.moduleList.get((byte) 42).addModel("Drill", new ModelDrill(ResourceHelper.getResource("/models/drillModelIron.png"))).addModel("Plate", new ModelToolPlate());
		ModuleData.moduleList.get((byte) 43).addModel("Drill", new ModelDrill(ResourceHelper.getResource("/models/drillModelHardened.png"))).addModel("Plate", new ModelToolPlate());
		ModuleData.moduleList.get((byte) 9).addModel("Drill", new ModelDrill(ResourceHelper.getResource("/models/drillModelMagic.png"))).addModel("Plate", new ModelToolPlate());
		ModuleData.moduleList.get((byte) 10).addModel("Rails", new ModelRailer(3));
		ModuleData.moduleList.get((byte) 11).addModel("Rails", new ModelRailer(6));
		ModuleData.moduleList.get((byte) 12).addModel("Bridge", new ModelBridge()).addModel("Plate", new ModelToolPlate());
		ModuleData.moduleList.get((byte) 13).addModel("Remover", new ModelTrackRemover()).setModelMult(0.6f);
		ModuleData.moduleList.get((byte) 14).addModel("Farmer", new ModelFarmer(ResourceHelper.getResource("/models/farmerModelDiamond.png"))).setModelMult(0.45f);
		ModuleData.moduleList.get((byte) 84).addModel("Farmer", new ModelFarmer(ResourceHelper.getResource("/models/farmerModelGalgadorian.png"))).setModelMult(0.45f);
		ModuleData.moduleList.get((byte) 15).addModel("WoodCutter", new ModelWoodCutter(ResourceHelper.getResource("/models/woodCutterModelDiamond.png"))).addModel("Plate", new ModelToolPlate());
		ModuleData.moduleList.get((byte) 79).addModel("WoodCutter", new ModelWoodCutter(ResourceHelper.getResource("/models/woodCutterModelHardened.png"))).addModel("Plate", new ModelToolPlate());
		ModuleData.moduleList.get((byte) 80).addModel("WoodCutter", new ModelWoodCutter(ResourceHelper.getResource("/models/woodCutterModelGalgadorian.png"))).addModel("Plate", new ModelToolPlate());
		ModuleData.moduleList.get((byte) 20).addModel("Sensor", new ModelLiquidSensors());
		ModuleData.moduleList.get((byte) 25).removeModel("Top").addModel("Chair", new ModelSeat());
		ModuleData.moduleList.get((byte) 26).addModel("Lever", new ModelLever(ResourceHelper.getResource("/models/leverModel.png")));
		ModuleData.moduleList.get((byte) 27).addModel("Lever", new ModelLever(ResourceHelper.getResource("/models/leverModel2.png"))).addModel("Wheel", new ModelWheel());
		final ArrayList<Integer> pipes = new ArrayList<>();
		for (int i = 0; i < 9; ++i) {
			if (i != 4) {
				pipes.add(i);
			}
		}
		ModuleData.moduleList.get((byte) 28).addModel("Rig", new ModelShootingRig()).addModel("Pipes", new ModelGun(pipes));
		ModuleData.moduleList.get((byte) 29).addModel("Rig", new ModelShootingRig()).addModel("MobDetector", new ModelMobDetector()).addModel("Pipes", new ModelSniperRifle());
		ModuleData.moduleList.get((byte) 30).addModel("Top", new ModelHullTop(ResourceHelper.getResource("/models/cleanerModelTop.png"), false)).addModel("Cleaner", new ModelCleaner());
		ModuleData.moduleList.get((byte) 31).addModel("Tnt", new ModelDynamite());
		ModuleData.moduleList.get((byte) 32).addModel("Shield", new ModelShield()).setModelMult(0.68f);
		ModuleData.moduleList.get((byte) 37).addModel("Hull", new ModelHull(ResourceHelper.getResource("/models/hullModelWooden.png"))).addModel("Top", new ModelHullTop(ResourceHelper.getResource("/models/hullModelWoodenTop.png")));
		ModuleData.moduleList.get((byte) 38).addModel("Hull", new ModelHull(ResourceHelper.getResource("/models/hullModelStandard.png"))).addModel("Top", new ModelHullTop(ResourceHelper.getResource("/models/hullModelStandardTop.png")));
		ModuleData.moduleList.get((byte) 39).addModel("Hull", new ModelHull(ResourceHelper.getResource("/models/hullModelLarge.png"))).addModel("Top", new ModelHullTop(ResourceHelper.getResource("/models/hullModelLargeTop.png")));
		ModuleData.moduleList.get((byte) 47).addModel("Hull", new ModelPumpkinHull(ResourceHelper.getResource("/models/hullModelPumpkin.png"), ResourceHelper.getResource("/models/hullModelWooden.png"))).addModel("Top", new ModelPumpkinHullTop(ResourceHelper.getResource("/models/hullModelPumpkinTop.png"), ResourceHelper.getResource("/models/hullModelWoodenTop.png")));
		ModuleData.moduleList.get((byte) 62).addModel("Hull", new ModelHull(ResourceHelper.getResource("/models/hullModelPig.png"))).addModel("Top", new ModelHullTop(ResourceHelper.getResource("/models/hullModelPigTop.png"))).addModel("Head", new ModelPigHead()).addModel("Tail", new ModelPigTail()).addModel("Helmet", new ModelPigHelmet(false)).addModel("Helmet_Overlay", new ModelPigHelmet(true));
		ModuleData.moduleList.get((byte) 76).addModel("Hull", new ModelHull(ResourceHelper.getResource("/models/hullModelCreative.png"))).addModel("Top", new ModelHullTop(ResourceHelper.getResource("/models/hullModelCreativeTop.png")));
		ModuleData.moduleList.get((byte) 81).addModel("Hull", new ModelHull(ResourceHelper.getResource("/models/hullModelGalgadorian.png"))).addModel("Top", new ModelHullTop(ResourceHelper.getResource("/models/hullModelGalgadorianTop.png")));
		ModuleData.moduleList.get((byte) 40).setModelMult(0.65f).addModel("Speakers", new ModelNote());
		ModuleData.moduleList.get((byte) 50).addModel("GiftStorage", new ModelGiftStorage());
		ModuleData.moduleList.get((byte) 57).removeModel("Top").addModel("Cage", new ModelCage(false), false).addModel("Cage", new ModelCage(true), true).setModelMult(0.65f);
		ModuleData.moduleList.get((byte) 64).addModel("SideTanks", new ModelSideTanks());
		ModuleData.moduleList.get((byte) 65).addModel("TopTank", new ModelTopTank(false));
		ModuleData.moduleList.get((byte) 66).addModel("LargeTank", new ModelAdvancedTank()).removeModel("Top");
		ModuleData.moduleList.get((byte) 67).setModelMult(0.68f).addModel("FrontTank", new ModelFrontTank());
		ModuleData.moduleList.get((byte) 73).addModel("TopTank", new ModelTopTank(true));
		ModuleData.moduleList.get((byte) 71).addModel("Top", new ModelHullTop(ResourceHelper.getResource("/models/cleanerModelTop.png"), false)).addModel("Cleaner", new ModelLiquidDrainer());
		ModuleData.moduleList.get((byte) 74).addModel("TopChest", new ModelEggBasket());
		ModuleData.moduleList.get((byte) 85).addModel("LawnMower", new ModelLawnMower()).setModelMult(0.4f);
		ModuleData.moduleList.get((byte) 99).addModel("Cake", new ModelCake());
		ModuleData.moduleList.get((byte) 100).addModel("Cake", new ModelCake());
	}

	public ModuleData(final int id, final String name, final Class<? extends ModuleBase> moduleClass, final int modularCost) {
		this.nemesis = null;
		this.requirement = null;
		this.parent = null;
		this.modelMult = 0.75f;
		this.id = (byte) id;
		this.moduleClass = moduleClass;
		this.name = name;
		this.modularCost = modularCost;
		this.groupID = ModuleData.moduleGroups.length;
		for (int i = 0; i < ModuleData.moduleGroups.length; ++i) {
			if (ModuleData.moduleGroups[i].isAssignableFrom(moduleClass)) {
				this.groupID = i;
				break;
			}
		}
		if (ModuleData.moduleList.containsKey(this.id)) {
			System.out.println("WARNING! " + name + " can't be added with ID " + id + " since that ID is already occupied by " + ModuleData.moduleList.get(this.id).getName());
		} else {
			ModuleData.moduleList.put(this.id, this);
		}
	}

	public Class<? extends ModuleBase> getModuleClass() {
		return this.moduleClass;
	}

	public boolean getIsValid() {
		return this.isValid;
	}

	public boolean getIsLocked() {
		return this.isLocked;
	}

	protected ModuleData lock() {
		this.isLocked = true;
		return this;
	}

	public boolean getEnabledByDefault() {
		return !this.defaultLock;
	}

	protected ModuleData lockByDefault() {
		this.defaultLock = true;
		return this;
	}

	protected ModuleData setAllowDuplicate() {
		this.allowDuplicate = true;
		return this;
	}

	protected boolean getAllowDuplicate() {
		return this.allowDuplicate;
	}

	protected ModuleData addSide(final SIDE side) {
		if (this.renderingSides == null) {
			this.renderingSides = new ArrayList<>();
		}
		this.renderingSides.add(side);
		if (side == SIDE.TOP) {
			this.removeModel("Rails");
		}
		return this;
	}

	public ModuleData useExtraData(final byte defaultValue) {
		this.extraDataDefaultValue = defaultValue;
		this.useExtraData = true;
		return this;
	}

	public boolean isUsingExtraData() {
		return this.useExtraData;
	}

	public byte getDefaultExtraData() {
		return this.extraDataDefaultValue;
	}

	public ArrayList<SIDE> getRenderingSides() {
		return this.renderingSides;
	}

	protected ModuleData addSides(final SIDE[] sides) {
		for (int i = 0; i < sides.length; ++i) {
			this.addSide(sides[i]);
		}
		return this;
	}

	protected ModuleData addParent(final ModuleData parent) {
		this.parent = parent;
		return this;
	}

	protected ModuleData addMessage(final Localization.MODULE_INFO s) {
		if (this.message == null) {
			this.message = new ArrayList<>();
		}
		this.message.add(s);
		return this;
	}

	protected void addNemesis(final ModuleData nemesis) {
		if (this.nemesis == null) {
			this.nemesis = new ArrayList<>();
		}
		this.nemesis.add(nemesis);
	}

	protected ModuleData addRequirement(final ModuleDataGroup requirement) {
		if (this.requirement == null) {
			this.requirement = new ArrayList<>();
		}
		this.requirement.add(requirement);
		return this;
	}

	protected static void addNemesis(final ModuleData m1, final ModuleData m2) {
		m2.addNemesis(m1);
		m1.addNemesis(m2);
	}

	public float getModelMult() {
		return this.modelMult;
	}

	protected ModuleData setModelMult(final float val) {
		this.modelMult = val;
		return this;
	}

	protected ModuleData addModel(final String tag, final ModelCartbase model) {
		this.addModel(tag, model, false);
		this.addModel(tag, model, true);
		return this;
	}

	protected ModuleData addModel(final String tag, final ModelCartbase model, final boolean placeholder) {
		if (placeholder) {
			if (this.modelsPlaceholder == null) {
				this.modelsPlaceholder = new HashMap<>();
			}
			this.modelsPlaceholder.put(tag, model);
		} else {
			if (this.models == null) {
				this.models = new HashMap<>();
			}
			this.models.put(tag, model);
		}
		return this;
	}

	public HashMap<String, ModelCartbase> getModels(final boolean placeholder) {
		if (placeholder) {
			return this.modelsPlaceholder;
		}
		return this.models;
	}

	public boolean haveModels(final boolean placeholder) {
		if (placeholder) {
			return this.modelsPlaceholder != null;
		}
		return this.models != null;
	}

	protected ModuleData removeModel(final String tag) {
		if (this.removedModels == null) {
			this.removedModels = new ArrayList<>();
		}
		if (!this.removedModels.contains(tag)) {
			this.removedModels.add(tag);
		}
		return this;
	}

	public ArrayList<String> getRemovedModels() {
		return this.removedModels;
	}

	public boolean haveRemovedModels() {
		return this.removedModels != null;
	}

	public String getName() {
		return I18n.translateToLocal(this.getUnlocalizedName());
	}

	public String getUnlocalizedName() {
		return "item.SC2:" + this.getRawName() + ".name";
	}

	public byte getID() {
		return this.id;
	}

	public int getCost() {
		return this.modularCost;
	}

	protected ModuleData getParent() {
		return this.parent;
	}

	protected ArrayList<ModuleData> getNemesis() {
		return this.nemesis;
	}

	protected ArrayList<ModuleDataGroup> getRequirement() {
		return this.requirement;
	}

	public boolean getHasRecipe() {
		return this.hasRecipe;
	}

	public String getModuleInfoText(final byte b) {
		return null;
	}

	public String getCartInfoText(final String name, final byte b) {
		return name;
	}

	public static ArrayList<ItemStack> getModularItems(
		@Nonnull
			ItemStack cart) {
		final ArrayList<ItemStack> modules = new ArrayList<>();
		if (cart != null && cart.getItem() == ModItems.carts && cart.getTagCompound() != null) {
			final NBTTagCompound info = cart.getTagCompound();
			if (info.hasKey("Modules")) {
				final byte[] IDs = info.getByteArray("Modules");
				for (int i = 0; i < IDs.length; ++i) {
					final byte id = IDs[i];
					@Nonnull
					ItemStack module = new ItemStack(ModItems.modules, 1, id);
					ModItems.modules.addExtraDataToModule(module, info, i);
					modules.add(module);
				}
			}
		}
		return modules;
	}

	public static ItemStack createModularCart(final EntityMinecartModular parentcart) {
		@Nonnull
		ItemStack cart = new ItemStack(ModItems.carts, 1);
		final NBTTagCompound save = new NBTTagCompound();
		final byte[] moduleIDs = new byte[parentcart.getModules().size()];
		for (int i = 0; i < parentcart.getModules().size(); ++i) {
			final ModuleBase module = parentcart.getModules().get(i);
			for (final ModuleData moduledata : ModuleData.moduleList.values()) {
				if (module.getClass() == moduledata.moduleClass) {
					moduleIDs[i] = moduledata.getID();
					break;
				}
			}
			ModItems.modules.addExtraDataToModule(save, module, i);
		}
		save.setByteArray("Modules", moduleIDs);
		cart.setTagCompound(save);
		CartVersion.addVersion(cart);
		return cart;
	}

	public static ItemStack createModularCartFromItems(final ArrayList<ItemStack> modules) {
		@Nonnull
		ItemStack cart = new ItemStack(ModItems.carts, 1);
		final NBTTagCompound save = new NBTTagCompound();
		final byte[] moduleIDs = new byte[modules.size()];
		for (int i = 0; i < moduleIDs.length; ++i) {
			moduleIDs[i] = (byte) modules.get(i).getItemDamage();
			ModItems.modules.addExtraDataToCart(save, modules.get(i), i);
		}
		save.setByteArray("Modules", moduleIDs);
		cart.setTagCompound(save);
		CartVersion.addVersion(cart);
		return cart;
	}

	public static boolean isItemOfModularType(
		@Nonnull
			ItemStack itemstack, final Class<? extends ModuleBase> validClass) {
		if (itemstack.getItem() == ModItems.modules) {
			final ModuleData module = ModItems.modules.getModuleData(itemstack);
			if (module != null && validClass.isAssignableFrom(module.moduleClass)) {
				return true;
			}
		}
		return false;
	}

	protected ModuleData addRecipe(final Object[][] recipe) {
		if (this.recipes == null) {
			this.recipes = new ArrayList<>();
		}
		this.recipes.add(recipe);
		return this;
	}

	public void loadRecipe() {
		if (!this.isLocked) {
			this.isValid = true;
			if (this.recipes != null) {
				this.hasRecipe = true;
				for (final Object[][] recipe : this.recipes) {
					RecipeHelper.addRecipe(this.getItemStack(), recipe);
				}
			}
		}
	}

	@Nonnull
	public ItemStack getItemStack() {
		@Nonnull
		ItemStack module = new ItemStack(ModItems.modules, 1, this.id);
		if (this.isUsingExtraData()) {
			final NBTTagCompound save = new NBTTagCompound();
			save.setByte("Data", this.getDefaultExtraData());
			module.setTagCompound(save);
		}
		return module;
	}

	public static boolean isValidModuleItem(final int validGroup,
	                                        @Nonnull
		                                        ItemStack itemstack) {
		if (itemstack.getItem() == ModItems.modules) {
			final ModuleData module = ModItems.modules.getModuleData(itemstack);
			return isValidModuleItem(validGroup, module);
		}
		return false;
	}

	public static boolean isValidModuleItem(final int validGroup, final ModuleData module) {
		if (module != null) {
			if (validGroup < 0) {
				for (int i = 0; i < ModuleData.moduleGroups.length; ++i) {
					if (ModuleData.moduleGroups[i].isAssignableFrom(module.moduleClass)) {
						return false;
					}
				}
				return true;
			}
			if (ModuleData.moduleGroups[validGroup].isAssignableFrom(module.moduleClass)) {
				return true;
			}
		}
		return false;
	}

	public static boolean isValidModuleCombo(final ModuleDataHull hull, final ArrayList<ModuleData> modules) {
		final int[] max = { 1, hull.getEngineMax(), 1, 4, hull.getAddonMax(), 6 };
		final int[] current = new int[max.length];
		for (final ModuleData module : modules) {
			int id = 5;
			for (int i = 0; i < 5; ++i) {
				if (isValidModuleItem(i, module)) {
					id = i;
					break;
				}
			}
			final int[] array = current;
			final int n = id;
			++array[n];
			if (current[id] > max[id]) {
				return false;
			}
		}
		return true;
	}

	public void addExtraMessage(final List list) {
		if (this.message != null) {
			list.add("");
			for (final Localization.MODULE_INFO m : this.message) {
				final String str = m.translate();
				if (str.length() <= 30) {
					this.addExtraMessage(list, str);
				} else {
					final String[] words = str.split(" ");
					String row = "";
					for (final String word : words) {
						final String next = (row + " " + word).trim();
						if (next.length() <= 30) {
							row = next;
						} else {
							this.addExtraMessage(list, row);
							row = word;
						}
					}
					this.addExtraMessage(list, row);
				}
			}
		}
	}

	private void addExtraMessage(final List list, final String str) {
		list.add(TextFormatting.DARK_GRAY + (TextFormatting.ITALIC + str + TextFormatting.RESET));
	}

	public void addSpecificInformation(final List list) {
		list.add(TextFormatting.GRAY + Localization.MODULE_INFO.MODULAR_COST.translate() + ": " + this.modularCost);
	}

	public final void addInformation(final List list, final NBTTagCompound compound) {
		this.addSpecificInformation(list);
		if (compound != null && compound.hasKey("Data")) {
			final String extradatainfo = this.getModuleInfoText(compound.getByte("Data"));
			if (extradatainfo != null) {
				list.add(TextFormatting.WHITE + extradatainfo);
			}
		}
		if (GuiScreen.isShiftKeyDown()) {
			if (this.getRenderingSides() == null || this.getRenderingSides().size() == 0) {
				list.add(TextFormatting.DARK_AQUA + Localization.MODULE_INFO.NO_SIDES.translate());
			} else {
				String sides = "";
				for (int i = 0; i < this.getRenderingSides().size(); ++i) {
					final SIDE side = this.getRenderingSides().get(i);
					if (i == 0) {
						sides += side.toString();
					} else if (i == this.getRenderingSides().size() - 1) {
						sides = sides + " " + Localization.MODULE_INFO.AND.translate() + " " + side.toString();
					} else {
						sides = sides + ", " + side.toString();
					}
				}
				list.add(TextFormatting.DARK_AQUA + Localization.MODULE_INFO.OCCUPIED_SIDES.translate(sides, String.valueOf(this.getRenderingSides().size())));
			}
			if (this.getNemesis() != null && this.getNemesis().size() != 0) {
				if (this.getRenderingSides() == null || this.getRenderingSides().size() == 0) {
					list.add(TextFormatting.RED + Localization.MODULE_INFO.CONFLICT_HOWEVER.translate() + ":");
				} else {
					list.add(TextFormatting.RED + Localization.MODULE_INFO.CONFLICT_ALSO.translate() + ":");
				}
				for (final ModuleData module : this.getNemesis()) {
					list.add(TextFormatting.RED + module.getName());
				}
			}
			if (this.parent != null) {
				list.add(TextFormatting.YELLOW + Localization.MODULE_INFO.REQUIREMENT.translate() + " " + this.parent.getName());
			}
			if (this.getRequirement() != null && this.getRequirement().size() != 0) {
				for (final ModuleDataGroup group : this.getRequirement()) {
					list.add(TextFormatting.YELLOW + Localization.MODULE_INFO.REQUIREMENT.translate() + " " + group.getCountName() + " " + group.getName());
				}
			}
			if (this.getAllowDuplicate()) {
				list.add(TextFormatting.GREEN + Localization.MODULE_INFO.DUPLICATES.translate());
			}
		}
		list.add(TextFormatting.BLUE + Localization.MODULE_INFO.TYPE.translate() + ": " + ModuleData.moduleGroupNames[this.groupID].translate());
		this.addExtraMessage(list);
	}

	public static String checkForErrors(final ModuleDataHull hull, final ArrayList<ModuleData> modules) {
		if (getTotalCost(modules) > hull.getCapacity()) {
			return Localization.MODULE_INFO.CAPACITY_ERROR.translate();
		}
		if (!isValidModuleCombo(hull, modules)) {
			return Localization.MODULE_INFO.COMBINATION_ERROR.translate();
		}
		for (int i = 0; i < modules.size(); ++i) {
			final ModuleData mod1 = modules.get(i);
			if (mod1.getCost() > hull.getComplexityMax()) {
				return Localization.MODULE_INFO.COMPLEXITY_ERROR.translate(mod1.getName());
			}
			if (mod1.getParent() != null && !modules.contains(mod1.getParent())) {
				return Localization.MODULE_INFO.PARENT_ERROR.translate(mod1.getName(), mod1.getParent().getName());
			}
			if (mod1.getNemesis() != null) {
				for (final ModuleData nemesis : mod1.getNemesis()) {
					if (modules.contains(nemesis)) {
						return Localization.MODULE_INFO.NEMESIS_ERROR.translate(mod1.getName(), nemesis.getName());
					}
				}
			}
			if (mod1.getRequirement() != null) {
				for (final ModuleDataGroup group : mod1.getRequirement()) {
					int count = 0;
					for (final ModuleData mod2 : group.getModules()) {
						for (final ModuleData mod3 : modules) {
							if (mod2.equals(mod3)) {
								++count;
							}
						}
					}
					if (count < group.getCount()) {
						return Localization.MODULE_INFO.PARENT_ERROR.translate(mod1.getName(), group.getCountName() + " " + group.getName());
					}
				}
			}
			for (int j = i + 1; j < modules.size(); ++j) {
				final ModuleData mod4 = modules.get(j);
				if (mod1 == mod4) {
					if (!mod1.getAllowDuplicate()) {
						return Localization.MODULE_INFO.DUPLICATE_ERROR.translate(mod1.getName());
					}
				} else if (mod1.getRenderingSides() != null && mod4.getRenderingSides() != null) {
					SIDE clash = SIDE.NONE;
					for (final SIDE side1 : mod1.getRenderingSides()) {
						for (final SIDE side2 : mod4.getRenderingSides()) {
							if (side1 == side2) {
								clash = side1;
								break;
							}
						}
						if (clash != SIDE.NONE) {
							break;
						}
					}
					if (clash != SIDE.NONE) {
						return Localization.MODULE_INFO.CLASH_ERROR.translate(mod1.getName(), mod4.getName(), clash.toString());
					}
				}
			}
		}
		return null;
	}

	public static int getTotalCost(final ArrayList<ModuleData> modules) {
		int currentCost = 0;
		for (final ModuleData module : modules) {
			currentCost += module.getCost();
		}
		return currentCost;
	}

	private static long calculateCombinations() {
		long combinations = 0L;
		final ArrayList<ModuleData> potential = new ArrayList<>();
		for (final ModuleData module : ModuleData.moduleList.values()) {
			if (!(module instanceof ModuleDataHull)) {
				potential.add(module);
			}
		}
		for (final ModuleData module : ModuleData.moduleList.values()) {
			if (module instanceof ModuleDataHull) {
				final ArrayList<ModuleData> modules = new ArrayList<>();
				combinations += populateHull((ModuleDataHull) module, modules, (ArrayList<ModuleData>) potential.clone(), 0);
				System.out.println("Hull added: " + combinations);
			}
		}
		return combinations;
	}

	private static long populateHull(final ModuleDataHull hull, final ArrayList<ModuleData> attached, final ArrayList<ModuleData> potential, final int depth) {
		if (checkForErrors(hull, attached) != null) {
			return 0L;
		}
		long combinations = 1L;
		final Iterator itt = potential.iterator();
		while (itt.hasNext()) {
			final ModuleData module = (ModuleData) itt.next();
			final ArrayList<ModuleData> attachedCopy = (ArrayList<ModuleData>) attached.clone();
			attachedCopy.add(module);
			final ArrayList<ModuleData> potentialCopy = (ArrayList<ModuleData>) potential.clone();
			itt.remove();
			combinations += populateHull(hull, attachedCopy, potentialCopy, depth + 1);
			if (depth < 3) {
				System.out.println("Modular state[" + depth + "]: " + combinations);
			}
		}
		return combinations;
	}

	public String getRawName() {
		return this.name.replace(":", "").replace("'", "").replace(" ", "_").replace("-", "_").toLowerCase();
	}

	//	@SideOnly(Side.CLIENT)
	//	public void createIcon(final IIconRegister register) {
	//		final StringBuilder sb = new StringBuilder();
	//		StevesCarts.instance.getClass();
	//		this.icon = register.registerIcon(sb.append("stevescarts").append(":").append(this.getRawName()).append("_icon").toString());
	//	}

	@SideOnly(Side.CLIENT)
	public String getIcon() {
		return this.icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public enum SIDE {
		NONE(Localization.MODULE_INFO.SIDE_NONE),
		TOP(Localization.MODULE_INFO.SIDE_TOP),
		CENTER(Localization.MODULE_INFO.SIDE_CENTER),
		BOTTOM(Localization.MODULE_INFO.SIDE_BOTTOM),
		BACK(Localization.MODULE_INFO.SIDE_BACK),
		LEFT(Localization.MODULE_INFO.SIDE_LEFT),
		RIGHT(Localization.MODULE_INFO.SIDE_RIGHT),
		FRONT(Localization.MODULE_INFO.SIDE_FRONT);

		private Localization.MODULE_INFO name;

		SIDE(final Localization.MODULE_INFO name) {
			this.name = name;
		}

		@Override
		public String toString() {
			return this.name.translate();
		}
	}
}
