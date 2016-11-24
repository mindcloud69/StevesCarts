package stevesvehicles.common.modules.common.storage.chest;

import stevesvehicles.common.vehicles.VehicleBase;

public class ModuleEggBasket extends ModuleChest {
	public ModuleEggBasket(VehicleBase vehicleBase) {
		super(vehicleBase);
	}

	@Override
	protected int getInventoryWidth() {
		return 6;
	}

	@Override
	protected int getInventoryHeight() {
		return 4;
	}

	@Override
	protected boolean playChestSound() {
		return false;
	}

	@Override
	protected float getLidSpeed() {
		return (float) (Math.PI / 150);
	}

	@Override
	protected float chestFullyOpenAngle() {
		return (float) Math.PI / 8F;
	}
}
