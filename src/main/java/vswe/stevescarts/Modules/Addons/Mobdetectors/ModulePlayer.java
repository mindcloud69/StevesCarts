package vswe.stevescarts.Modules.Addons.Mobdetectors;

import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayerMP;
import vswe.stevescarts.Carts.MinecartModular;
import vswe.stevescarts.Helpers.Localization;

public class ModulePlayer extends ModuleMobdetector {
	public ModulePlayer(final MinecartModular cart) {
		super(cart);
	}

	@Override
	public String getName() {
		return Localization.MODULES.ADDONS.DETECTOR_PLAYERS.translate();
	}

	@Override
	public boolean isValidTarget(final Entity target) {
		return target instanceof EntityPlayerMP || (target instanceof EntityTameable && ((EntityTameable) target).isTamed());
	}
}
