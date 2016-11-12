package vswe.stevescarts.modules;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface ICropModule {
	boolean isSeedValid(ItemStack itemStackd);

	Block getCropFromSeed(ItemStack itemStack);

	boolean isReadyToHarvest(World world, BlockPos pos);
}
