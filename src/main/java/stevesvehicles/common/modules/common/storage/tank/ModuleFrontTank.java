package stevesvehicles.common.modules.common.storage.tank;

import stevesvehicles.common.vehicles.VehicleBase;

public class ModuleFrontTank extends ModuleTank {
	public ModuleFrontTank(VehicleBase vehicleBase) {
		super(vehicleBase);
	}

	@Override
	protected int getTankSize() {
		return 8000;
	}
}
