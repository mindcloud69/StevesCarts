package vswe.stevescarts.modules.addons.projectiles;

import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.EntitySnowball;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import vswe.stevescarts.entitys.EntityMinecartModular;

public class ModuleSnowball extends ModuleProjectile {
	public ModuleSnowball(final EntityMinecartModular cart) {
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
