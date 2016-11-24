package stevesvehicles.common.modules.cart;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface ITreeModule {
	public boolean isLeaves(World world, IBlockState state, BlockPos pos);

	public boolean isWood(World world, IBlockState state, BlockPos pos);

	public boolean isSapling(ItemStack sapling);
}
