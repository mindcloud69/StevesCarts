package vswe.stevescarts.modules.addons.mobdetectors;

import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.passive.EntityWolf;
import vswe.stevescarts.entitys.EntityMinecartModular;
import vswe.stevescarts.helpers.Localization;

public class ModuleMonster extends ModuleMobdetector {
	public ModuleMonster(final EntityMinecartModular cart) {
		super(cart);
	}

	@Override
	public String getName() {
		return Localization.MODULES.ADDONS.DETECTOR_MONSTERS.translate();
	}

	@Override
	public boolean isValidTarget(final Entity target) {
		return (target instanceof EntityMob || target instanceof EntityDragon || target instanceof EntityGhast || target instanceof EntitySlime || target instanceof EntityEnderCrystal || (target instanceof EntityWolf && ((EntityWolf) target).isAngry())) && !(target instanceof EntityEnderman);
	}
}
