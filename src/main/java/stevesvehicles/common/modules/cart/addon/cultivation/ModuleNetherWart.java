package stevesvehicles.common.modules.cart.addon.cultivation;

import net.minecraft.block.BlockNetherWart;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import stevesvehicles.common.modules.cart.ICropModule;
import stevesvehicles.common.modules.common.addon.ModuleAddon;
import stevesvehicles.common.vehicles.VehicleBase;

public class ModuleNetherWart extends ModuleAddon implements ICropModule {
	public ModuleNetherWart(VehicleBase vehicleBase) {
		super(vehicleBase);
	}

	@Override
	public boolean isSeedValid(ItemStack seed) {
		return seed.getItem() == Items.NETHER_WART;
	}

	@Override
	public IBlockState getCropFromSeed(ItemStack seed) {
		return Blocks.NETHER_WART.getDefaultState();
	}

	@Override
	public boolean isReadyToHarvest(World world, IBlockState state, BlockPos pos) {
		return state.getBlock() == Blocks.NETHER_WART && state.getValue(BlockNetherWart.AGE) == 3;
	}
}
