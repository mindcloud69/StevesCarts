package vswe.stevesvehicles.module.common.addon;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import vswe.stevesvehicles.vehicle.VehicleBase;

public class ModuleMelterExtreme extends ModuleMelter {
	public ModuleMelterExtreme(VehicleBase vehicleBase) {
		super(vehicleBase);
	}

	@Override
	protected boolean melt(IBlockState state, BlockPos pos) {
		if (!super.melt(state, pos)) {
			if (state.getBlock() == Blocks.SNOW) {
				getVehicle().getWorld().setBlockToAir(pos);
				return true;
			} else if (state.getBlock() == Blocks.ICE) {
				getVehicle().getWorld().setBlockState(pos, Blocks.WATER.getDefaultState());
				return true;
			}
		}
		return false;
	}
}
