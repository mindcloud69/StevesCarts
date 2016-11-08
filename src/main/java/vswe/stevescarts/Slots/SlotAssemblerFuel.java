package vswe.stevescarts.Slots;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraftforge.fluids.FluidContainerRegistry;
import vswe.stevescarts.TileEntities.TileEntityCartAssembler;

public class SlotAssemblerFuel extends SlotAssembler {
	public SlotAssemblerFuel(final TileEntityCartAssembler assembler, final int i, final int j, final int k) {
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
		return itemstack.getItem() == Items.COAL || (this.getAssembler().isCombustionFuelValid() && !FluidContainerRegistry.isFilledContainer(itemstack) && TileEntityFurnace.getItemBurnTime(itemstack) > 0);
	}

	public int getFuelLevel(final ItemStack itemstack) {
		if (this.isItemValid(itemstack)) {
			return (int) (TileEntityFurnace.getItemBurnTime(itemstack) * 0.25f);
		}
		return 0;
	}

	@Override
	public boolean shouldUpdatePlaceholder() {
		return false;
	}

	@Override
	public int getSlotStackLimit() {
		return 64;
	}
}
