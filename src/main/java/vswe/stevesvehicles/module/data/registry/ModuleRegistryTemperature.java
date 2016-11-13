package vswe.stevesvehicles.module.data.registry;

import static vswe.stevesvehicles.item.ComponentTypes.SIMPLE_PCB;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;

import vswe.stevesvehicles.StevesVehicles;
import vswe.stevesvehicles.holiday.HolidayType;
import vswe.stevesvehicles.module.common.addon.ModuleMelter;
import vswe.stevesvehicles.module.common.addon.ModuleMelterExtreme;
import vswe.stevesvehicles.module.common.addon.ModuleSnowCannon;
import vswe.stevesvehicles.module.data.ModuleData;
import vswe.stevesvehicles.vehicle.VehicleRegistry;

public class ModuleRegistryTemperature extends ModuleRegistry {
	public ModuleRegistryTemperature() {
		super("common.temperature");
		ModuleData melter = new ModuleData("melter", ModuleMelter.class, 10);
		melter.addShapedRecipe(Blocks.NETHER_BRICK, Blocks.GLOWSTONE, Blocks.NETHER_BRICK, Items.GLOWSTONE_DUST, Blocks.FURNACE, Items.GLOWSTONE_DUST, Blocks.NETHER_BRICK, Blocks.GLOWSTONE, Blocks.NETHER_BRICK);
		melter.addVehicles(VehicleRegistry.CART, VehicleRegistry.BOAT);
		register(melter);
		ModuleData extreme = new ModuleData("extreme_melter", ModuleMelterExtreme.class, 19);
		extreme.addShapedRecipe(Blocks.NETHER_BRICK, Blocks.OBSIDIAN, Blocks.NETHER_BRICK, melter, Items.LAVA_BUCKET, melter, Blocks.NETHER_BRICK, Blocks.OBSIDIAN, Blocks.NETHER_BRICK);
		extreme.addVehicles(VehicleRegistry.CART, VehicleRegistry.BOAT);
		register(extreme);
		ModuleData.addNemesis(melter, extreme);
		ModuleData freezer = new ModuleData("freezer", ModuleSnowCannon.class, 24);
		freezer.addShapedRecipe(Blocks.SNOW, Items.WATER_BUCKET, Blocks.SNOW, Items.WATER_BUCKET, SIMPLE_PCB, Items.WATER_BUCKET, Blocks.SNOW, Items.WATER_BUCKET, Blocks.SNOW);
		freezer.addVehicles(VehicleRegistry.CART, VehicleRegistry.BOAT);
		register(freezer);
		if (!StevesVehicles.holidays.contains(HolidayType.CHRISTMAS)) {
			freezer.lock();
		}
	}
}
