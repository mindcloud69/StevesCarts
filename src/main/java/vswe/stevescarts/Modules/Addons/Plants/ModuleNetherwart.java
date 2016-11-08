package vswe.stevescarts.Modules.Addons.Plants;

import net.minecraft.block.Block;
import net.minecraft.block.BlockNetherWart;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import vswe.stevescarts.Carts.MinecartModular;
import vswe.stevescarts.Modules.Addons.ModuleAddon;
import vswe.stevescarts.Modules.ICropModule;

public class ModuleNetherwart extends ModuleAddon implements ICropModule {
	public ModuleNetherwart(final MinecartModular cart) {
		super(cart);
	}

	@Override
	public boolean isSeedValid(final ItemStack seed) {
		return seed.getItem() == Items.NETHER_WART;
	}

	@Override
	public Block getCropFromSeed(final ItemStack seed) {
		return Blocks.NETHER_WART;
	}

	@Override
	public boolean isReadyToHarvest(BlockPos pos) {
		IBlockState blockState = getCart().worldObj.getBlockState(pos);
		return blockState.getBlock() == Blocks.NETHER_WART && blockState.getValue(BlockNetherWart.AGE) == 3;
	}
}
