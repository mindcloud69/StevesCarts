package stevesvehicles.common.modules.common.storage.chest;

import stevesvehicles.common.vehicles.VehicleBase;

public class ModuleFrontChest extends ModuleChest {
	public ModuleFrontChest(VehicleBase vehicleBase) {
		super(vehicleBase);
	}

	@Override
	protected int getInventoryWidth() {
		return 4;
	}

	@Override
	protected int getInventoryHeight() {
		return 3;
	}
}
