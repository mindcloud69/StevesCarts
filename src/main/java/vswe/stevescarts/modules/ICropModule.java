package vswe.stevescarts.modules;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

public interface ICropModule {
	boolean isSeedValid(final ItemStack p0);

	Block getCropFromSeed(final ItemStack p0);

	boolean isReadyToHarvest(BlockPos pos);
}
