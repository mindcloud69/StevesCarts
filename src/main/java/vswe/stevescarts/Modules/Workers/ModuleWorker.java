package vswe.stevescarts.Modules.Workers;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRailBase;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fluids.IFluidBlock;
import vswe.stevescarts.Carts.MinecartModular;
import vswe.stevescarts.Modules.ModuleBase;

public abstract class ModuleWorker extends ModuleBase {
	private boolean preWork;
	private boolean shouldDie;

	public ModuleWorker(final MinecartModular cart) {
		super(cart);
		this.preWork = true;
	}

	public abstract byte getWorkPriority();

	public abstract boolean work();

	protected void startWorking(final int time) {
		this.getCart().setWorkingTime(time);
		this.preWork = false;
		this.getCart().setWorker(this);
	}

	public void stopWorking() {
		if (this.getCart().getWorker() == this) {
			this.preWork = true;
			this.getCart().setWorker(null);
		}
	}

	public boolean preventAutoShutdown() {
		return false;
	}

	public void kill() {
		this.shouldDie = true;
	}

	public boolean isDead() {
		return this.shouldDie;
	}

	public void revive() {
		this.shouldDie = false;
	}

	protected boolean doPreWork() {
		return this.preWork;
	}

	public BlockPos getLastblock() {
		return this.getNextblock(false);
	}

	public BlockPos getNextblock() {
		return this.getNextblock(true);
	}

	private BlockPos getNextblock(final boolean flag) {
		BlockPos pos = getCart().getPosition();
		if (BlockRailBase.isRailBlock(getCart().worldObj, pos.down())) {
			pos = pos.down();
		}
		IBlockState blockState = getCart().worldObj.getBlockState(pos);
		if (BlockRailBase.isRailBlock(blockState)) {
			int meta = ((BlockRailBase) blockState.getBlock()).getRailDirection(getCart().worldObj, pos, blockState, getCart()).getMetadata();
			if (meta >= 2 && meta <= 5) {
				pos = pos.up();
			}
			final int[][] logic = MinecartModular.railDirectionCoordinates[meta];
			final double pX = this.getCart().pushX;
			final double pZ = this.getCart().pushZ;
			final boolean xDir = (pX > 0.0 && logic[0][0] > 0) || pX == 0.0 || logic[0][0] == 0 || (pX < 0.0 && logic[0][0] < 0);
			final boolean zDir = (pZ > 0.0 && logic[0][2] > 0) || pZ == 0.0 || logic[0][2] == 0 || (pZ < 0.0 && logic[0][2] < 0);
			final int dir = ((xDir && zDir) != flag) ? 1 : 0;
			return pos.add(logic[dir][0], logic[dir][1], logic[dir][2]);
		}
		return pos;
	}

	@Override
	public float getMaxSpeed() {
		if (!this.doPreWork()) {
			return 0.0f;
		}
		return super.getMaxSpeed();
	}

	protected boolean isValidForTrack(BlockPos pos, final boolean flag) {
		boolean result = this.countsAsAir(pos) && (!flag || getCart().worldObj.isSideSolid(pos.down(), EnumFacing.UP));
		if (result) {
			final int coordX = pos.getX() - (this.getCart().x() - pos.getX());
			final int coordZ = pos.getY() - (this.getCart().z() - pos.getY());
			final Block block = this.getCart().worldObj.getBlockState(new BlockPos(coordX, pos.getY(), coordZ)).getBlock();
			final boolean isWater = block == Blocks.WATER || block == Blocks.FLOWING_WATER || block == Blocks.ICE;
			final boolean isLava = block == Blocks.LAVA || block == Blocks.FLOWING_LAVA;
			final boolean isOther = block != null && block instanceof IFluidBlock;
			final boolean isLiquid = isWater || isLava || isOther;
			result = !isLiquid;
		}
		return result;
	}
}
