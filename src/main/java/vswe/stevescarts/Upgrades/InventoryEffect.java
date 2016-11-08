package vswe.stevescarts.upgrades;

import java.lang.reflect.Constructor;
import java.util.ArrayList;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import vswe.stevescarts.blocks.tileentities.TileEntityUpgrade;

public abstract class InventoryEffect extends InterfaceEffect {
	protected ArrayList<Slot> slots;

	public InventoryEffect() {
		this.slots = new ArrayList<Slot>();
	}

	public Class<? extends Slot> getSlot(final int id) {
		return Slot.class;
	}

	public void onInventoryChanged(final TileEntityUpgrade upgrade) {
	}

	public abstract int getInventorySize();

	public abstract int getSlotX(final int p0);

	public abstract int getSlotY(final int p0);

	public void addSlot(final Slot slot) {
		this.slots.add(slot);
	}

	public void clear() {
		this.slots.clear();
	}

	public boolean isItemValid(final int slotId, final ItemStack item) {
		return slotId >= 0 && slotId < this.slots.size() && this.slots.get(slotId).isItemValid(item);
	}

	public Slot createSlot(final TileEntityUpgrade upgrade, final int id) {
		try {
			final Class<? extends Slot> slotClass = this.getSlot(id);
			final Constructor slotConstructor = slotClass.getConstructor(IInventory.class, Integer.TYPE, Integer.TYPE, Integer.TYPE);
			final Object slotObject = slotConstructor.newInstance(upgrade, id, this.getSlotX(id), this.getSlotY(id));
			return (Slot) slotObject;
		} catch (Exception e) {
			System.out.println("Failed to create slot! More info below.");
			e.printStackTrace();
			return null;
		}
	}
}
