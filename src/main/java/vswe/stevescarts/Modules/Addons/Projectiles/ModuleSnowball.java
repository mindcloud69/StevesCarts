package vswe.stevescarts.Modules.Addons.Projectiles;

import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.EntitySnowball;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import vswe.stevescarts.Carts.MinecartModular;

public class ModuleSnowball extends ModuleProjectile {
	public ModuleSnowball(final MinecartModular cart) {
		super(cart);
	}

	@Override
	public boolean isValidProjectile(final ItemStack item) {
		return item.getItem() == Items.SNOWBALL;
	}

	@Override
	public Entity createProjectile(final Entity target, final ItemStack item) {
		return new EntitySnowball(this.getCart().worldObj);
	}
}
