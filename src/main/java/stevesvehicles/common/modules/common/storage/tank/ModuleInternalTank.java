package stevesvehicles.common.modules.common.storage.tank;

import stevesvehicles.common.vehicles.VehicleBase;

public class ModuleInternalTank extends ModuleTank {
	public ModuleInternalTank(VehicleBase vehicleBase) {
		super(vehicleBase);
	}

	@Override
	protected int getTankSize() {
		return 4000;
	}

	@Override
	public boolean hasVisualTank() {
		return false;
	}
}
