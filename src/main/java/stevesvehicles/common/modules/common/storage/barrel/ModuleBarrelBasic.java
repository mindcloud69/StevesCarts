package stevesvehicles.common.modules.common.storage.barrel;

import stevesvehicles.common.vehicles.VehicleBase;

public class ModuleBarrelBasic extends ModuleBarrel {
	public ModuleBarrelBasic(VehicleBase vehicleBase) {
		super(vehicleBase);
	}

	@Override
	protected int getStackCount() {
		return 64;
	}
}
