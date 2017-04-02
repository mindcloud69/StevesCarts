package vswe.stevescarts.compat.minecraft;

import net.minecraft.block.BlockCrops;
import net.minecraft.block.IGrowable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;
import vswe.stevescarts.api.farms.ICropModule;

import javax.annotation.Nonnull;

/**
 * Created by modmuss50 on 15/11/16.
 */
public class DefaultCropModule implements ICropModule {

	@Override
	public boolean isSeedValid(
		@Nonnull
			ItemStack seed) {
		return seed.getItem() == Items.WHEAT_SEEDS || seed.getItem() == Items.POTATO || seed.getItem() == Items.CARROT || seed.getItem() instanceof IPlantable;
	}

	@Override
	public IBlockState getCropFromSeed(
		@Nonnull
			ItemStack seed, World world, BlockPos pos) {
		if (seed.getItem() == Items.CARROT) {
			return Blocks.CARROTS.getDefaultState();
		}
		if (seed.getItem() == Items.POTATO) {
			return Blocks.POTATOES.getDefaultState();
		}
		if (seed.getItem() == Items.WHEAT_SEEDS) {
			return Blocks.WHEAT.getDefaultState();
		}
		if (seed.getItem() instanceof IPlantable) {
			IPlantable plantable = (IPlantable) seed.getItem();
			return plantable.getPlant(world, pos);
		}
		return null;
	}

	@Override
	public boolean isReadyToHarvest(World world, BlockPos pos) {
		IBlockState blockState = world.getBlockState(pos);
		if (blockState.getBlock() instanceof IGrowable) {
			IGrowable growable = (IGrowable) blockState.getBlock();
			if (!growable.canGrow(world, pos, blockState, false)) {
				return true;
			}
		}
		if (blockState.getBlock() instanceof BlockCrops) {
			BlockCrops crops = (BlockCrops) blockState.getBlock();
			if (crops.isMaxAge(blockState)) {
				return true;
			}
		}
		return false;
	}
}
