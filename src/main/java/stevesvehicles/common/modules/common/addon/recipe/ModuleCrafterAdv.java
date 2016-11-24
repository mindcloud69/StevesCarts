package stevesvehicles.common.modules.common.addon.recipe;

import stevesvehicles.common.vehicles.VehicleBase;

public class ModuleCrafterAdv extends ModuleCrafter {
	public ModuleCrafterAdv(VehicleBase vehicleBase) {
		super(vehicleBase);
	}

	@Override
	protected boolean canUseAdvancedFeatures() {
		return true;
	}
}
