package vswe.stevescarts.modules.addons;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import vswe.stevescarts.entitys.EntityMinecartModular;

public class ModuleMelter extends ModuleAddon {
	private int tick;

	public ModuleMelter(final EntityMinecartModular cart) {
		super(cart);
	}

	@Override
	public void update() {
		super.update();
		if (getCart().world.isRemote) {
			return;
		}
		if (getCart().hasFuel()) {
			if (tick >= getInterval()) {
				tick = 0;
				melt();
			} else {
				++tick;
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
		for (int x = -getBlocksOnSide(); x <= getBlocksOnSide(); ++x) {
			for (int z = -getBlocksOnSide(); z <= getBlocksOnSide(); ++z) {
				for (int y = -getBlocksFromLevel(); y <= getBlocksFromLevel(); ++y) {
					BlockPos pos = cartPos.add(x, y, z);
					final Block b = getCart().world.getBlockState(cartPos).getBlock();
					melt(b, cartPos);
				}
			}
		}
	}

	protected boolean melt(final Block b, BlockPos pos) {
		if (b == Blocks.SNOW) {
			getCart().world.setBlockToAir(pos);
			return true;
		}
		return false;
	}
}
