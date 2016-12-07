package stevesvehicles.common.blocks.tileentitys.detector.modulestate;

import net.minecraft.entity.Entity;
import stevesvehicles.common.vehicles.VehicleBase;

public class ModuleStatePassenger extends ModuleState {
	private Class passengerClass;

	public ModuleStatePassenger(String unlocalizedName, Class passengerClass) {
		super(unlocalizedName);
		this.passengerClass = passengerClass;
	}

	@Override
	public boolean isValid(VehicleBase vehicle) {
		Entity passenger = vehicle.getEntity().getControllingPassenger();
		return passenger != null && passengerClass.isAssignableFrom(passenger.getClass()) && isPassengerValid(passenger);
	}

	public boolean isPassengerValid(Entity passenger) {
		return true;
	}
}
