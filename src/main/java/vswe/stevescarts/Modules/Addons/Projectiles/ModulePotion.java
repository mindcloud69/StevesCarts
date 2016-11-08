package vswe.stevescarts.Modules.Addons.Projectiles;

import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.EntityPotion;
import net.minecraft.item.ItemStack;
import vswe.stevescarts.Carts.MinecartModular;

public class ModulePotion extends ModuleProjectile {
	public ModulePotion(final MinecartModular cart) {
		super(cart);
	}

	@Override
	public boolean isValidProjectile(final ItemStack item) {
		//		return item.getItem() == Items.POTIONITEM && ItemPotion.isSplash(item.getItemDamage());
		return false; //TODO
	}

	@Override
	public Entity createProjectile(final Entity target, final ItemStack item) {
		final EntityPotion potion = new EntityPotion(this.getCart().worldObj);
		potion.setItem(item);
		return potion;
	}
}
