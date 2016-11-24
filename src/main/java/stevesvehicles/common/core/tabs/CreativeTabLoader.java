package stevesvehicles.common.core.tabs;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.item.ItemStack;
import stevesvehicles.common.blocks.ModBlocks;
import stevesvehicles.common.items.ComponentTypes;
import stevesvehicles.common.vehicles.VehicleRegistry;
import stevesvehicles.common.vehicles.VehicleType;

public final class CreativeTabLoader {
	private static Map<VehicleType, CreativeTabVehicle> vehicles;
	private static CreativeTabVehicle[] vehicleList;
	public static CreativeTabCustom components;
	public static CreativeTabCustom blocks;

	public static void init() {
		vehicles = new HashMap<>();
		vehicleList = new CreativeTabVehicle[VehicleRegistry.getInstance().getAllVehicles().size()];
		List<VehicleType> allVehicles = VehicleRegistry.getInstance().getAllVehicles();
		for (int i = 0; i < allVehicles.size(); i++) {
			VehicleType vehicleType = allVehicles.get(i);
			CreativeTabVehicle tab = new CreativeTabVehicle(vehicleType);
			vehicleList[i] = tab;
			vehicles.put(vehicleType, tab);
		}
		components = new CreativeTabSimple("items");
		blocks = new CreativeTabSimple("blocks");
	}

	public static void postInit() {
		components.setIcon(ComponentTypes.REINFORCED_WHEELS.getItemStack());
		blocks.setIcon(new ItemStack(ModBlocks.CART_ASSEMBLER.getBlock()));
	}

	public static CreativeTabVehicle getVehicleTab(VehicleType type) {
		return vehicles.get(type);
	}

	public static CreativeTabVehicle[] getAllVehicleTabs() {
		return vehicleList;
	}

	private CreativeTabLoader() {
	}
}
