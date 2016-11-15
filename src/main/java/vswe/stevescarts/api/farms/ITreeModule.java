package vswe.stevescarts.api.farms;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

public interface ITreeModule {
	boolean isLeaves(IBlockState blockState, BlockPos pos, EntityMinecart cart);

	boolean isWood(IBlockState blockState, BlockPos pos, EntityMinecart cart);

	boolean isSapling(ItemStack itemStack);
}
