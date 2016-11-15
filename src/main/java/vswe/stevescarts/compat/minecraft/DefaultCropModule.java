package vswe.stevescarts.compat.minecraft;

import net.minecraft.block.Block;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import vswe.stevescarts.api.farms.ICropModule;

/**
 * Created by modmuss50 on 15/11/16.
 */
public class DefaultCropModule implements ICropModule {

	@Override
	public boolean isSeedValid(final ItemStack seed) {
		return seed.getItem() == Items.WHEAT_SEEDS || seed.getItem() == Items.POTATO || seed.getItem() == Items.CARROT;
	}

	@Override
	public Block getCropFromSeed(final ItemStack seed) {
		if (seed.getItem() == Items.CARROT) {
			return Blocks.CARROTS;
		}
		if (seed.getItem() == Items.POTATO) {
			return Blocks.POTATOES;
		}
		if (seed.getItem() == Items.WHEAT_SEEDS) {
			return Blocks.WHEAT;
		}
		return null;
	}

	@Override
	public boolean isReadyToHarvest(World world, BlockPos pos) {
		IBlockState blockState = world.getBlockState(pos);
		return blockState.getBlock() instanceof BlockCrops && blockState.getValue(BlockCrops.AGE) == 7;
	}
}
