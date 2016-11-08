package vswe.stevescarts.modules;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

public interface ITreeModule {
	boolean isLeaves(IBlockState blockState, BlockPos pos);

	boolean isWood(IBlockState blockState, BlockPos pos);

	boolean isSapling(ItemStack itemStack);
}
