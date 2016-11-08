package vswe.stevescarts.Slots;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import vswe.stevescarts.TileEntities.TileEntityUpgrade;
import vswe.stevescarts.Upgrades.BaseEffect;
import vswe.stevescarts.Upgrades.Disassemble;

public class SlotCartDisassemble extends SlotCart {
	public SlotCartDisassemble(final IInventory iinventory, final int i, final int j, final int k) {
		super(iinventory, i, j, k);
	}

	@Override
	public boolean isItemValid(final ItemStack itemstack) {
		if (this.inventory instanceof TileEntityUpgrade) {
			final TileEntityUpgrade upgrade = (TileEntityUpgrade) this.inventory;
			if (upgrade.getUpgrade() != null) {
				for (final BaseEffect effect : upgrade.getUpgrade().getEffects()) {
					if (effect instanceof Disassemble) {
						return ((Disassemble) effect).canDisassemble(upgrade) == 2 && super.isItemValid(itemstack);
					}
				}
			}
		}
		return false;
	}
}
