package stevesvehicles.common.modules.cart.tool;

import stevesvehicles.common.vehicles.VehicleBase;

public class ModuleWoodcutterGalgadorian extends ModuleWoodcutter {
	public ModuleWoodcutterGalgadorian(VehicleBase vehicleBase) {
		super(vehicleBase);
	}

	@Override
	public int getPercentageDropChance() {
		return 125;
	}
}
