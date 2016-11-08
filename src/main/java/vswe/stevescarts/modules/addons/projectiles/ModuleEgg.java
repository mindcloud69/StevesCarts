package vswe.stevescarts.modules.addons.projectiles;

import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.EntityEgg;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import vswe.stevescarts.entitys.MinecartModular;

public class ModuleEgg extends ModuleProjectile {
	public ModuleEgg(final MinecartModular cart) {
		super(cart);
	}

	@Override
	public boolean isValidProjectile(final ItemStack item) {
		return item.getItem() == Items.EGG;
	}

	@Override
	public Entity createProjectile(final Entity target, final ItemStack item) {
		return new EntityEgg(this.getCart().worldObj);
	}
}
