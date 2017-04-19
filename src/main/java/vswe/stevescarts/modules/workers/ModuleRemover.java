package vswe.stevescarts.modules.workers;

import net.minecraft.block.BlockRailBase;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import vswe.stevescarts.entitys.EntityMinecartModular;

import javax.annotation.Nonnull;

public class ModuleRemover extends ModuleWorker {
	@Nonnull
	private BlockPos remove;

	public ModuleRemover(final EntityMinecartModular cart) {
		super(cart);
		remove = new BlockPos(0, -1, 0);
	}

	@Override
	public byte getWorkPriority() {
		return 120;
	}

	@Override
	protected boolean preventTurnback() {
		return true;
	}

	@Override
	public boolean work() {
		EntityMinecartModular cart = getCart();
		World world = cart.world;
		if (remove.getY() != -1 && (remove.getX() != cart.x() || remove.getZ() != cart.z()) && removeRail(world, remove, true)) {
			return false;
		}
		BlockPos next = getNextblock();
		BlockPos last = getLastblock();
		final boolean front = isRailAtCoords(world, next);
		final boolean back = isRailAtCoords(world, last);
		if (!front) {
			if (back) {
				turnback();
				if (removeRail(world, cart.getPosition(), false)) {
					return true;
				}
			}
		} else if (!back && removeRail(world, cart.getPosition(), false)) {
			return true;
		}
		return false;
	}

	private boolean isRailAtCoords(World world, BlockPos coords) {
		return BlockRailBase.isRailBlock(world, coords.up()) || BlockRailBase.isRailBlock(getCart().world, coords) || BlockRailBase.isRailBlock(getCart().world, coords.down());
	}

	private boolean removeRail(World world, BlockPos pos, final boolean flag) {
		if (flag) {
			IBlockState blockState = world.getBlockState(pos);
			if (BlockRailBase.isRailBlock(blockState) && blockState.getBlock() == Blocks.RAIL) {
				if (doPreWork()) {
					startWorking(12);
					return true;
				}
				final int rInt = getCart().rand.nextInt(100);
				@Nonnull
				ItemStack iStack = new ItemStack(Blocks.RAIL, 1, 0);
				getCart().addItemToChest(iStack);
				if (iStack.getCount() == 0) {
					world.setBlockToAir(pos);
				}
				remove = new BlockPos(pos.getX(), -1, pos.getZ());
			} else {
				remove = new BlockPos(pos.getX(), -1, pos.getZ());
			}
		} else if (BlockRailBase.isRailBlock(world, pos.down())) {
			remove = pos.down();
		} else if (BlockRailBase.isRailBlock(world, pos)) {
			remove = pos;
		} else if (BlockRailBase.isRailBlock(world, pos.up())) {
			remove = pos.up();
		}
		stopWorking();
		return false;
	}
}
