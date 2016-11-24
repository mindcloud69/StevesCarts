package stevesvehicles.common.modules.common.storage.tank;

import stevesvehicles.common.vehicles.VehicleBase;

public class ModuleTopTank extends ModuleTank {
	public ModuleTopTank(VehicleBase vehicleBase) {
		super(vehicleBase);
	}

	@Override
	protected int getTankSize() {
		return 14000;
	}
}
