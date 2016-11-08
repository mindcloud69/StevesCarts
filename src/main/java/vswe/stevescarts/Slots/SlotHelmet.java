package vswe.stevescarts.Slots;

import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;

public class SlotHelmet extends SlotBase {
	public SlotHelmet(final IInventory iinventory, final int i, final int j, final int k) {
		super(iinventory, i, j, k);
	}

	public boolean isItemValid(final ItemStack itemstack) {
		return itemstack.getItem() instanceof ItemArmor && ((ItemArmor) itemstack.getItem()).armorType == EntityEquipmentSlot.HEAD;
	}
}
