package vswe.stevesvehicles.module.cart.addon.cultivation;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSapling;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;

import vswe.stevesvehicles.module.cart.ITreeModule;
import vswe.stevesvehicles.module.common.addon.ModuleAddon;
import vswe.stevesvehicles.vehicle.VehicleBase;

public class ModuleModTrees extends ModuleAddon implements ITreeModule {

	public ModuleModTrees(VehicleBase vehicleBase) {
		super(vehicleBase);
	}

	@Override
	public boolean isLeaves(World world, IBlockState state, BlockPos pos) {
		return state.getBlock().isLeaves(state, world, pos);
	}

	@Override
	public boolean isWood(World world, IBlockState state, BlockPos pos) {
		return state.getBlock().isWood(world, pos);
	}
	@Override
	public boolean isSapling(ItemStack sapling) {
		if (sapling != null /*&& sapling.getItem() instanceof ItemBlock*/) {

			if (isStackSapling(sapling)) {
				return true;
			}else if (sapling.getItem() instanceof ItemBlock){
				Block b = Block.getBlockFromItem(sapling.getItem());

				if (b instanceof BlockSapling) {
					return true;
				}

				return b != null && isStackSapling(new ItemStack(b, 1, OreDictionary.WILDCARD_VALUE));
			}
		}

		return false;

	}	

	private boolean isStackSapling(ItemStack sapling) {
		int[] ids = OreDictionary.getOreIDs(sapling);
		for(int id : ids){
			String name = OreDictionary.getOreName(id);
			if(name != null && name.startsWith("treeSapling")){
				return true;	
			}
		}
		return false;
	}


}