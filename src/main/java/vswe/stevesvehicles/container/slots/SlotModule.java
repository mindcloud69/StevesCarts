package vswe.stevesvehicles.container.slots;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import vswe.stevesvehicles.item.ModItems;
public class SlotModule extends Slot {
	public SlotModule(IInventory inventory, int id, int x, int y) {
		super(inventory, id, x, y);
	}

	@Override
	public boolean isItemValid(ItemStack itemstack) {
		return itemstack.getItem() == ModItems.modules;
	}
}