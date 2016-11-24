package stevesvehicles.common.modules.cart.attachment;

import stevesvehicles.common.vehicles.VehicleBase;

public class ModuleRailerLarge extends ModuleRailer {
	public ModuleRailerLarge(VehicleBase vehicleBase) {
		super(vehicleBase);
	}

	@Override
	protected int getInventoryHeight() {
		return 2;
	}
}
