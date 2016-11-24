package stevesvehicles.common.modules.common.storage.chest;

import stevesvehicles.common.vehicles.VehicleBase;

public class ModuleGiftStorage extends ModuleChest {
	public ModuleGiftStorage(VehicleBase vehicleBase) {
		super(vehicleBase);
	}

	@Override
	protected int getInventoryWidth() {
		return 9;
	}

	@Override
	protected int getInventoryHeight() {
		return 4;
	}
}
