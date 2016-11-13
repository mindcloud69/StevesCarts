package vswe.stevesvehicles.module.data.registry.cart;

import static vswe.stevesvehicles.item.ComponentTypes.ADVANCED_PCB;
import static vswe.stevesvehicles.item.ComponentTypes.ENHANCED_GALGADORIAN_METAL;
import static vswe.stevesvehicles.item.ComponentTypes.GALGADORIAN_METAL;
import static vswe.stevesvehicles.item.ComponentTypes.GALGADORIAN_SAW_BLADE;
import static vswe.stevesvehicles.item.ComponentTypes.HARDENED_MESH;
import static vswe.stevesvehicles.item.ComponentTypes.HARDENED_SAW_BLADE;
import static vswe.stevesvehicles.item.ComponentTypes.REINFORCED_METAL;
import static vswe.stevesvehicles.item.ComponentTypes.SAW_BLADE;
import static vswe.stevesvehicles.item.ComponentTypes.SIMPLE_PCB;
import static vswe.stevesvehicles.item.ComponentTypes.WOOD_CUTTING_CORE;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vswe.stevesvehicles.client.ResourceHelper;
import vswe.stevesvehicles.client.rendering.models.cart.ModelDrill;
import vswe.stevesvehicles.client.rendering.models.cart.ModelFarmer;
import vswe.stevesvehicles.client.rendering.models.cart.ModelToolPlate;
import vswe.stevesvehicles.client.rendering.models.cart.ModelWoodCutter;
import vswe.stevesvehicles.localization.entry.info.LocalizationGroup;
import vswe.stevesvehicles.module.cart.tool.ModuleDrillDiamond;
import vswe.stevesvehicles.module.cart.tool.ModuleDrillGalgadorian;
import vswe.stevesvehicles.module.cart.tool.ModuleDrillHardened;
import vswe.stevesvehicles.module.cart.tool.ModuleDrillIron;
import vswe.stevesvehicles.module.cart.tool.ModuleFarmerDiamond;
import vswe.stevesvehicles.module.cart.tool.ModuleFarmerGalgadorian;
import vswe.stevesvehicles.module.cart.tool.ModuleWoodcutterDiamond;
import vswe.stevesvehicles.module.cart.tool.ModuleWoodcutterGalgadorian;
import vswe.stevesvehicles.module.cart.tool.ModuleWoodcutterHardened;
import vswe.stevesvehicles.module.data.ModuleData;
import vswe.stevesvehicles.module.data.ModuleDataGroup;
import vswe.stevesvehicles.module.data.ModuleSide;
import vswe.stevesvehicles.module.data.registry.ModuleRegistry;
import vswe.stevesvehicles.vehicle.VehicleRegistry;

public class ModuleRegistryCartTools extends ModuleRegistry {
	public static final String TOOL_KEY = "Tools";
	public static final String DRILL_KEY = "Drills";
	public static final String WOOD_KEY = "WoodCutters";
	public static final String FARM_KEY = "Farmers";

	public ModuleRegistryCartTools() {
		super("cart.tools");
		// Create a combined group for all the tool groups. The order groups are
		// being accessed is irrelevant.
		// The combined group is created before the individual groups to prove
		// this.
		// (The order independence is more important when multiple
		// registries/mods are involved)
		ModuleDataGroup.getCombinedGroup(TOOL_KEY, LocalizationGroup.TOOL, ModuleDataGroup.getGroup(DRILL_KEY), ModuleDataGroup.getGroup(WOOD_KEY), ModuleDataGroup.getGroup(FARM_KEY));
		loadDrills();
		loadWoodCutters();
		loadFarmers();
	}

	private void loadDrills() {
		ModuleDataGroup drills = ModuleDataGroup.createGroup(DRILL_KEY, LocalizationGroup.DRILL);
		ModuleData iron = new ModuleData("iron_drill", ModuleDrillIron.class, 3) {
			@Override
			@SideOnly(Side.CLIENT)
			public void loadModels() {
				addModel("Drill", new ModelDrill(ResourceHelper.getResource("/models/drillModelIron.png")));
				addModel("Plate", new ModelToolPlate());
			}
		};
		iron.addShapedRecipe(Items.IRON_INGOT, Items.IRON_INGOT, null, null, Items.IRON_INGOT, Items.IRON_INGOT, Items.IRON_INGOT, Items.IRON_INGOT, null);
		iron.addSides(ModuleSide.FRONT);
		iron.addVehicles(VehicleRegistry.CART);
		drills.add(iron);
		register(iron);
		ModuleData basic = new ModuleData("basic_drill", ModuleDrillDiamond.class, 10) {
			@Override
			@SideOnly(Side.CLIENT)
			public void loadModels() {
				addModel("Drill", new ModelDrill(ResourceHelper.getResource("/models/drillModelDiamond.png")));
				addModel("Plate", new ModelToolPlate());
			}
		};
		basic.addShapedRecipe(Items.IRON_INGOT, Items.DIAMOND, null, null, Items.IRON_INGOT, Items.DIAMOND, Items.IRON_INGOT, Items.DIAMOND, null);
		basic.addSides(ModuleSide.FRONT);
		basic.addVehicles(VehicleRegistry.CART);
		drills.add(basic);
		register(basic);
		ModuleData hardened = new ModuleData("hardened_drill", ModuleDrillHardened.class, 45) {
			@Override
			@SideOnly(Side.CLIENT)
			public void loadModels() {
				addModel("Drill", new ModelDrill(ResourceHelper.getResource("/models/drillModelHardened.png")));
				addModel("Plate", new ModelToolPlate());
			}
		};
		hardened.addShapedRecipe(HARDENED_MESH, REINFORCED_METAL, null, Blocks.DIAMOND_BLOCK, basic, REINFORCED_METAL, HARDENED_MESH, REINFORCED_METAL, null);
		hardened.addSides(ModuleSide.FRONT);
		hardened.addVehicles(VehicleRegistry.CART);
		drills.add(hardened);
		register(hardened);
		ModuleData galgadorian = new ModuleData("galgadorian_drill", ModuleDrillGalgadorian.class, 150) {
			@Override
			@SideOnly(Side.CLIENT)
			public void loadModels() {
				addModel("Drill", new ModelDrill(ResourceHelper.getResource("/models/drillModelMagic.png")));
				addModel("Plate", new ModelToolPlate());
			}
		};
		galgadorian.addShapedRecipe(GALGADORIAN_METAL, ENHANCED_GALGADORIAN_METAL, null, Blocks.DIAMOND_BLOCK, hardened, ENHANCED_GALGADORIAN_METAL, GALGADORIAN_METAL, ENHANCED_GALGADORIAN_METAL, null);
		galgadorian.addSides(ModuleSide.FRONT);
		galgadorian.addVehicles(VehicleRegistry.CART);
		drills.add(galgadorian);
		register(galgadorian);
	}

	private void loadWoodCutters() {
		ModuleDataGroup cutters = ModuleDataGroup.createGroup(WOOD_KEY, LocalizationGroup.CUTTER);
		ModuleData basic = new ModuleData("basic_wood_cutter", ModuleWoodcutterDiamond.class, 34) {
			@Override
			@SideOnly(Side.CLIENT)
			public void loadModels() {
				addModel("WoodCutter", new ModelWoodCutter(ResourceHelper.getResource("/models/woodCutterModelDiamond.png")));
				addModel("Plate", new ModelToolPlate());
			}
		};
		basic.addShapedRecipe(SAW_BLADE, SAW_BLADE, SAW_BLADE, SAW_BLADE, Items.IRON_INGOT, SAW_BLADE, null, WOOD_CUTTING_CORE, null);
		basic.addSides(ModuleSide.FRONT);
		basic.addVehicles(VehicleRegistry.CART);
		cutters.add(basic);
		register(basic);
		ModuleData hardened = new ModuleData("hardened_wood_cutter", ModuleWoodcutterHardened.class, 65) {
			@Override
			@SideOnly(Side.CLIENT)
			public void loadModels() {
				addModel("WoodCutter", new ModelWoodCutter(ResourceHelper.getResource("/models/woodCutterModelHardened.png")));
				addModel("Plate", new ModelToolPlate());
			}
		};
		hardened.addShapedRecipe(HARDENED_SAW_BLADE, HARDENED_SAW_BLADE, HARDENED_SAW_BLADE, HARDENED_SAW_BLADE, Items.DIAMOND, HARDENED_SAW_BLADE, null, WOOD_CUTTING_CORE, null);
		hardened.addShapedRecipe(REINFORCED_METAL, REINFORCED_METAL, REINFORCED_METAL, REINFORCED_METAL, Items.IRON_INGOT, REINFORCED_METAL, null, basic, null);
		hardened.addSides(ModuleSide.FRONT);
		hardened.addVehicles(VehicleRegistry.CART);
		cutters.add(hardened);
		register(hardened);
		ModuleData galgadorian = new ModuleData("galgadorian_wood_cutter", ModuleWoodcutterGalgadorian.class, 120) {
			@Override
			@SideOnly(Side.CLIENT)
			public void loadModels() {
				addModel("WoodCutter", new ModelWoodCutter(ResourceHelper.getResource("/models/woodCutterModelGalgadorian.png")));
				addModel("Plate", new ModelToolPlate());
			}
		};
		galgadorian.addShapedRecipe(GALGADORIAN_SAW_BLADE, GALGADORIAN_SAW_BLADE, GALGADORIAN_SAW_BLADE, GALGADORIAN_SAW_BLADE, REINFORCED_METAL, GALGADORIAN_SAW_BLADE, null, WOOD_CUTTING_CORE, null);
		galgadorian.addShapedRecipe(GALGADORIAN_METAL, GALGADORIAN_METAL, GALGADORIAN_METAL, GALGADORIAN_METAL, Items.IRON_INGOT, GALGADORIAN_METAL, null, hardened, null);
		galgadorian.addSides(ModuleSide.FRONT);
		galgadorian.addVehicles(VehicleRegistry.CART);
		cutters.add(galgadorian);
		register(galgadorian);
	}

	private void loadFarmers() {
		ModuleDataGroup farmers = ModuleDataGroup.createGroup(FARM_KEY, LocalizationGroup.FARMER);
		ModuleData basic = new ModuleData("basic_farmer", ModuleFarmerDiamond.class, 36) {
			@Override
			@SideOnly(Side.CLIENT)
			public void loadModels() {
				addModel("Farmer", new ModelFarmer(ResourceHelper.getResource("/models/farmerModelDiamond.png")));
				setModelMultiplier(0.45F);
			}
		};
		basic.addShapedRecipe(Items.DIAMOND, Items.DIAMOND, Items.DIAMOND, null, Items.IRON_INGOT, null, SIMPLE_PCB, Items.GOLD_INGOT, SIMPLE_PCB);
		basic.addSides(ModuleSide.FRONT);
		basic.addVehicles(VehicleRegistry.CART);
		farmers.add(basic);
		register(basic);
		ModuleData galgadorian = new ModuleData("galgadorian_farmer", ModuleFarmerGalgadorian.class, 55) {
			@Override
			@SideOnly(Side.CLIENT)
			public void loadModels() {
				addModel("Farmer", new ModelFarmer(ResourceHelper.getResource("/models/farmerModelGalgadorian.png")));
				setModelMultiplier(0.45F);
			}
		};
		galgadorian.addShapedRecipe(GALGADORIAN_METAL, GALGADORIAN_METAL, GALGADORIAN_METAL, null, REINFORCED_METAL, null, ADVANCED_PCB, Items.GOLD_INGOT, ADVANCED_PCB);
		galgadorian.addShapedRecipe(GALGADORIAN_METAL, GALGADORIAN_METAL, GALGADORIAN_METAL, null, basic, null, null, SIMPLE_PCB, null);
		galgadorian.addSides(ModuleSide.FRONT);
		galgadorian.addVehicles(VehicleRegistry.CART);
		farmers.add(galgadorian);
		register(galgadorian);
	}
}
