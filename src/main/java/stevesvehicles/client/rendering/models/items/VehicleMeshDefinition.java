package stevesvehicles.client.rendering.models.items;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.ItemStack;
import stevesvehicles.common.core.Constants;
import stevesvehicles.common.items.ModItems;
import stevesvehicles.common.modules.datas.ModuleDataItemHandler;
import stevesvehicles.common.vehicles.VehicleRegistry;
import stevesvehicles.common.vehicles.VehicleType;

public class VehicleMeshDefinition implements ItemMeshDefinition {
	private static final ModelResourceLocation VEHICLE_LOCATION = new ModelResourceLocation(ModItems.vehicles.getRegistryName(), "inventory");
	private Map<VehicleType, ModelResourceLocation> fallbackLocations = new HashMap<>();

	public VehicleMeshDefinition() {
	}

	@Override
	public ModelResourceLocation getModelLocation(ItemStack stack) {
		if (ModuleDataItemHandler.hasModules(stack)) {
			return VEHICLE_LOCATION;
		}
		return getLocation(VehicleRegistry.getInstance().getTypeFromId(stack.getItemDamage()));
	}

	private ModelResourceLocation getLocation(VehicleType type) {
		ModelResourceLocation location = fallbackLocations.get(type);
		if (location == null) {
			fallbackLocations.put(type, location = new ModelResourceLocation(Constants.MOD_ID + ":" + type.getUnlocalizedNameForItem().substring(5), "inventory"));
		}
		return location;
	}
}
