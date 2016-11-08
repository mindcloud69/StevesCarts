package vswe.stevescarts.Modules.Addons.Mobdetectors;

import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityTameable;
import vswe.stevescarts.Carts.MinecartModular;
import vswe.stevescarts.Helpers.Localization;

public class ModuleAnimal extends ModuleMobdetector {
	public ModuleAnimal(final MinecartModular cart) {
		super(cart);
	}

	@Override
	public String getName() {
		return Localization.MODULES.ADDONS.DETECTOR_ANIMALS.translate();
	}

	@Override
	public boolean isValidTarget(final Entity target) {
		return target instanceof EntityAnimal && (!(target instanceof EntityTameable) || !((EntityTameable) target).isTamed());
	}
}
