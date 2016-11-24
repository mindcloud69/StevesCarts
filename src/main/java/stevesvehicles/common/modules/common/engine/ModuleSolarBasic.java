package stevesvehicles.common.modules.common.engine;

import stevesvehicles.common.vehicles.VehicleBase;

public class ModuleSolarBasic extends ModuleSolarTop {
	public ModuleSolarBasic(VehicleBase vehicleBase) {
		super(vehicleBase);
	}

	@Override
	protected int getMaxCapacity() {
		return 100000;
	}

	@Override
	protected int getGenSpeed() {
		return 2;
	}
}
