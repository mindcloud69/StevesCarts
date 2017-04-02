package vswe.stevescarts.containers.slots;

import net.minecraft.item.ItemStack;
import reborncore.common.util.FluidUtils;
import vswe.stevescarts.blocks.tileentities.TileEntityLiquid;
import vswe.stevescarts.helpers.storages.SCTank;

import javax.annotation.Nonnull;

public class SlotLiquidManagerInput extends SlotBase {
	private TileEntityLiquid manager;
	private int tankid;

	public SlotLiquidManagerInput(final TileEntityLiquid manager, final int tankid, final int i, final int j, final int k) {
		super(manager, i, j, k);
		this.manager = manager;
		this.tankid = tankid;
	}

	@Override
	public boolean isItemValid(
		@Nonnull
			ItemStack itemstack) {
		return isItemStackValid(itemstack, this.manager, this.tankid);
	}

	public static boolean isItemStackValid(
		@Nonnull
			ItemStack itemstack, final TileEntityLiquid manager, final int tankid) {
		if (tankid < 0 || tankid >= 4) {
			return FluidUtils.getFluidStackInContainer(itemstack) != null;
		}
		final SCTank tank = manager.getTanks()[tankid];
		return (FluidUtils.getFluidStackInContainer(itemstack) != null && tank.getFluid() != null) || (FluidUtils.getFluidStackInContainer(itemstack) != null && (tank.getFluid() == null || tank.getFluid().isFluidEqual(FluidUtils.getFluidStackInContainer(itemstack))));
	}
}
