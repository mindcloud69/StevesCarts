package vswe.stevesvehicles.module.common.addon.projectile;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.EntityPotion;
import net.minecraft.init.Items;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;

import vswe.stevesvehicles.vehicle.VehicleBase;

public class ModulePotion extends ModuleProjectile {
	public ModulePotion(VehicleBase vehicleBase) {
		super(vehicleBase);
	}


	@Override
	public boolean isValidProjectile(ItemStack item) {
		return item.getItem() == Items.potionitem && ItemPotion.isSplash(item.getItemDamage());
	}
	@Override
	public Entity createProjectile(Entity target, ItemStack item) {
		return new EntityPotion(getVehicle().getWorld(), 0, 0, 0, item);
	}

}