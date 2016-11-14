package vswe.stevesvehicles.module.common.addon;

import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import vswe.stevesvehicles.vehicle.VehicleBase;

public class ModuleSnowCannon extends ModuleAddon {
	public ModuleSnowCannon(VehicleBase vehicleBase) {
		super(vehicleBase);
	}

	// called to update the module's actions. Called by the cart's update code.
	@Override
	public void update() {
		super.update();
		if (getVehicle().getWorld().isRemote) {
			return;
		}
		if (getVehicle().hasFuel()) {
			if (tick >= getInterval()) {
				tick = 0;
				generateSnow();
			} else {
				tick++;
			}
		}
	}

	private int tick;

	protected int getInterval() {
		return 70;
	}

	protected int getBlocksOnSide() {
		return 7;
	}

	protected int getBlocksFromLevel() {
		return 1;
	}

	private void generateSnow() {
		for (int offsetX = -getBlocksOnSide(); offsetX <= getBlocksOnSide(); offsetX++) {
			for (int offsetZ = -getBlocksOnSide(); offsetZ <= getBlocksOnSide(); offsetZ++) {
				for (int offsetY = -getBlocksFromLevel(); offsetY <= getBlocksFromLevel(); offsetY++) {
					BlockPos target = getVehicle().pos().add(offsetX, offsetY, offsetZ);
					if (countsAsAir(target) && getVehicle().getWorld().getBiome(target).getFloatTemperature(target) <= 1.0F /* snow golems won't be hurt */ && Blocks.SNOW.canPlaceBlockAt(getVehicle().getWorld(), target)) {
						getVehicle().getWorld().setBlockState(target, Blocks.SNOW_LAYER.getDefaultState());
					}
				}
			}
		}
	}
}