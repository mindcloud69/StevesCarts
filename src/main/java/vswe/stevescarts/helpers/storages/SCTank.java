package vswe.stevescarts.helpers.storages;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.*;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vswe.stevescarts.guis.GuiBase;
import vswe.stevescarts.helpers.Localization;

import javax.annotation.Nonnull;

@Deprecated
public class SCTank extends FluidTank {
	private ITankHolder owner;
	private int tankid;
	private boolean isLocked;

	public SCTank(final ITankHolder owner, final int tankSize, final int tankid) {
		super(tankSize);
		this.owner = owner;
		this.tankid = tankid;
	}

	public SCTank copy() {
		final SCTank tank = new SCTank(this.owner, this.capacity, this.tankid);
		if (this.getFluid() != null) {
			tank.setFluid(this.getFluid().copy());
		}
		return tank;
	}

	public void setFluid(final FluidStack fluid) {
		this.fluid = fluid;
	}

	public void containerTransfer() {
		@Nonnull
		ItemStack item = this.owner.getInputContainer(this.tankid);
		if (!item.isEmpty()) {
			FluidUtil.tryFillContainer(item, this, 1000, null, true);
			FluidUtil.tryEmptyContainer(item, this, 1000, null, true);
		}
	}

	public void setLocked(final boolean val) {
		this.isLocked = val;
	}

	public boolean isLocked() {
		return this.isLocked;
	}

	public String getMouseOver() {
		String name = Localization.MODULES.TANKS.EMPTY.translate();
		int amount = 0;
		if (this.fluid != null) {
			name = this.fluid.getFluid().getLocalizedName(fluid);
			if (name.indexOf(".") != -1) {
				name = FluidRegistry.getFluidName(this.fluid);
			}
			if (name != null && !name.equals("")) {
				name = Character.toUpperCase(name.charAt(0)) + name.substring(1);
			} else {
				name = Localization.MODULES.TANKS.INVALID.translate();
			}
			amount = this.fluid.amount;
		}
		return name + "\n" + this.formatNumber(amount) + " / " + this.formatNumber(this.capacity);
	}

	private String formatNumber(final int number) {
		return String.format("%,d", number)/*.replace('�', ' ')*/;
	}

	private static float getColorComponent(final int color, final int id) {
		return ((color & 255 << id * 8) >> id * 8) / 255.0f;
	}

	@Deprecated
	@SideOnly(Side.CLIENT)
	public void drawFluid(final GuiBase gui, final int startX, final int startY) {
		//		if (this.fluid != null) {
		//			final int fluidLevel = (int) (48.0f * (this.fluid.amount / this.tanksize));
		//			final IconData data = getIconAndTexture(this.fluid);
		//			if (data.getIcon() == null) {
		//				return;
		//			}
		//			ResourceHelper.bindResource(data.getResource());
		//			applyColorFilter(this.fluid);
		//			for (int y = 0; y < 3; ++y) {
		//				int pixels = fluidLevel - (2 - y) * 16;
		//				if (pixels > 0) {
		//					if (pixels > 16) {
		//						pixels = 16;
		//					}
		//					for (int x = 0; x < 2; ++x) {
		//						this.owner.drawImage(this.tankid, gui, data.getIcon(), startX + 2 + 16 * x, startY + 1 + 16 * y + (16 - pixels), 0, 16 - pixels, 16, pixels);
		//					}
		//				}
		//			}
		//			GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		//		}
	}

	@Override
	public int getFluidAmount() {
		return (this.fluid == null) ? 0 : this.fluid.amount;
	}

	@Override
	public FluidTankInfo getInfo() {
		return new FluidTankInfo(this.fluid, this.getCapacity());
	}
}
