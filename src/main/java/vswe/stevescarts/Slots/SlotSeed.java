package vswe.stevescarts.Slots;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import vswe.stevescarts.Modules.Workers.Tools.ModuleFarmer;

public class SlotSeed extends SlotBase {
	private ModuleFarmer module;

	public SlotSeed(final IInventory iinventory, final ModuleFarmer module, final int i, final int j, final int k) {
		super(iinventory, i, j, k);
		this.module = module;
	}

	public boolean isItemValid(final ItemStack itemstack) {
		return this.module.isSeedValidHandler(itemstack);
	}
}
