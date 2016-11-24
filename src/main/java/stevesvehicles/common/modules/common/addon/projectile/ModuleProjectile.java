package stevesvehicles.common.modules.common.addon.projectile;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import stevesvehicles.common.modules.common.addon.ModuleAddon;
import stevesvehicles.common.vehicles.VehicleBase;

public abstract class ModuleProjectile extends ModuleAddon {
	public ModuleProjectile(VehicleBase vehicleBase) {
		super(vehicleBase);
	}

	public abstract boolean isValidProjectile(ItemStack item);

	public abstract Entity createProjectile(Entity target, ItemStack item);
}
