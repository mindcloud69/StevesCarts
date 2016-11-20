package vswe.stevesvehicles.module.cart.attachment;

import net.minecraft.block.BlockFarmland;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidRegistry;

import vswe.stevesvehicles.module.ModuleBase;
import vswe.stevesvehicles.module.cart.ModuleWorker;
import vswe.stevesvehicles.module.cart.tool.ModuleFarmer;
import vswe.stevesvehicles.vehicle.VehicleBase;

public class ModuleHydrater extends ModuleWorker {
	public ModuleHydrater(VehicleBase vehicleBase) {
		super(vehicleBase);
	}

	// lower numbers are prioritized
	@Override
	public byte getWorkPriority() {
		return 82;
	}

	private int range = 1;

	@Override
	public void init() {
		super.init();
		for (ModuleBase module : getVehicle().getModules()) {
			if (module instanceof ModuleFarmer) {
				range = ((ModuleFarmer) module).getExternalRange();
				break;
			}
		}
	}

	// return true when the work is done, false allow other modules to continue
	// the work
	@Override
	public boolean work() {
		// get the next block so the cart knows where to mine
		BlockPos next = getNextBlock();
		// loop through the blocks in the "hole" in front of the cart
		for (int i = -range; i <= range; i++) {
			for (int j = -range; j <= range; j++) {
				if (hydrate(next.add(i, -1, j))) {
					return true;
				}
			}
		}
		return false;
	}

	private boolean hydrate(BlockPos pos) {
		World world = getVehicle().getWorld();
		IBlockState state = world.getBlockState(pos);
		if (state.getBlock() == Blocks.FARMLAND && state.getValue(BlockFarmland.MOISTURE) != 7) {
			int waterCost = 7 - state.getValue(BlockFarmland.MOISTURE);
			waterCost = getVehicle().drain(FluidRegistry.WATER, waterCost, false);
			if (waterCost > 0) {
				if (doPreWork()) {
					startWorking(2 + waterCost);
					return true;
				} else {
					stopWorking();
					if (!getVehicle().hasCreativeSupplies()) {
						getVehicle().drain(FluidRegistry.WATER, waterCost, true);
					}
					getVehicle().getWorld().setBlockState(pos, state.withProperty(BlockFarmland.MOISTURE, state.getValue(BlockFarmland.MOISTURE) + waterCost), 3);
				}
			}
		}
		return false;
	}
}
