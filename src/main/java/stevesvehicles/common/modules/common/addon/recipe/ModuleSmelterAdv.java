package stevesvehicles.common.modules.common.addon.recipe;

import stevesvehicles.common.vehicles.VehicleBase;

public class ModuleSmelterAdv extends ModuleSmelter {
	public ModuleSmelterAdv(VehicleBase vehicleBase) {
		super(vehicleBase);
	}

	@Override
	protected boolean canUseAdvancedFeatures() {
		return true;
	}
}
