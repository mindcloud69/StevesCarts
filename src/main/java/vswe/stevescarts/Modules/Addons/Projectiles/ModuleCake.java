package vswe.stevescarts.modules.addons.projectiles;

import net.minecraft.entity.Entity;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import vswe.stevescarts.entitys.MinecartModular;
import vswe.stevescarts.helpers.EntityCake;

public class ModuleCake extends ModuleProjectile {
	public ModuleCake(final MinecartModular cart) {
		super(cart);
	}

	@Override
	public boolean isValidProjectile(final ItemStack item) {
		return item.getItem() == Items.CAKE;
	}

	@Override
	public Entity createProjectile(final Entity target, final ItemStack item) {
		return new EntityCake(this.getCart().worldObj);
	}
}
