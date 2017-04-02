package vswe.stevescarts.helpers.storages;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.*;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import reborncore.common.util.FluidUtils;
import vswe.stevescarts.guis.GuiBase;
import vswe.stevescarts.helpers.IconData;
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

	//TODO 1.11
	public void containerTransfer() {
//		@Nonnull
//		ItemStack item = this.owner.getInputContainer(this.tankid);
//		if (!item.isEmpty()) {
//			if (FluidContainerRegistry.isFilledContainer(item)) {
//				final FluidStack fluidContent = FluidContainerRegistry.getFluidForFilledItem(item);
//				if (fluidContent != null) {
//					final int fill = this.fill(fluidContent, false, false);
//					if (fill == fluidContent.amount) {
//						final Item container = item.getItem().getContainerItem();
//						ItemStack containerStack = null;
//						if (container != null) {
//							containerStack = new ItemStack(container, 1);
//							this.owner.addToOutputContainer(this.tankid, containerStack);
//						}
//						if (containerStack == null || containerStack.getCount() == 0) {
//							@Nonnull
//							ItemStack itemStack = item;
//							itemStack.shrink(1);
//							if (item.getCount() <= 0) {
//								this.owner.clearInputContainer(this.tankid);
//							}
//							this.fill(fluidContent, true, false);
//						}
//					}
//				}
//			} else if (FluidContainerRegistry.isEmptyContainer(item)) {
//				@Nonnull
//				ItemStack full = FluidContainerRegistry.fillFluidContainer(this.fluid, item);
//				if (full != null) {
//					final FluidStack fluidContent2 = FluidContainerRegistry.getFluidForFilledItem(full);
//					if (fluidContent2 != null) {
//						this.owner.addToOutputContainer(this.tankid, full);
//						if (full.getCount() == 0) {
//							@Nonnull
//							ItemStack itemStack2 = item;
//							itemStack2.shrink(1);
//							if (item.getCount() <= 0) {
//								this.owner.clearInputContainer(this.tankid);
//							}
//							this.drain(fluidContent2.amount, true, false);
//						}
//					}
//				}
//			}
//		}
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
