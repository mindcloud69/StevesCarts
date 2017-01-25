package vswe.stevescarts.modules.addons;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import vswe.stevescarts.entitys.EntityMinecartModular;

public class ModuleMelterExtreme extends ModuleMelter {
	public ModuleMelterExtreme(final EntityMinecartModular cart) {
		super(cart);
	}

	@Override
	protected boolean melt(final Block b, BlockPos pos) {
		if (!super.melt(b, pos)) {
			if (b == Blocks.SNOW) {
				this.getCart().world.setBlockToAir(pos);
				return true;
			}
			if (b == Blocks.ICE) {
				this.getCart().world.setBlockState(pos, Blocks.WATER.getDefaultState());
				return true;
			}
		}
		return false;
	}
}
