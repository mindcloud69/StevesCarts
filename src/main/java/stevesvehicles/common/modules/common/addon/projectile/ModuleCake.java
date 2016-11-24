package stevesvehicles.common.modules.common.addon.projectile;

import net.minecraft.entity.Entity;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import stevesvehicles.common.vehicles.VehicleBase;

public class ModuleCake extends ModuleProjectile {
	public ModuleCake(VehicleBase vehicleBase) {
		super(vehicleBase);
	}

	@Override
	public boolean isValidProjectile(ItemStack item) {
		return item.getItem() == Items.CAKE;
	}

	@Override
	public Entity createProjectile(Entity target, ItemStack item) {
		return new EntityCake(getVehicle().getWorld());
	}
}
