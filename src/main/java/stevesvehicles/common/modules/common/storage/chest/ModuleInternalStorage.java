package stevesvehicles.common.modules.common.storage.chest;

import stevesvehicles.common.vehicles.VehicleBase;

public class ModuleInternalStorage extends ModuleChest {
	public ModuleInternalStorage(VehicleBase vehicleBase) {
		super(vehicleBase);
	}

	@Override
	protected int getInventoryWidth() {
		return 3;
	}

	@Override
	protected int getInventoryHeight() {
		return 3;
	}

	@Override
	protected boolean hasVisualChest() {
		return false;
	}

	@Override
	public int guiWidth() {
		return super.guiWidth() + 30;
	}
}
