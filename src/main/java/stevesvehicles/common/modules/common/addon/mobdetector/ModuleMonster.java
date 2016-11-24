package stevesvehicles.common.modules.common.addon.mobdetector;

import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.passive.EntityWolf;
import stevesvehicles.client.localization.entry.module.LocalizationShooter;
import stevesvehicles.common.vehicles.VehicleBase;

public class ModuleMonster extends ModuleEntityDetector {
	public ModuleMonster(VehicleBase vehicleBase) {
		super(vehicleBase);
	}

	@Override
	public String getName() {
		return LocalizationShooter.MONSTER_TITLE.translate();
	}

	@Override
	public boolean isValidTarget(Entity target) {
		return (target instanceof EntityMob || target instanceof EntityDragon || target instanceof EntityGhast || target instanceof EntitySlime || target instanceof EntityEnderCrystal || ((target instanceof EntityWolf) && ((EntityWolf) target).isAngry()))
				&& !(target instanceof EntityEnderman) // projectiles can't hit
				// them anyways
				;
	}
}
