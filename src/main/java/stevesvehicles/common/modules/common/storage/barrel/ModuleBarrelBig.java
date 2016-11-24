package stevesvehicles.common.modules.common.storage.barrel;

import stevesvehicles.common.vehicles.VehicleBase;

public class ModuleBarrelBig extends ModuleBarrel {
	public ModuleBarrelBig(VehicleBase vehicleBase) {
		super(vehicleBase);
	}

	@Override
	protected int getStackCount() {
		return 1024;
	}
}
