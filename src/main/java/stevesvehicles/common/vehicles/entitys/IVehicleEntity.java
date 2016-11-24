package stevesvehicles.common.vehicles.entitys;

import net.minecraft.inventory.IInventory;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import stevesvehicles.common.vehicles.VehicleBase;

public interface IVehicleEntity extends IInventory, IEntityAdditionalSpawnData, IFluidHandler {
	VehicleBase getVehicle();
}
