package vswe.stevescarts.Slots;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidContainerRegistry;
import vswe.stevescarts.Helpers.Tank;
import vswe.stevescarts.TileEntities.TileEntityLiquid;

public class SlotLiquidManagerInput extends SlotBase {
	private TileEntityLiquid manager;
	private int tankid;

	public SlotLiquidManagerInput(final TileEntityLiquid manager, final int tankid, final int i, final int j, final int k) {
		super(manager, i, j, k);
		this.manager = manager;
		this.tankid = tankid;
	}

	@Override
	public boolean isItemValid(final ItemStack itemstack) {
		return isItemStackValid(itemstack, this.manager, this.tankid);
	}

	public static boolean isItemStackValid(final ItemStack itemstack, final TileEntityLiquid manager, final int tankid) {
		if (tankid < 0 || tankid >= 4) {
			return FluidContainerRegistry.isContainer(itemstack);
		}
		final Tank tank = manager.getTanks()[tankid];
		return (FluidContainerRegistry.isEmptyContainer(itemstack) && tank.getFluid() != null) || (FluidContainerRegistry.isFilledContainer(itemstack) && (tank.getFluid() == null || tank.getFluid().isFluidEqual(FluidContainerRegistry.getFluidForFilledItem(itemstack))));
	}
}
