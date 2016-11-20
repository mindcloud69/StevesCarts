package vswe.stevesvehicles.module.cart.addon.cultivation;

import net.minecraft.block.BlockNetherWart;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import vswe.stevesvehicles.module.cart.ICropModule;
import vswe.stevesvehicles.module.common.addon.ModuleAddon;
import vswe.stevesvehicles.vehicle.VehicleBase;

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
