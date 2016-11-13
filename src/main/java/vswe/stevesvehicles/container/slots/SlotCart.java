package vswe.stevesvehicles.container.slots;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import vswe.stevesvehicles.item.ModItems;
import vswe.stevesvehicles.vehicle.VehicleBase;

public class SlotCart extends Slot {
	public SlotCart(IInventory inventory, int id, int x, int y) {
		super(inventory, id, x, y);
	}

	@Override
	public boolean isItemValid(ItemStack itemstack) {
		return itemstack != null && itemstack.getItem() == ModItems.vehicles && itemstack.getTagCompound() != null && !itemstack.getTagCompound().hasKey(VehicleBase.NBT_INTERRUPT_MAX_TIME);
	}

	@Override
	public int getSlotStackLimit() {
		return 1;
	}
}