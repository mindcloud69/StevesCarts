package vswe.stevesvehicles.module.data.registry;

import static vswe.stevesvehicles.item.ComponentTypes.ADVANCED_PCB;
import static vswe.stevesvehicles.item.ComponentTypes.ADVANCED_SOLAR_PANEL;
import static vswe.stevesvehicles.item.ComponentTypes.REINFORCED_METAL;
import static vswe.stevesvehicles.item.ComponentTypes.SIMPLE_PCB;
import static vswe.stevesvehicles.item.ComponentTypes.SOLAR_PANEL;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vswe.stevesvehicles.client.rendering.models.common.ModelCompactSolarPanel;
import vswe.stevesvehicles.client.rendering.models.common.ModelEngineFrame;
import vswe.stevesvehicles.client.rendering.models.common.ModelEngineInside;
import vswe.stevesvehicles.client.rendering.models.common.ModelSolarPanelBase;
import vswe.stevesvehicles.client.rendering.models.common.ModelSolarPanelHeads;
import vswe.stevesvehicles.localization.entry.info.LocalizationGroup;
import vswe.stevesvehicles.module.common.engine.ModuleCheatEngine;
import vswe.stevesvehicles.module.common.engine.ModuleCoalStandard;
import vswe.stevesvehicles.module.common.engine.ModuleCoalTiny;
import vswe.stevesvehicles.module.common.engine.ModuleSolarBasic;
import vswe.stevesvehicles.module.common.engine.ModuleSolarCompact;
import vswe.stevesvehicles.module.common.engine.ModuleSolarStandard;
import vswe.stevesvehicles.module.common.engine.ModuleThermalAdvanced;
import vswe.stevesvehicles.module.common.engine.ModuleThermalStandard;
import vswe.stevesvehicles.module.data.ModuleData;
import vswe.stevesvehicles.module.data.ModuleDataGroup;
import vswe.stevesvehicles.module.data.ModuleSide;
import vswe.stevesvehicles.vehicle.VehicleRegistry;


public class ModuleRegistryEngines extends ModuleRegistry {
	public ModuleRegistryEngines() {
		super("common.engines");

		ModuleData coalSmall = new ModuleData("tiny_coal_engine", ModuleCoalTiny.class, 2) {
			@Override
			@SideOnly(Side.CLIENT)
			public void loadModels() {
				addModel("Engine", new ModelEngineFrame());
				addModel("Fire", new ModelEngineInside());
			}
		};

		coalSmall.addShapedRecipeWithSize(3, 2,
				Items.IRON_INGOT,   Blocks.FURNACE,     Items.IRON_INGOT,
				null,               Blocks.PISTON,      null);

		coalSmall.addVehicles(VehicleRegistry.CART, VehicleRegistry.BOAT);
		register(coalSmall);

		ModuleData solarSmall = new ModuleData("basic_solar_engine", ModuleSolarBasic.class, 12) {
			@Override
			@SideOnly(Side.CLIENT)
			public void loadModels() {
				addModel("SolarPanelBase",new ModelSolarPanelBase());
				addModel("SolarPanels", new ModelSolarPanelHeads(2));
				removeModel("Top");
			}
		};

		solarSmall.addShapedRecipe( SOLAR_PANEL,        Items.IRON_INGOT,       SOLAR_PANEL,
				Items.IRON_INGOT,   SIMPLE_PCB,             Items.IRON_INGOT,
				null,               Blocks.PISTON,          null);


		solarSmall.addSides(ModuleSide.CENTER, ModuleSide.TOP);
		solarSmall.addVehicles(VehicleRegistry.CART, VehicleRegistry.BOAT);
		register(solarSmall);

		ModuleData thermalSmall = new ModuleData("thermal_engine", ModuleThermalStandard.class, 28);
		thermalSmall.addShapedRecipe(   Blocks.NETHER_BRICK,        Blocks.NETHER_BRICK,       Blocks.NETHER_BRICK,
				Blocks.OBSIDIAN,   Blocks.FURNACE,             Blocks.OBSIDIAN,
				Blocks.PISTON,               null,          Blocks.PISTON);


		thermalSmall.addVehicles(VehicleRegistry.CART, VehicleRegistry.BOAT);
		thermalSmall.addRequirement(ModuleDataGroup.getGroup(ModuleRegistryTanks.TANK_KEY));
		register(thermalSmall);

		ModuleData coalLarge = new ModuleData("coal_engine", ModuleCoalStandard.class, 15) {
			@Override
			@SideOnly(Side.CLIENT)
			public void loadModels() {
				addModel("Engine", new ModelEngineFrame());
				addModel("Fire", new ModelEngineInside());
			}
		};

		coalLarge.addShapedRecipe(  Items.IRON_INGOT,       Items.IRON_INGOT,       Items.IRON_INGOT,
				Items.IRON_INGOT,       Blocks.FURNACE,         Items.IRON_INGOT,
				Blocks.PISTON,          null,                   Blocks.PISTON);

		coalLarge.addVehicles(VehicleRegistry.CART, VehicleRegistry.BOAT);
		register(coalLarge);
		ModuleData.addNemesis(coalLarge, coalSmall);


		ModuleData solarLarge = new ModuleData("solar_engine", ModuleSolarStandard.class, 20) {
			@Override
			@SideOnly(Side.CLIENT)
			public void loadModels() {
				addModel("SolarPanelBase",new ModelSolarPanelBase());
				addModel("SolarPanels", new ModelSolarPanelHeads(4));
				removeModel("Top");
			}
		};

		solarLarge.addShapedRecipe( Items.IRON_INGOT,   SOLAR_PANEL,    Items.IRON_INGOT,
				SOLAR_PANEL,        ADVANCED_PCB,   SOLAR_PANEL,
				Blocks.PISTON,      SOLAR_PANEL,    Blocks.PISTON);


		solarLarge.addSides(ModuleSide.CENTER, ModuleSide.TOP);
		solarLarge.addVehicles(VehicleRegistry.CART, VehicleRegistry.BOAT);
		register(solarLarge);
		ModuleData.addNemesis(solarLarge, solarSmall);
		engines.add(solarLarge);


		ModuleData thermalLarge = new ModuleData("advanced_thermal_engine", ModuleThermalAdvanced.class, 58);
		thermalLarge.addShapedRecipe(   Blocks.NETHER_BRICK,    Blocks.NETHER_BRICK,    Blocks.NETHER_BRICK,
				REINFORCED_METAL,       thermalSmall,           REINFORCED_METAL,
				Blocks.PISTON,          null,                   Blocks.PISTON);


		thermalLarge.addVehicles(VehicleRegistry.CART, VehicleRegistry.BOAT);
		thermalLarge.addRequirement(ModuleDataGroup.getGroup(ModuleRegistryTanks.TANK_KEY).copyWithName(ModuleRegistryTanks.TANK_KEY + 2, 2));
		register(thermalLarge);
		ModuleData.addNemesis(thermalLarge, thermalSmall);

		ModuleData solarAdvanced = new ModuleData("compact_solar_engine", ModuleSolarCompact.class, 32) {
			@Override
			@SideOnly(Side.CLIENT)
			public void loadModels() {
				addModel("SolarPanelSide", new ModelCompactSolarPanel());
			}
		};

		solarAdvanced.addShapedRecipe(  ADVANCED_SOLAR_PANEL,       Items.IRON_INGOT,       ADVANCED_SOLAR_PANEL,
				ADVANCED_PCB,               Items.REDSTONE,         ADVANCED_PCB,
				Blocks.PISTON,              Items.IRON_INGOT,       Blocks.PISTON);


		solarAdvanced.addSides(ModuleSide.RIGHT, ModuleSide.LEFT);
		solarAdvanced.addVehicles(VehicleRegistry.CART, VehicleRegistry.BOAT);
		register(solarAdvanced);

		ModuleData cheat = new ModuleData("creative_engine", ModuleCheatEngine.class, 1);
		cheat.addVehicles(VehicleRegistry.CART, VehicleRegistry.BOAT);
		register(cheat);
	}

	public static final String ENGINE_KEY = "Engines";
	private ModuleDataGroup engines = ModuleDataGroup.createGroup(ENGINE_KEY, LocalizationGroup.ENGINE);
	@Override
	public void register(ModuleData moduleData) {
		super.register(moduleData);
		engines.add(moduleData);
	}
}
