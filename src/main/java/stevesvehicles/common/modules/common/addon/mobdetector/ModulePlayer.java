package stevesvehicles.common.modules.common.addon.mobdetector;

import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayerMP;
import stevesvehicles.client.localization.entry.module.LocalizationShooter;
import stevesvehicles.common.vehicles.VehicleBase;

public class ModulePlayer extends ModuleEntityDetector {
	public ModulePlayer(VehicleBase vehicleBase) {
		super(vehicleBase);
	}

	@Override
	public String getName() {
		return LocalizationShooter.PLAYER_TITLE.translate();
	}

	@Override
	public boolean isValidTarget(Entity target) {
		return (target instanceof EntityPlayerMP || ((target instanceof EntityTameable) && ((EntityTameable) target).isTamed()));
	}
}
