package vswe.stevescarts.helpers.storages;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.*;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import reborncore.client.RenderUtil;
import vswe.stevescarts.guis.GuiBase;
import vswe.stevescarts.helpers.IconData;
import vswe.stevescarts.helpers.Localization;
import vswe.stevescarts.helpers.ResourceHelper;

import javax.annotation.Nonnull;

@Deprecated
public class SCTank extends FluidTank implements IFluidHandler {
	private ITankHolder owner;
	private int tankid;
	private boolean isLocked;

	public SCTank(final ITankHolder owner, final int tankSize, final int tankid) {
		super(tankSize);
		this.owner = owner;
		this.tankid = tankid;
	}

	public SCTank copy() {
		final SCTank tank = new SCTank(owner, capacity, tankid);
		if (getFluid() != null) {
			tank.setFluid(getFluid().copy());
		}
		return tank;
	}

	public void setFluid(final FluidStack fluid) {
		this.fluid = fluid;
	}

	public void containerTransfer() {
		@Nonnull
		ItemStack item = owner.getInputContainer(tankid);
		if (!item.isEmpty()) {
			//FluidUtil.tryFillContainer(item, this, 1000, null, true);
			FluidUtil.tryEmptyContainer(item, this, 1000, null, true);
		}
	}

	public void setLocked(final boolean val) {
		isLocked = val;
	}

	public boolean isLocked() {
		return isLocked;
	}

	public String getMouseOver() {
		String name = Localization.MODULES.TANKS.EMPTY.translate();
		int amount = 0;
		if (fluid != null) {
			name = fluid.getFluid().getLocalizedName(fluid);
			if (name.indexOf(".") != -1) {
				name = FluidRegistry.getFluidName(fluid);
			}
			if (name != null && !name.equals("")) {
				name = Character.toUpperCase(name.charAt(0)) + name.substring(1);
			} else {
				name = Localization.MODULES.TANKS.INVALID.translate();
			}
			amount = fluid.amount;
		}
		return name + "\n" + formatNumber(amount) + " / " + formatNumber(capacity);
	}

	private String formatNumber(final int number) {
		return String.format("%,d", number)/*.replace('�', ' ')*/;
	}

	private static float getColorComponent(final int color, final int id) {
		return ((color & 255 << id * 8) >> id * 8) / 255.0f;
	}

	@Deprecated
	@SideOnly(Side.CLIENT)
	public void drawFluid(final GuiBase gui, final int startX, final int startY, int width, int height) {
		RenderUtil.renderGuiTank(this, gui.getGuiLeft() + startX, + gui.getGuiTop() + startY, 0, width, height);
	}

	@Override
	public int getFluidAmount() {
		return (fluid == null) ? 0 : fluid.amount;
	}

	@Override
	public FluidTankInfo getInfo() {
		return new FluidTankInfo(fluid, getCapacity());
	}
}
