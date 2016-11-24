package stevesvehicles.common.blocks.tileentitys.detector.modulestate;

import stevesvehicles.common.modules.IActivatorModule;
import stevesvehicles.common.modules.ModuleBase;
import stevesvehicles.common.vehicles.VehicleBase;

public class ModuleStateActivation extends ModuleState {
	private Class<? extends IActivatorModule> moduleClass;
	private int id;

	public ModuleStateActivation(String unlocalizedName, Class<? extends IActivatorModule> moduleClass, int id) {
		super(unlocalizedName);
		this.moduleClass = moduleClass;
		this.id = id;
	}

	@Override
	public boolean isValid(VehicleBase vehicle) {
		for (ModuleBase moduleBase : vehicle.getModules()) {
			if (moduleClass.isAssignableFrom(moduleBase.getClass())) {
				return ((IActivatorModule) moduleBase).isActive(id);
			}
		}
		return false;
	}
}
