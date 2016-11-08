package vswe.stevescarts.containers.slots;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import vswe.stevescarts.blocks.tileentities.TileEntityCartAssembler;
import vswe.stevescarts.items.ModItems;

public class SlotOutput extends SlotAssembler {
	public SlotOutput(final TileEntityCartAssembler assembler, final int i, final int j, final int k) {
		super(assembler, i, j, k, 0, true, 0);
	}

	@Override
	public void validate() {
	}

	@Override
	public void invalidate() {
	}

	@Override
	public boolean isItemValid(final ItemStack itemstack) {
		if (!this.getAssembler().getIsAssembling() && itemstack.getItem() == ModItems.carts) {
			final NBTTagCompound info = itemstack.getTagCompound();
			if (info != null && info.hasKey("maxTime")) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean shouldUpdatePlaceholder() {
		return false;
	}
}
