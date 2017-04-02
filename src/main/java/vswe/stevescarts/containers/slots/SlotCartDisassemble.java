package vswe.stevescarts.containers.slots;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import vswe.stevescarts.blocks.tileentities.TileEntityUpgrade;
import vswe.stevescarts.upgrades.BaseEffect;
import vswe.stevescarts.upgrades.Disassemble;

import javax.annotation.Nonnull;

public class SlotCartDisassemble extends SlotCart {
	public SlotCartDisassemble(final IInventory iinventory, final int i, final int j, final int k) {
		super(iinventory, i, j, k);
	}

	@Override
	public boolean isItemValid(
		@Nonnull
			ItemStack itemstack) {
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
