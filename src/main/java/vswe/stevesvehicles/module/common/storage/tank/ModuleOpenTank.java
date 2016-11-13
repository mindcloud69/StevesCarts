package vswe.stevesvehicles.module.common.storage.tank;

import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import vswe.stevesvehicles.vehicle.VehicleBase;

public class ModuleOpenTank extends ModuleTank {
	public ModuleOpenTank(VehicleBase vehicleBase) {
		super(vehicleBase);
	}

	@Override
	protected int getTankSize() {
		return 7000;
	}

	int cooldown = 0;
	private static final int RAIN_UPDATE_COOLDOWN = 20;
	private static final int RAIN_INCREASE = 20;
	private static final int SNOW_INCREASE = 5;

	@Override
	public void update() {
		super.update();
		World world = getVehicle().getWorld();
		if (cooldown > 0) {
			cooldown--;
		} else {
			cooldown = RAIN_UPDATE_COOLDOWN;
			if (world.isRaining() && world.canBlockSeeSky(getVehicle().pos().up()) && world.getPrecipitationHeight(getVehicle().pos()).getY() < getVehicle().pos().up().getY()) {
				fill(new FluidStack(FluidRegistry.WATER, getVehicle().getWorld().getBiome(getVehicle().pos()).getEnableSnow() ? SNOW_INCREASE : RAIN_INCREASE), true);
			}
		}
	}
}