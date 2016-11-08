package vswe.stevescarts.containers.slots;

import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class SlotFirework extends SlotBase {
	public SlotFirework(final IInventory iinventory, final int i, final int j, final int k) {
		super(iinventory, i, j, k);
	}

	@Override
	public boolean isItemValid(final ItemStack itemstack) {
		final Item item = itemstack.getItem();
		return item == Items.FIREWORKS || item == Items.GUNPOWDER || item == Items.FIREWORK_CHARGE || item == Items.DYE || item == Items.PAPER || item == Items.GLOWSTONE_DUST || item == Items.DIAMOND || item == Items.FIRE_CHARGE || item == Items.FEATHER || item == Items.GOLD_NUGGET || item == Items.SKULL;
	}
}
