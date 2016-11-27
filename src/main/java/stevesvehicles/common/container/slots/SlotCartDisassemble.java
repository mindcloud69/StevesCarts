package stevesvehicles.common.container.slots;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import stevesvehicles.common.blocks.tileentitys.assembler.UpgradeContainer;
import stevesvehicles.common.upgrades.effects.BaseEffect;
import stevesvehicles.common.upgrades.effects.assembly.Disassemble;

public class SlotCartDisassemble extends SlotCart {
	public SlotCartDisassemble(IInventory inventory, int id, int x, int y) {
		super(inventory, id, x, y);
	}

	@Override
	public boolean isItemValid(ItemStack itemstack) {
		if (this.inventory instanceof UpgradeContainer) {
			UpgradeContainer upgrade = (UpgradeContainer) this.inventory;
			for (BaseEffect effect : upgrade.getEffects()) {
				if (effect instanceof Disassemble) {
					return ((Disassemble) effect).canDisassemble(upgrade) == 2 && super.isItemValid(itemstack);
				}
			}
		}
		return false;
	}
}
