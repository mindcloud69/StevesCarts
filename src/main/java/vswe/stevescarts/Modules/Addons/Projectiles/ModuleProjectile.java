package vswe.stevescarts.Modules.Addons.Projectiles;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import vswe.stevescarts.Carts.MinecartModular;
import vswe.stevescarts.Modules.Addons.ModuleAddon;

public abstract class ModuleProjectile extends ModuleAddon {
	public ModuleProjectile(final MinecartModular cart) {
		super(cart);
	}

	public abstract boolean isValidProjectile(final ItemStack p0);

	public abstract Entity createProjectile(final Entity p0, final ItemStack p1);
}
