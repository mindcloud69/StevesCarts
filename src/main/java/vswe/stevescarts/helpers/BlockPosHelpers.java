package vswe.stevescarts.helpers;

import net.minecraft.util.math.BlockPos;
import vswe.stevescarts.entitys.EntityMinecartModular;

public class BlockPosHelpers {

	public static double getHorizontalDistToCartSquared(BlockPos pos, EntityMinecartModular cart) {
		final int xDif = pos.getX() - cart.x();
		final int zDif = pos.getZ() - cart.z();
		return Math.pow(xDif, 2.0) + Math.pow(zDif, 2.0);
	}

	public double getDistToCartSquared(BlockPos pos, EntityMinecartModular cart) {
		final int xDif = pos.getX() - cart.x();
		final int yDif = pos.getY() - cart.y();
		final int zDif = pos.getZ() - cart.z();
		return Math.pow(xDif, 2.0) + Math.pow(yDif, 2.0) + Math.pow(zDif, 2.0);
	}
}
