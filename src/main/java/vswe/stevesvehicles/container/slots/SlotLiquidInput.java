package vswe.stevesvehicles.container.slots;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidUtil;
import vswe.stevesvehicles.tank.Tank;

public class SlotLiquidInput extends SlotBase {
	private Tank tank;
	private int maxsize;

	public SlotLiquidInput(IInventory inventory, Tank tank, int maxsize, int id, int x, int y) {
		super(inventory, id, x, y);
		this.tank = tank;
		this.maxsize = maxsize;
	}

	@Override
	public int getSlotStackLimit() {
		if (maxsize != -1) {
			return maxsize;
		} else {
			return Math.min(8, tank.getCapacity() / Fluid.BUCKET_VOLUME);
		}
	}

	@Override
	public boolean isItemValid(ItemStack itemstack) {
		return (FluidUtil.getFluidContained(itemstack) == null && tank.getFluid() != null) || (FluidUtil.getFluidContained(itemstack) != null && (tank.getFluid() == null || tank.getFluid().isFluidEqual(FluidUtil.getFluidContained(itemstack))));
	}
}
