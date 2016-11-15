package vswe.stevesvehicles.module.cart.addon;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.block.BlockOre;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.oredict.OreDictionary;

import vswe.stevesvehicles.module.cart.tool.ModuleDrill;
import vswe.stevesvehicles.module.common.addon.ModuleAddon;
import vswe.stevesvehicles.vehicle.VehicleBase;

public class ModuleOreTracker extends ModuleAddon {
	public ModuleOreTracker(VehicleBase vehicleBase) {
		super(vehicleBase);
	}

	public BlockPos findBlockToMine(ModuleDrill drill, BlockPos start) {
		return findBlockToMine(drill, new ArrayList<BlockPos>(), start, true);
	}

	private BlockPos findBlockToMine(ModuleDrill drill, ArrayList<BlockPos> checked, BlockPos current, boolean first) {
		if (current == null || checked.contains(current) || (!first && !isOre(current))) {
			return null;
		}
		checked.add(current);
		if (checked.size() < 200) {
			for (int x = -1; x <= 1; x++) {
				for (int y = -1; y <= 1; y++) {
					for (int z = -1; z <= 1; z++) {
						if (Math.abs(x) + Math.abs(y) + Math.abs(z) == 1) {
							BlockPos ret = findBlockToMine(drill, checked, current.add(x, y, z), false);
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
		if (drill.isValidBlock(current, 0, 1, true) == null) {
			return null;
		}
		return current;
	}

	private boolean isOre(BlockPos pos) {
		Block block = getVehicle().getWorld().getBlockState(pos).getBlock();
		if (block != null) {
			if (block instanceof BlockOre) {
				return true;
			} else {
				ItemStack item = new ItemStack(block);
				if (item.getItem() != null) {
					int[] oreIds = OreDictionary.getOreIDs(item);
					for (int oreId : oreIds) {
						if (oreId == -1) {
							return false;
						} else {
							String oreName = OreDictionary.getOreName(oreId);
							if (oreName != null && oreName.toLowerCase().startsWith("ore")) {
								return true;
							}
						}
					}
				}
			}
		}
		return false;
	}
}
