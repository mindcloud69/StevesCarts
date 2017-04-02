package vswe.stevescarts.modules.addons.projectiles;

import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.EntityEgg;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import vswe.stevescarts.entitys.EntityMinecartModular;

import javax.annotation.Nonnull;

public class ModuleEgg extends ModuleProjectile {
	public ModuleEgg(final EntityMinecartModular cart) {
		super(cart);
	}

	@Override
	public boolean isValidProjectile(
		@Nonnull
			ItemStack item) {
		return item.getItem() == Items.EGG;
	}

	@Override
	public Entity createProjectile(final Entity target,
	                               @Nonnull
		                               ItemStack item) {
		return new EntityEgg(this.getCart().world);
	}
}
