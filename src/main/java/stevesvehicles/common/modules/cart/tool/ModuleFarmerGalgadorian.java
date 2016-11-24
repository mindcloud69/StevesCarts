package stevesvehicles.common.modules.cart.tool;

import stevesvehicles.common.vehicles.VehicleBase;

public class ModuleFarmerGalgadorian extends ModuleFarmer {
	public ModuleFarmerGalgadorian(VehicleBase vehicleBase) {
		super(vehicleBase);
	}

	@Override
	public int getRange() {
		return 2;
	}
}
