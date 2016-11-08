package vswe.stevescarts.containers.slots;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import vswe.stevescarts.modules.workers.tools.ModuleWoodcutter;

public class SlotSapling extends SlotBase {
	private ModuleWoodcutter module;

	public SlotSapling(final IInventory iinventory, final ModuleWoodcutter module, final int i, final int j, final int k) {
		super(iinventory, i, j, k);
		this.module = module;
	}

	@Override
	public boolean isItemValid(final ItemStack itemstack) {
		return this.module.isSaplingHandler(itemstack);
	}
}
