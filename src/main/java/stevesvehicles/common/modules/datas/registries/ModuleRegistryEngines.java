package stevesvehicles.common.modules.datas.registries;

import static stevesvehicles.common.items.ComponentTypes.ADVANCED_PCB;
import static stevesvehicles.common.items.ComponentTypes.ADVANCED_SOLAR_PANEL;
import static stevesvehicles.common.items.ComponentTypes.REINFORCED_METAL;
import static stevesvehicles.common.items.ComponentTypes.SIMPLE_PCB;
import static stevesvehicles.common.items.ComponentTypes.SOLAR_PANEL;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import stevesvehicles.client.localization.entry.info.LocalizationGroup;
import stevesvehicles.client.rendering.models.common.ModelCompactSolarPanel;
import stevesvehicles.client.rendering.models.common.ModelEngineFrame;
import stevesvehicles.client.rendering.models.common.ModelEngineInside;
import stevesvehicles.client.rendering.models.common.ModelSolarPanelBase;
import stevesvehicles.client.rendering.models.common.ModelSolarPanelHeads;
import stevesvehicles.common.modules.common.engine.ModuleCheatEngine;
import stevesvehicles.common.modules.common.engine.ModuleCoalStandard;
import stevesvehicles.common.modules.common.engine.ModuleCoalTiny;
import stevesvehicles.common.modules.common.engine.ModuleSolarBasic;
import stevesvehicles.common.modules.common.engine.ModuleSolarCompact;
import stevesvehicles.common.modules.common.engine.ModuleSolarStandard;
import stevesvehicles.common.modules.common.engine.ModuleThermalAdvanced;
import stevesvehicles.common.modules.common.engine.ModuleThermalStandard;
import stevesvehicles.common.modules.datas.ModuleData;
import stevesvehicles.common.modules.datas.ModuleDataGroup;
import stevesvehicles.common.modules.datas.ModuleSide;
import stevesvehicles.common.vehicles.VehicleRegistry;

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
		coalSmall.addShapedRecipeWithSize(3, 2, Items.IRON_INGOT, Blocks.FURNACE, Items.IRON_INGOT, null, Blocks.PISTON, null);
		coalSmall.addVehicles(VehicleRegistry.CART, VehicleRegistry.BOAT);
		register(coalSmall);
		ModuleData solarSmall = new ModuleData("basic_solar_engine", ModuleSolarBasic.class, 12) {
			@Override
			@SideOnly(Side.CLIENT)
			public void loadModels() {
				addModel("SolarPanelBase", new ModelSolarPanelBase());
				addModel("SolarPanels", new ModelSolarPanelHeads(2));
				removeModel("Top");
			}
		};
		solarSmall.addShapedRecipe(SOLAR_PANEL, Items.IRON_INGOT, SOLAR_PANEL, Items.IRON_INGOT, SIMPLE_PCB, Items.IRON_INGOT, null, Blocks.PISTON, null);
		solarSmall.addSides(ModuleSide.CENTER, ModuleSide.TOP);
		solarSmall.addVehicles(VehicleRegistry.CART, VehicleRegistry.BOAT);
		register(solarSmall);
		ModuleData thermalSmall = new ModuleData("thermal_engine", ModuleThermalStandard.class, 28);
		thermalSmall.addShapedRecipe(Blocks.NETHER_BRICK, Blocks.NETHER_BRICK, Blocks.NETHER_BRICK, Blocks.OBSIDIAN, Blocks.FURNACE, Blocks.OBSIDIAN, Blocks.PISTON, null, Blocks.PISTON);
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
		coalLarge.addShapedRecipe(Items.IRON_INGOT, Items.IRON_INGOT, Items.IRON_INGOT, Items.IRON_INGOT, Blocks.FURNACE, Items.IRON_INGOT, Blocks.PISTON, null, Blocks.PISTON);
		coalLarge.addVehicles(VehicleRegistry.CART, VehicleRegistry.BOAT);
		register(coalLarge);
		ModuleData.addNemesis(coalLarge, coalSmall);
		ModuleData solarLarge = new ModuleData("solar_engine", ModuleSolarStandard.class, 20) {
			@Override
			@SideOnly(Side.CLIENT)
			public void loadModels() {
				addModel("SolarPanelBase", new ModelSolarPanelBase());
				addModel("SolarPanels", new ModelSolarPanelHeads(4));
				removeModel("Top");
			}
		};
		solarLarge.addShapedRecipe(Items.IRON_INGOT, SOLAR_PANEL, Items.IRON_INGOT, SOLAR_PANEL, ADVANCED_PCB, SOLAR_PANEL, Blocks.PISTON, SOLAR_PANEL, Blocks.PISTON);
		solarLarge.addSides(ModuleSide.CENTER, ModuleSide.TOP);
		solarLarge.addVehicles(VehicleRegistry.CART, VehicleRegistry.BOAT);
		register(solarLarge);
		ModuleData.addNemesis(solarLarge, solarSmall);
		engines.add(solarLarge);
		ModuleData thermalLarge = new ModuleData("advanced_thermal_engine", ModuleThermalAdvanced.class, 58);
		thermalLarge.addShapedRecipe(Blocks.NETHER_BRICK, Blocks.NETHER_BRICK, Blocks.NETHER_BRICK, REINFORCED_METAL, thermalSmall, REINFORCED_METAL, Blocks.PISTON, null, Blocks.PISTON);
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
		solarAdvanced.addShapedRecipe(ADVANCED_SOLAR_PANEL, Items.IRON_INGOT, ADVANCED_SOLAR_PANEL, ADVANCED_PCB, Items.REDSTONE, ADVANCED_PCB, Blocks.PISTON, Items.IRON_INGOT, Blocks.PISTON);
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
