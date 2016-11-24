package stevesvehicles.common.modules.common.addon.mobdetector;

import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityBat;
import stevesvehicles.client.localization.entry.module.LocalizationShooter;
import stevesvehicles.common.vehicles.VehicleBase;

public class ModuleBat extends ModuleEntityDetector {
	public ModuleBat(VehicleBase vehicleBase) {
		super(vehicleBase);
	}

	@Override
	public String getName() {
		return LocalizationShooter.BAT_TITLE.translate();
	}

	@Override
	public boolean isValidTarget(Entity target) {
		return (target instanceof EntityBat);
	}
}
