package stevesvehicles.common.modules.cart.tool;

import stevesvehicles.common.vehicles.VehicleBase;

public class ModuleWoodcutterDiamond extends ModuleWoodcutter {
	public ModuleWoodcutterDiamond(VehicleBase vehicleBase) {
		super(vehicleBase);
	}

	@Override
	public int getPercentageDropChance() {
		return 80;
	}
}
