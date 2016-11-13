package vswe.stevesvehicles.container.slots;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class SlotFirework extends SlotBase {
	public SlotFirework(IInventory inventory, int id, int x, int y) {
		super(inventory, id, x, y);
	}

	@Override
	public boolean isItemValid(ItemStack itemstack) {
		Item item = itemstack.getItem();

		return 
				item == Items.FIREWORKS ||
				item == Items.GUNPOWDER ||
				item == Items.FIREWORK_CHARGE ||
				item == Items.DYE ||
				item == Items.PAPER ||
				item == Items.GLOWSTONE_DUST ||
				item == Items.DIAMOND ||
				item == Items.FIRE_CHARGE ||
				item == Items.FEATHER ||
				item == Items.GOLD_NUGGET ||
				item == Items.SKULL;
	}
}
