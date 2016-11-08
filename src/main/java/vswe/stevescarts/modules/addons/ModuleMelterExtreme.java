package vswe.stevescarts.modules.addons;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import vswe.stevescarts.entitys.MinecartModular;

public class ModuleMelterExtreme extends ModuleMelter {
	public ModuleMelterExtreme(final MinecartModular cart) {
		super(cart);
	}

	@Override
	protected boolean melt(final Block b, BlockPos pos) {
		if (!super.melt(b, pos)) {
			if (b == Blocks.SNOW) {
				this.getCart().worldObj.setBlockToAir(pos);
				return true;
			}
			if (b == Blocks.ICE) {
				this.getCart().worldObj.setBlockState(pos, Blocks.WATER.getDefaultState());
				return true;
			}
		}
		return false;
	}
}
