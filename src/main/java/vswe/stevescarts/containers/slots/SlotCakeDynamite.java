package vswe.stevescarts.containers.slots;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import vswe.stevescarts.items.ModItems;

import javax.annotation.Nonnull;

public class SlotCakeDynamite extends SlotCake implements ISlotExplosions {
	public SlotCakeDynamite(final IInventory iinventory, final int i, final int j, final int k) {
		super(iinventory, i, j, k);
	}

	@Override
	public boolean isItemValid(
		@Nonnull
			ItemStack itemstack) {
		return super.isItemValid(itemstack) || (itemstack != null && itemstack.getItem() == ModItems.component && itemstack.getItemDamage() == 6);
	}
}
