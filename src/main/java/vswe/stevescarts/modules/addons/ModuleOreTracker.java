package vswe.stevescarts.modules.addons;

import net.minecraft.block.Block;
import net.minecraft.block.BlockOre;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.oredict.OreDictionary;
import vswe.stevescarts.entitys.EntityMinecartModular;
import vswe.stevescarts.modules.workers.tools.ModuleDrill;

import java.util.ArrayList;

public class ModuleOreTracker extends ModuleAddon {
	public ModuleOreTracker(final EntityMinecartModular cart) {
		super(cart);
	}

	public BlockPos findBlockToMine(final ModuleDrill drill, final BlockPos start) {
		return findBlockToMine(drill, new ArrayList<>(), start, true);
	}

	private BlockPos findBlockToMine(final ModuleDrill drill, final ArrayList<BlockPos> checked, final BlockPos current, final boolean first) {
		if (current == null || checked.contains(current) || (!first && !isOre(current))) {
			return null;
		}
		checked.add(current);
		if (checked.size() < 200) {
			for (int x = -1; x <= 1; ++x) {
				for (int y = -1; y <= 1; ++y) {
					for (int z = -1; z <= 1; ++z) {
						if (Math.abs(x) + Math.abs(y) + Math.abs(z) == 1) {
							final BlockPos ret = findBlockToMine(drill, checked, current.add(x, y, z), false);
							if (ret != null) {
								return ret;
							}
						}
					}
				}
			}
		}
		if (first && !isOre(current)) {
			return null;
		}
		if (drill.isValidBlock(getCart().world, current, 0, 1, true) == null) {
			return null;
		}
		return current;
	}

	private boolean isOre(BlockPos pos) {
		final Block b = getCart().world.getBlockState(pos).getBlock();
		if (b == null || b == Blocks.AIR) {
			return false;
		}
		if (b instanceof BlockOre) {
			return true;
		}
		ItemStack stack = new ItemStack(b);
		if (stack == null || stack.getItem() == null) {
			return false;
		}
		final int[] oreIds = OreDictionary.getOreIDs(stack);
		if (oreIds.length == 0) {
			return false;
		}
		for (int oreId : oreIds) {
			final String oreName = OreDictionary.getOreName(oreId);
			if (oreName != null && oreName.startsWith("ore")) {
				return true;
			}
		}
		return false;
	}
}
