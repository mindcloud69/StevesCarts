package stevesvehicles.common.modules.common.storage.tank;

import stevesvehicles.common.vehicles.VehicleBase;

public class ModuleAdvancedTank extends ModuleTank {
	public ModuleAdvancedTank(VehicleBase vehicleBase) {
		super(vehicleBase);
	}

	@Override
	protected int getTankSize() {
		return 32000;
	}
}
