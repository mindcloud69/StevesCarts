package stevesvehicles.common.modules.datas.registries;

import static stevesvehicles.common.items.ComponentTypes.SIMPLE_PCB;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import stevesvehicles.common.modules.common.storage.barrel.ModuleBarrelBasic;
import stevesvehicles.common.modules.common.storage.barrel.ModuleBarrelBig;
import stevesvehicles.common.modules.common.storage.barrel.ModuleBarrelMedium;
import stevesvehicles.common.modules.datas.ModuleData;
import stevesvehicles.common.vehicles.VehicleRegistry;

public class ModuleRegistryBarrel extends ModuleRegistry {
	private static final String PLANK = "plankWood";

	public ModuleRegistryBarrel() {
		super("common.barrel");
		ModuleData basic = new ModuleData("basic_barrel", ModuleBarrelBasic.class, 10);
		basic.addShapedRecipe(PLANK, SIMPLE_PCB, PLANK, PLANK, Blocks.CHEST, PLANK, PLANK, PLANK, PLANK);
		basic.addVehicles(VehicleRegistry.CART, VehicleRegistry.BOAT);
		basic.setAllowDuplicate(true);
		register(basic);
		ModuleData normal = new ModuleData("normal_barrel", ModuleBarrelMedium.class, 30);
		normal.addShapedRecipe(Items.IRON_INGOT, SIMPLE_PCB, Items.IRON_INGOT, Items.IRON_INGOT, basic, Items.IRON_INGOT, Items.IRON_INGOT, Items.IRON_INGOT, Items.IRON_INGOT);
		normal.addVehicles(VehicleRegistry.CART, VehicleRegistry.BOAT);
		normal.setAllowDuplicate(true);
		register(normal);
		ModuleData large = new ModuleData("large_barrel", ModuleBarrelBig.class, 50);
		large.addShapedRecipe(Items.GOLD_INGOT, SIMPLE_PCB, Items.GOLD_INGOT, Items.GOLD_INGOT, normal, Items.GOLD_INGOT, Items.GOLD_INGOT, Items.GOLD_INGOT, Items.GOLD_INGOT);
		large.addVehicles(VehicleRegistry.CART, VehicleRegistry.BOAT);
		large.setAllowDuplicate(true);
		register(large);
	}
}
