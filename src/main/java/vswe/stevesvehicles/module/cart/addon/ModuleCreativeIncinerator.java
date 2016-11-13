package vswe.stevesvehicles.module.cart.addon;

import net.minecraft.item.ItemStack;

import vswe.stevesvehicles.vehicle.VehicleBase;

public class ModuleCreativeIncinerator extends ModuleIncinerator {

	public ModuleCreativeIncinerator(VehicleBase vehicleBase) {
		super(vehicleBase);
	}

	@Override
	protected int getIncinerationCost() {
		return 0;
	}

	@Override
	protected boolean isItemValid(ItemStack item) {
		return item != null && item.getItem() != null;
	}	

	@Override
	public boolean hasGui() {
		return false;
	}		

}
