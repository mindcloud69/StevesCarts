package stevesvehicles.common.modules.common.engine;

import stevesvehicles.common.vehicles.VehicleBase;

public class ModuleCoalStandard extends ModuleCoalBase {
	public ModuleCoalStandard(VehicleBase vehicleBase) {
		super(vehicleBase);
	}

	@Override
	public double getFuelMultiplier() {
		return 2.25;
	}
}
