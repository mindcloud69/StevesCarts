package vswe.stevescarts.modules.addons.plants;

import net.minecraft.block.BlockNetherWart;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import vswe.stevescarts.api.farms.ICropModule;
import vswe.stevescarts.entitys.EntityMinecartModular;
import vswe.stevescarts.modules.addons.ModuleAddon;

import javax.annotation.Nonnull;

public class ModuleNetherwart extends ModuleAddon implements ICropModule {
	public ModuleNetherwart(final EntityMinecartModular cart) {
		super(cart);
	}

	@Override
	public boolean isSeedValid(
		@Nonnull
			ItemStack seed) {
		return seed.getItem() == Items.NETHER_WART;
	}

	@Override
	public IBlockState getCropFromSeed(
		@Nonnull
			ItemStack seed, World world, BlockPos pos) {
		return Blocks.NETHER_WART.getDefaultState();
	}

	@Override
	public boolean isReadyToHarvest(World world, BlockPos pos) {
		IBlockState blockState = world.getBlockState(pos);
		return blockState.getBlock() == Blocks.NETHER_WART && blockState.getValue(BlockNetherWart.AGE) == 3;
	}
}
