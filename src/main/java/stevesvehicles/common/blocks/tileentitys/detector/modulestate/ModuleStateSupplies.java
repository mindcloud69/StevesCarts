package stevesvehicles.common.blocks.tileentitys.detector.modulestate;

import stevesvehicles.common.modules.ISuppliesModule;
import stevesvehicles.common.modules.ModuleBase;
import stevesvehicles.common.vehicles.VehicleBase;

public class ModuleStateSupplies extends ModuleState {
	private Class<? extends ISuppliesModule> moduleClass;

	public ModuleStateSupplies(String unlocalizedName, Class<? extends ISuppliesModule> moduleClass) {
		super(unlocalizedName);
		this.moduleClass = moduleClass;
	}

	@Override
	public boolean isValid(VehicleBase vehicle) {
		for (ModuleBase moduleBase : vehicle.getModules()) {
			if (moduleClass.isAssignableFrom(moduleBase.getClass())) {
				return ((ISuppliesModule) moduleBase).haveSupplies();
			}
		}
		return false;
	}
}
