package vswe.stevesvehicles.container.slots;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public class SlotFertilizer extends SlotBase {
	public SlotFertilizer(IInventory iinventory, int i, int j, int k) {
		super(iinventory, i, j, k);
	}

	@Override
	public boolean isItemValid(ItemStack itemstack) {
		return itemstack.getItem() == Items.BONE || (itemstack.getItem() == Items.DYE && itemstack.getItemDamage() == 15);
	}
}
