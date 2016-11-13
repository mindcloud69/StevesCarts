package vswe.stevesvehicles.module.common.addon.mobdetector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityTameable;
import vswe.stevesvehicles.localization.entry.module.LocalizationShooter;
import vswe.stevesvehicles.vehicle.VehicleBase;

public class ModuleAnimal extends ModuleEntityDetector {
	public ModuleAnimal(VehicleBase vehicleBase) {
		super(vehicleBase);
	}

	@Override
	public String getName() {
		return LocalizationShooter.ANIMAL_TITLE.translate();
	}
	@Override
	public boolean isValidTarget(Entity target) {
		return
				target instanceof EntityAnimal
				&&
				(
						!(target instanceof EntityTameable)
						||
						!((EntityTameable)target).isTamed()
						)
				;
	}
}