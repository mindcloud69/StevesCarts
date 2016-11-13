package vswe.stevesvehicles.vehicle.entity;


import net.minecraft.inventory.IInventory;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import vswe.stevesvehicles.vehicle.VehicleBase;

public interface IVehicleEntity extends IInventory, IEntityAdditionalSpawnData, IFluidHandler {
	VehicleBase getVehicle();
}
