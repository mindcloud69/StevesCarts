package vswe.stevesvehicles.module.cart;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface ICropModule {
	public boolean isSeedValid(ItemStack seed);
	public IBlockState getCropFromSeed(ItemStack seed);
	public boolean isReadyToHarvest(World world, IBlockState state, BlockPos pos);		
}