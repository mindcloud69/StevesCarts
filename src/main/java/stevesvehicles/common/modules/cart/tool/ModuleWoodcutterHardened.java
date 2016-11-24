package stevesvehicles.common.modules.cart.tool;

import stevesvehicles.common.vehicles.VehicleBase;

public class ModuleWoodcutterHardened extends ModuleWoodcutter {
	public ModuleWoodcutterHardened(VehicleBase vehicleBase) {
		super(vehicleBase);
	}

	@Override
	public int getPercentageDropChance() {
		return 100;
	}
}
