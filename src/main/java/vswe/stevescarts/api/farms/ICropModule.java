package vswe.stevescarts.api.farms;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface ICropModule {
	boolean isSeedValid(ItemStack itemStack);

	IBlockState getCropFromSeed(ItemStack itemStack, World world, BlockPos pos);

	boolean isReadyToHarvest(World world, BlockPos pos);
}
