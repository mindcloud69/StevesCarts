package vswe.stevescarts.compat.forestry;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import vswe.stevescarts.api.farms.ITreeModule;

/**
 * Created by modmuss50 on 15/11/16.
 */
public class ForestryTreeModule implements ITreeModule {
	@Override
	public boolean isLeaves(IBlockState blockState, BlockPos pos, EntityMinecart cart) {
		return false;
	}

	@Override
	public boolean isWood(IBlockState blockState, BlockPos pos, EntityMinecart cart) {
		return false;
	}

	@Override
	public boolean isSapling(ItemStack itemStack) {
		return false;
	}
}
