package vswe.stevescarts.Slots;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import vswe.stevescarts.Modules.Realtimers.ModuleShooter;

public class SlotArrow extends SlotBase {
	private ModuleShooter shooter;

	public SlotArrow(final IInventory iinventory, final ModuleShooter shooter, final int i, final int j, final int k) {
		super(iinventory, i, j, k);
		this.shooter = shooter;
	}

	@Override
	public boolean isItemValid(final ItemStack itemstack) {
		return this.shooter.isValidProjectileItem(itemstack);
	}
}
