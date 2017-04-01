package vswe.stevescarts.modules.workers;

import javax.annotation.Nonnull;

import net.minecraft.block.BlockRailBase;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import vswe.stevescarts.entitys.EntityMinecartModular;

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
		if (remove.getY() != -1 && (remove.getX() != cart.x() || remove.getZ() != cart.z()) && this.removeRail(world, remove, true)) {
			return false;
		}
		BlockPos next = this.getNextblock();
		BlockPos last = this.getLastblock();
		final boolean front = this.isRailAtCoords(world, next);
		final boolean back = this.isRailAtCoords(world, last);
		if (!front) {
			if (back) {
				this.turnback();
				if (this.removeRail(world, cart.getPosition(), false)) {
					return true;
				}
			}
		} else if (!back && this.removeRail(world, cart.getPosition(), false)) {
			return true;
		}
		return false;
	}

	private boolean isRailAtCoords(World world, BlockPos coords) {
		return BlockRailBase.isRailBlock(world, coords.up()) || BlockRailBase.isRailBlock(this.getCart().world, coords) || BlockRailBase.isRailBlock(this.getCart().world, coords.down());
	}

	private boolean removeRail(World world, BlockPos pos, final boolean flag) {
		if (flag) {
			IBlockState blockState = world.getBlockState(pos);
			if (BlockRailBase.isRailBlock(blockState) && blockState.getBlock() == Blocks.RAIL) {
				if (this.doPreWork()) {
					this.startWorking(12);
					return true;
				}
				final int rInt = this.getCart().rand.nextInt(100);
				@Nonnull ItemStack iStack = new ItemStack(Blocks.RAIL, 1, 0);
				this.getCart().addItemToChest(iStack);
				if (iStack.stackSize == 0) {
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
		this.stopWorking();
		return false;
	}
}
