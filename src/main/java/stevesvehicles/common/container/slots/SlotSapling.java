package stevesvehicles.common.container.slots;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import stevesvehicles.common.modules.cart.tool.ModuleWoodcutter;

public class SlotSapling extends SlotBase {
	private ModuleWoodcutter module;

	public SlotSapling(IInventory iinventory, ModuleWoodcutter module, int id, int x, int y) {
		super(iinventory, id, x, y);
		this.module = module;
	}

	@Override
	public boolean isItemValid(ItemStack itemstack) {
		return module.isSaplingHandler(itemstack);
	}
}
