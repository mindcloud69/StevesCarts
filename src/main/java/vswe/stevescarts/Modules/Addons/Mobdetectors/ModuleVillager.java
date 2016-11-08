package vswe.stevescarts.modules.addons.mobdetectors;

import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityGolem;
import net.minecraft.entity.passive.EntityVillager;
import vswe.stevescarts.entitys.MinecartModular;
import vswe.stevescarts.helpers.Localization;

public class ModuleVillager extends ModuleMobdetector {
	public ModuleVillager(final MinecartModular cart) {
		super(cart);
	}

	@Override
	public String getName() {
		return Localization.MODULES.ADDONS.DETECTOR_VILLAGERS.translate();
	}

	@Override
	public boolean isValidTarget(final Entity target) {
		return target instanceof EntityGolem || target instanceof EntityVillager;
	}
}
