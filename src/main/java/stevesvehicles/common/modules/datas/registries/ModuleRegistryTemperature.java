package stevesvehicles.common.modules.datas.registries;

import static stevesvehicles.common.items.ComponentTypes.SIMPLE_PCB;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import stevesvehicles.common.core.StevesVehicles;
import stevesvehicles.common.holiday.HolidayType;
import stevesvehicles.common.modules.common.addon.ModuleMelter;
import stevesvehicles.common.modules.common.addon.ModuleMelterExtreme;
import stevesvehicles.common.modules.common.addon.ModuleSnowCannon;
import stevesvehicles.common.modules.datas.ModuleData;
import stevesvehicles.common.vehicles.VehicleRegistry;

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
