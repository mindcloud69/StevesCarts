package stevesvehicles.common.modules.common.storage.chest;

import stevesvehicles.common.vehicles.VehicleBase;

public class ModuleTopChest extends ModuleChest {
	public ModuleTopChest(VehicleBase vehicleBase) {
		super(vehicleBase);
	}

	@Override
	protected int getInventoryWidth() {
		return 6;
	}

	@Override
	protected int getInventoryHeight() {
		return 3;
	}
}
