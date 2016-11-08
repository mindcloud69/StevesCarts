package vswe.stevescarts.containers.slots;

import java.util.ArrayList;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import vswe.stevescarts.helpers.EnchantmentInfo;

public class SlotEnchantment extends SlotBase {
	private ArrayList<EnchantmentInfo.ENCHANTMENT_TYPE> enabledTypes;

	public SlotEnchantment(final IInventory iinventory, final ArrayList<EnchantmentInfo.ENCHANTMENT_TYPE> enabledTypes, final int i, final int j, final int k) {
		super(iinventory, i, j, k);
		this.enabledTypes = enabledTypes;
	}

	@Override
	public boolean isItemValid(final ItemStack itemstack) {
		return EnchantmentInfo.isItemValid(this.enabledTypes, itemstack);
	}
}
