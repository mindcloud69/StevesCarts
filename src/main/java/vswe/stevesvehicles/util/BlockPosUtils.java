package vswe.stevesvehicles.util;

import net.minecraft.util.math.BlockPos;
import vswe.stevesvehicles.vehicle.VehicleBase;

public class BlockPosUtils {

	public static double getHorizontalDistToVehicleSquared(BlockPos pos, VehicleBase vehicle) {
		final int xDif = pos.getX() - vehicle.x();
		final int zDif = pos.getZ() - vehicle.z();
		return Math.pow(xDif, 2.0) + Math.pow(zDif, 2.0);
	}

	public double getDistToVehicleSquared(BlockPos pos, VehicleBase vehicle) {
		final int xDif = pos.getX() - vehicle.x();
		final int yDif = pos.getY() - vehicle.y();
		final int zDif = pos.getZ() - vehicle.z();
		return Math.pow(xDif, 2.0) + Math.pow(yDif, 2.0) + Math.pow(zDif, 2.0);
	}
}
