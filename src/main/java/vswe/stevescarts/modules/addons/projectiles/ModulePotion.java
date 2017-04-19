package vswe.stevescarts.modules.addons.projectiles;

import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.EntityPotion;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import vswe.stevescarts.entitys.EntityMinecartModular;

import javax.annotation.Nonnull;

public class ModulePotion extends ModuleProjectile {
	public ModulePotion(final EntityMinecartModular cart) {
		super(cart);
	}

	@Override
	public boolean isValidProjectile(
		@Nonnull
			ItemStack item) {
		return item.getItem() == Items.SPLASH_POTION;
	}

	@Override
	public Entity createProjectile(final Entity target,
	                               @Nonnull
		                               ItemStack item) {
		final EntityPotion potion = new EntityPotion(getCart().world);
		potion.setItem(item);
		return potion;
	}
}
