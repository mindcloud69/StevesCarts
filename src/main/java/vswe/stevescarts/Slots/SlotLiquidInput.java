package vswe.stevescarts.Slots;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidContainerRegistry;
import vswe.stevescarts.Helpers.Tank;

public class SlotLiquidInput extends SlotBase {
	private Tank tank;
	private int maxsize;

	public SlotLiquidInput(final IInventory iinventory, final Tank tank, final int maxsize, final int i, final int j, final int k) {
		super(iinventory, i, j, k);
		this.tank = tank;
		this.maxsize = maxsize;
	}

	@Override
	public int getSlotStackLimit() {
		if (this.maxsize != -1) {
			return this.maxsize;
		}
		return Math.min(8, this.tank.getCapacity() / 1000);
	}

	@Override
	public boolean isItemValid(final ItemStack itemstack) {
		return (FluidContainerRegistry.isEmptyContainer(itemstack) && this.tank.getFluid() != null) || (FluidContainerRegistry.isFilledContainer(itemstack) && (this.tank.getFluid() == null || this.tank.getFluid().isFluidEqual(FluidContainerRegistry.getFluidForFilledItem(itemstack))));
	}
}
