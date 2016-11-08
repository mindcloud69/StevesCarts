package vswe.stevescarts.modules.addons;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import vswe.stevescarts.entitys.MinecartModular;

public class ModuleMelter extends ModuleAddon {
	private int tick;

	public ModuleMelter(final MinecartModular cart) {
		super(cart);
	}

	@Override
	public void update() {
		super.update();
		if (this.getCart().worldObj.isRemote) {
			return;
		}
		if (this.getCart().hasFuel()) {
			if (this.tick >= this.getInterval()) {
				this.tick = 0;
				this.melt();
			} else {
				++this.tick;
			}
		}
	}

	protected int getInterval() {
		return 70;
	}

	protected int getBlocksOnSide() {
		return 7;
	}

	protected int getBlocksFromLevel() {
		return 1;
	}

	private void melt() {
		BlockPos cartPos = getCart().getPosition();
		for (int x = -this.getBlocksOnSide(); x <= this.getBlocksOnSide(); ++x) {
			for (int z = -this.getBlocksOnSide(); z <= this.getBlocksOnSide(); ++z) {
				for (int y = -this.getBlocksFromLevel(); y <= this.getBlocksFromLevel(); ++y) {
					BlockPos pos = cartPos.add(x, y, z);
					final Block b = this.getCart().worldObj.getBlockState(cartPos).getBlock();
					this.melt(b, cartPos);
				}
			}
		}
	}

	protected boolean melt(final Block b, BlockPos pos) {
		if (b == Blocks.SNOW) {
			this.getCart().worldObj.setBlockToAir(pos);
			return true;
		}
		return false;
	}
}
