package stevesvehicles.common.modules.common.storage.tank;

import stevesvehicles.common.vehicles.VehicleBase;

public class ModuleSideTanks extends ModuleTank {
	public ModuleSideTanks(VehicleBase vehicleBase) {
		super(vehicleBase);
	}

	@Override
	protected int getTankSize() {
		return 8000;
	}
}
