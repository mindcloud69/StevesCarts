package vswe.stevescarts.Modules.Workers;

import net.minecraft.block.BlockRailBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import vswe.stevescarts.Carts.MinecartModular;

public class ModuleRemover extends ModuleWorker {
	private int removeX;
	private int removeY;
	private int removeZ;

	public ModuleRemover(final MinecartModular cart) {
		super(cart);
		this.removeY = -1;
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
		if (this.removeY != -1 && (this.removeX != this.getCart().x() || this.removeZ != this.getCart().z()) && this.removeRail(this.removeX, this.removeY, this.removeZ, true)) {
			return false;
		}
		BlockPos next = this.getNextblock();
		BlockPos last = this.getLastblock();
		final boolean front = this.isRailAtCoords(next);
		final boolean back = this.isRailAtCoords(last);
		if (!front) {
			if (back) {
				this.turnback();
				if (this.removeRail(this.getCart().x(), this.getCart().y(), this.getCart().z(), false)) {
					return true;
				}
			}
		} else if (!back && this.removeRail(this.getCart().x(), this.getCart().y(), this.getCart().z(), false)) {
			return true;
		}
		return false;
	}

	private boolean isRailAtCoords(BlockPos coords) {
		return BlockRailBase.isRailBlock(this.getCart().worldObj, coords.up()) || BlockRailBase.isRailBlock(this.getCart().worldObj, coords) || BlockRailBase.isRailBlock(this.getCart().worldObj, coords.down());
	}

	private boolean removeRail(final int x, final int y, final int z, final boolean flag) {
		if (flag) {
			if (BlockRailBase.isRailBlock(this.getCart().worldObj, new BlockPos(x, y, z)) && this.getCart().worldObj.getBlockState(new BlockPos(x, y, z)).getBlock() == Blocks.RAIL) {
				if (this.doPreWork()) {
					this.startWorking(12);
					return true;
				}
				final int rInt = this.getCart().rand.nextInt(100);
				final ItemStack iStack = new ItemStack(Blocks.RAIL, 1, 0);
				this.getCart().addItemToChest(iStack);
				if (iStack.stackSize == 0) {
					this.getCart().worldObj.setBlockToAir(new BlockPos(x, y, z));
				}
				this.removeY = -1;
			} else {
				this.removeY = -1;
			}
		} else if (BlockRailBase.isRailBlock(this.getCart().worldObj, new BlockPos(x, y - 1, z))) {
			this.removeX = x;
			this.removeY = y - 1;
			this.removeZ = z;
		} else if (BlockRailBase.isRailBlock(this.getCart().worldObj, new BlockPos(x, y, z))) {
			this.removeX = x;
			this.removeY = y;
			this.removeZ = z;
		} else if (BlockRailBase.isRailBlock(this.getCart().worldObj, new BlockPos(x, y, z))) {
			this.removeX = x;
			this.removeY = y + 1;
			this.removeZ = z;
		}
		this.stopWorking();
		return false;
	}
}
