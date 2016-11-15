package vswe.stevesvehicles.module.cart.tool;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import vswe.stevesvehicles.module.ModuleBase;
import vswe.stevesvehicles.module.cart.ModuleWorker;
import vswe.stevesvehicles.module.common.addon.enchanter.EnchantmentInfo;
import vswe.stevesvehicles.module.common.addon.enchanter.ModuleEnchants;
import vswe.stevesvehicles.vehicle.VehicleBase;

public abstract class ModuleTool extends ModuleWorker {
	public ModuleTool(VehicleBase vehicleBase) {
		super(vehicleBase);
	}

	protected ModuleEnchants enchanter;

	@Override
	public void init() {
		super.init();
		for (ModuleBase module : getVehicle().getModules()) {
			if (module instanceof ModuleEnchants) {
				enchanter = (ModuleEnchants) module;
				enchanter.addType(EnchantmentInfo.Enchantment_Type.TOOL);
				break;
			}
		}
	}

	public boolean shouldSilkTouch(IBlockState state, BlockPos pos) {
		boolean doSilkTouch = false;
		// try-catch here just because I need to give it a null player, and
		// other mods might assume that they actually get a player, I don't
		// know.
		try {
			if (enchanter != null && enchanter.useSilkTouch() && state.getBlock().canSilkHarvest(getVehicle().getWorld(), pos, state, null)) {
				return true;
			}
		} catch (Exception ignored) {
		}
		return false;
	}

	public ItemStack getSilkTouchedItem(IBlockState state) {
		ItemStack stack = new ItemStack(state.getBlock(), 1, 0);
		if (stack.getItem() != null && stack.getItem().getHasSubtypes()) {
			return new ItemStack(stack.getItem(), 1, state.getBlock().getMetaFromState(state));
		} else {
			return stack;
		}
	}

	protected void damageTool(int val) {
		if (enchanter != null) {
			enchanter.damageEnchant(EnchantmentInfo.Enchantment_Type.TOOL, val);
		}
	}
}
