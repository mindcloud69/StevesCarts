package vswe.stevescarts.modules.addons.plants;

import net.minecraft.block.Block;
import net.minecraft.block.BlockSapling;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.oredict.OreDictionary;
import vswe.stevescarts.entitys.MinecartModular;
import vswe.stevescarts.modules.ITreeModule;
import vswe.stevescarts.modules.addons.ModuleAddon;

public class ModuleModTrees extends ModuleAddon implements ITreeModule {
	public ModuleModTrees(final MinecartModular cart) {
		super(cart);
	}

	@Override
	public boolean isLeaves(IBlockState blockState, BlockPos pos) {
		return blockState.getBlock().isLeaves(blockState, this.getCart().worldObj, pos);
	}

	@Override
	public boolean isWood(IBlockState blockState, BlockPos pos) {
		return blockState.getBlock().isWood(this.getCart().worldObj, pos);
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
