package vswe.stevesvehicles.module.cart.attachment;

import net.minecraft.block.BlockRailBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import vswe.stevesvehicles.module.cart.ModuleWorker;
import vswe.stevesvehicles.vehicle.VehicleBase;

public class ModuleRemover extends ModuleWorker {
	public ModuleRemover(VehicleBase vehicleBase) {
		super(vehicleBase);
	}

	// lower numbers are prioritized
	@Override
	public byte getWorkPriority() {
		return 120;
	}

	@Override
	protected boolean preventTurnBack() {
		return true;
	}

	private BlockPos remove = BlockPos.ORIGIN.add(0, -1, 0);

	// return true when the work is done, false allow other modules to continue
	// the work
	@Override
	public boolean work() {
		World world = getVehicle().getWorld();
		if (remove.getY() != -1 && !(remove.getX() == getVehicle().x() && remove.getY() == getVehicle().z())) {
			if (removeRail(world, remove, true)) {
				return false;
			}
		}
		BlockPos next = getNextBlock();
		BlockPos last = getLastBlock();
		boolean front = isRailAtLocation(world, next);
		boolean back = isRailAtLocation(world, last);
		if (!front) {
			if (back) {
				turnback();
				if (removeRail(world, getVehicle().pos(), false)) {
					return true;
				}
			} else {
				// out of rails to remove
			}
		} else if (!back) {
			if (removeRail(world, getVehicle().pos(), false)) {
				return true;
			}
		}
		return false;
	}

	private boolean isRailAtLocation(World world, BlockPos coordinates) {
		return BlockRailBase.isRailBlock(world, coordinates.up()) || BlockRailBase.isRailBlock(world, coordinates) || BlockRailBase.isRailBlock(world, coordinates.down());
	}

	private boolean removeRail(World world, BlockPos coordinates, boolean flag) {
		if (flag) {
			if (BlockRailBase.isRailBlock(getVehicle().getWorld(), coordinates) && world.getBlockState(coordinates).getBlock() == Blocks.RAIL) {
				if (!doPreWork()) {
					ItemStack item = new ItemStack(Blocks.RAIL, 1);
					getVehicle().addItemToChest(item);
					if (item.getCount() == 0) {
						getVehicle().getWorld().setBlockToAir(coordinates);
					}
					remove = new BlockPos(remove.getX(), -1, remove.getZ());
				} else {
					startWorking(12);
					return true;
				}
			} else {
				remove = new BlockPos(remove.getX(), -1, remove.getZ());
			}
		} else {
			if (BlockRailBase.isRailBlock(getVehicle().getWorld(), coordinates.down())) {
				remove = coordinates.down();
			} else if (BlockRailBase.isRailBlock(getVehicle().getWorld(), coordinates)) {
				remove = coordinates;
			} else if (BlockRailBase.isRailBlock(getVehicle().getWorld(), coordinates.up())) {
				remove = coordinates.up();
			}
		}
		stopWorking();
		return false;
	}
}
