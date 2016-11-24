package stevesvehicles.common.blocks.tileentitys.detector.modulestate;

import stevesvehicles.common.modules.ModuleBase;
import stevesvehicles.common.modules.common.addon.ModulePowerObserver;
import stevesvehicles.common.vehicles.VehicleBase;

public class ModuleStatePower extends ModuleState {
	private int colorId;

	public ModuleStatePower(String unlocalizedName, int colorId) {
		super(unlocalizedName);
		this.colorId = colorId;
	}

	@Override
	public boolean isValid(VehicleBase vehicle) {
		for (ModuleBase moduleBase : vehicle.getModules()) {
			if (moduleBase instanceof ModulePowerObserver) {
				return ((ModulePowerObserver) moduleBase).isAreaActive(colorId);
			}
		}
		return false;
	}
}
