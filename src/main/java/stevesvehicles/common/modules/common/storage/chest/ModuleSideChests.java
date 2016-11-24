package stevesvehicles.common.modules.common.storage.chest;

import stevesvehicles.common.vehicles.VehicleBase;

public class ModuleSideChests extends ModuleChest {
	public ModuleSideChests(VehicleBase vehicleBase) {
		super(vehicleBase);
	}

	@Override
	protected int getInventoryWidth() {
		return 5;
	}

	@Override
	protected int getInventoryHeight() {
		return 3;
	}
}
