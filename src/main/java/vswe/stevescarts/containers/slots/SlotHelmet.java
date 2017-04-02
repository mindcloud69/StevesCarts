package vswe.stevescarts.containers.slots;

import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public class SlotHelmet extends SlotBase {
	public SlotHelmet(final IInventory iinventory, final int i, final int j, final int k) {
		super(iinventory, i, j, k);
	}

	@Override
	public boolean isItemValid(
		@Nonnull
			ItemStack itemstack) {
		return itemstack.getItem() instanceof ItemArmor && ((ItemArmor) itemstack.getItem()).armorType == EntityEquipmentSlot.HEAD;
	}
}
