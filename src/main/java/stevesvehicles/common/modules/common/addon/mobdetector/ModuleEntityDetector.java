package stevesvehicles.common.modules.common.addon.mobdetector;

import net.minecraft.entity.Entity;
import stevesvehicles.common.modules.common.addon.ModuleAddon;
import stevesvehicles.common.vehicles.VehicleBase;

public abstract class ModuleEntityDetector extends ModuleAddon {
	public ModuleEntityDetector(VehicleBase vehicleBase) {
		super(vehicleBase);
	}

	public abstract String getName();

	public abstract boolean isValidTarget(Entity target);
}
