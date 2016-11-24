package stevesvehicles.common.modules.cart.tool;

import stevesvehicles.common.vehicles.VehicleBase;

public class ModuleFarmerDiamond extends ModuleFarmer {
	public ModuleFarmerDiamond(VehicleBase vehicleBase) {
		super(vehicleBase);
	}

	@Override
	public int getRange() {
		return 1;
	}
}
