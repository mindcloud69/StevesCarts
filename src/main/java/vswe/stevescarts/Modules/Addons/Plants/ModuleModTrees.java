package vswe.stevescarts.Modules.Addons.Plants;

import net.minecraft.block.Block;
import net.minecraft.block.BlockSapling;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.oredict.OreDictionary;
import vswe.stevescarts.Carts.MinecartModular;
import vswe.stevescarts.Modules.Addons.ModuleAddon;
import vswe.stevescarts.Modules.ITreeModule;

public class ModuleModTrees extends ModuleAddon implements ITreeModule {
	public ModuleModTrees(final MinecartModular cart) {
		super(cart);
	}

	@Override
	public boolean isLeaves(IBlockState blockState, BlockPos pos) {
		return blockState.getBlock().isLeaves(blockState, (IBlockAccess) this.getCart().worldObj, pos);
	}

	@Override
	public boolean isWood(IBlockState blockState, BlockPos pos) {
		return blockState.getBlock().isWood((IBlockAccess) this.getCart().worldObj, pos);
	}

	@Override
	public boolean isSapling(final ItemStack sapling) {
		if (sapling != null) {
			if (this.isStackSapling(sapling)) {
				return true;
			}
			if (sapling.getItem() instanceof ItemBlock) {
				final Block b = Block.getBlockFromItem(sapling.getItem());
				return b instanceof BlockSapling || (b != null && this.isStackSapling(new ItemStack(b, 1, 32767)));
			}
		}
		return false;
	}

	private boolean isStackSapling(final ItemStack sapling) {
		final int[] ids = OreDictionary.getOreIDs(sapling);
		for(int id : ids){
			final String name = OreDictionary.getOreName(id);
			if(name != null && name.startsWith("treeSapling")){
				return true;
			}
		}
		return false;
	}
}
