package vswe.stevescarts.Modules.Addons;

import net.minecraft.block.Block;
import net.minecraft.block.BlockOre;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.oredict.OreDictionary;
import vswe.stevescarts.Carts.MinecartModular;
import vswe.stevescarts.Modules.Workers.Tools.ModuleDrill;

import java.util.ArrayList;

public class ModuleOreTracker extends ModuleAddon {
	public ModuleOreTracker(final MinecartModular cart) {
		super(cart);
	}

	public BlockPos findBlockToMine(final ModuleDrill drill, final BlockPos start) {
		return this.findBlockToMine(drill, new ArrayList<BlockPos>(), start, true);
	}

	private BlockPos findBlockToMine(final ModuleDrill drill, final ArrayList<BlockPos> checked, final BlockPos current, final boolean first) {
		if (current == null || checked.contains(current) || (!first && !this.isOre(current))) {
			return null;
		}
		checked.add(current);
		if (checked.size() < 200) {
			for (int x = -1; x <= 1; ++x) {
				for (int y = -1; y <= 1; ++y) {
					for (int z = -1; z <= 1; ++z) {
						if (Math.abs(x) + Math.abs(y) + Math.abs(z) == 1) {
							final BlockPos ret = this.findBlockToMine(drill, checked, current.add(x, y, z), false);
							if (ret != null) {
								return ret;
							}
						}
					}
				}
			}
		}
		if (first && !this.isOre(current)) {
			return null;
		}
		if (drill.isValidBlock(current, 0, 1, true) == null) {
			return null;
		}
		return current;
	}

	private boolean isOre(BlockPos pos) {
		final Block b = this.getCart().worldObj.getBlockState(pos).getBlock();
		if (b == null) {
			return false;
		}
		if (b instanceof BlockOre) {
			return true;
		}
		final int[] oreIds = OreDictionary.getOreIDs(new ItemStack(b));
		if (oreIds.length == 0) {
			return false;
		}
		for(int oreId : oreIds){
			final String oreName = OreDictionary.getOreName(oreId);
			if(oreName != null && oreName.startsWith("ore")){
				return true;
			}
		}
		return false;
	}
}
