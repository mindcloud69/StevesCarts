package stevesvehicles.common.modules.common.storage.barrel;

import stevesvehicles.common.vehicles.VehicleBase;

public class ModuleBarrelMedium extends ModuleBarrel {
	public ModuleBarrelMedium(VehicleBase vehicleBase) {
		super(vehicleBase);
	}

	@Override
	protected int getStackCount() {
		return 256;
	}
}
