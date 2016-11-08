package vswe.stevescarts.modules.addons.projectiles;

import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.EntitySmallFireball;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import vswe.stevescarts.entitys.MinecartModular;

public class ModuleFireball extends ModuleProjectile {
	public ModuleFireball(final MinecartModular cart) {
		super(cart);
	}

	@Override
	public boolean isValidProjectile(final ItemStack item) {
		return item.getItem() == Items.FIRE_CHARGE;
	}

	@Override
	public Entity createProjectile(final Entity target, final ItemStack item) {
		return new EntitySmallFireball(this.getCart().worldObj);
	}
}
