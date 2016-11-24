package stevesvehicles.common.blocks.tileentitys.detector.modulestate;

import stevesvehicles.common.modules.ModuleBase;
import stevesvehicles.common.modules.common.storage.chest.ModuleChest;
import stevesvehicles.common.vehicles.VehicleBase;

public class ModuleStateChest extends ModuleState {
	private boolean full;

	public ModuleStateChest(String unlocalizedName, boolean full) {
		super(unlocalizedName);
		this.full = full;
	}

	@Override
	public boolean isValid(VehicleBase vehicle) {
		boolean hasModule = false;
		for (ModuleBase moduleBase : vehicle.getModules()) {
			if (moduleBase instanceof ModuleChest) {
				ModuleChest chest = (ModuleChest) moduleBase;
				if (full && !chest.isCompletelyFilled()) {
					return false;
				} else if (!full && !chest.isCompletelyEmpty()) {
					return false;
				}
				hasModule = true;
			}
		}
		return hasModule;
	}
}
