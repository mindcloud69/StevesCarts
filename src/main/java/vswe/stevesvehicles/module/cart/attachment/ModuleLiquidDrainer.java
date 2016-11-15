package vswe.stevesvehicles.module.cart.attachment;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidBlock;

import vswe.stevesvehicles.module.cart.ModuleWorker;
import vswe.stevesvehicles.module.cart.tool.ModuleDrill;
import vswe.stevesvehicles.util.BlockPosUtils;
import vswe.stevesvehicles.vehicle.VehicleBase;

public class ModuleLiquidDrainer extends ModuleWorker {
	public ModuleLiquidDrainer(VehicleBase vehicleBase) {
		super(vehicleBase);
	}

	// lower numbers are prioritized
	@Override
	public byte getWorkPriority() {
		return (byte) 0;
	}

	// return true when the work is done, false allow other modules to continue
	// the work
	@Override
	public boolean work() {
		return false;
	}

	public void handleLiquid(ModuleDrill drill, BlockPos traget) {
		ArrayList<BlockPos> checked = new ArrayList<>();
		int result = drainAt(getVehicle().getWorld(), drill, checked, traget, 0);
		if (result > 0 && doPreWork()) {
			drill.kill();
			startWorking((int) (2.5F * result));
		} else {
			stopWorking();
		}
	}

	@Override
	public boolean preventAutoShutdown() {
		return true;
	}

	private int drainAt(World world, ModuleDrill drill, ArrayList<BlockPos> checked, BlockPos here, int buckets) {
		int drained = 0;
		IBlockState state = world.getBlockState(here);
		Block block = state.getBlock();
		if (!isLiquid(block)) {
			return 0;
		}
		FluidStack liquid = getFluidStack(world, block, here, !doPreWork());
		if (liquid != null) {
			if (doPreWork()) {
				liquid.amount += buckets * Fluid.BUCKET_VOLUME;
			}
			int amount = getVehicle().fill(liquid, false);
			if (amount == liquid.amount) {
				boolean canDrain = state.getValue(BlockLiquid.LEVEL) == 0;
				if (!doPreWork()) {
					if (canDrain) {
						getVehicle().fill(liquid, true);
					}
					getVehicle().getWorld().setBlockToAir(here);
				}
				drained += canDrain ? 40 : 3;
				buckets += canDrain ? 1 : 0;
			}
		}
		checked.add(here);
		if (checked.size() < 100 && BlockPosUtils.getHorizontalDistToVehicleSquared(here, getVehicle()) < 200) {
			for (int y = 1; y >= 0; y--) {
				for (int x = -1; x <= 1; x++) {
					for (int z = -1; z <= 1; z++) {
						if (Math.abs(x) + Math.abs(y) + Math.abs(z) == 1) {
							BlockPos next = new BlockPos(here.getX() + x, here.getY() + y, here.getZ() + z);
							if (!checked.contains(next)) {
								drained += drainAt(world, drill, checked, next, buckets);
							}
						}
					}
				}
			}
		}
		return drained;
	}

	private boolean isLiquid(Block b) {
		boolean isWater = b == Blocks.WATER || b == Blocks.FLOWING_WATER || b == Blocks.ICE;
		boolean isLava = b == Blocks.LAVA || b == Blocks.FLOWING_LAVA;
		boolean isOther = b != null && b instanceof IFluidBlock;
		return isWater || isLava || isOther;
	}

	private FluidStack getFluidStack(World world, Block b, BlockPos pos, boolean doDrain) {
		if (b == Blocks.WATER || b == Blocks.FLOWING_WATER) {
			return new FluidStack(FluidRegistry.WATER, Fluid.BUCKET_VOLUME);
		} else if (b == Blocks.LAVA || b == Blocks.FLOWING_LAVA) {
			return new FluidStack(FluidRegistry.LAVA, Fluid.BUCKET_VOLUME);
		} else if (b instanceof IFluidBlock) {
			IFluidBlock liquid = (IFluidBlock) b;
			return liquid.drain(world, pos, doDrain);
		} else {
			return null;
		}
	}
}