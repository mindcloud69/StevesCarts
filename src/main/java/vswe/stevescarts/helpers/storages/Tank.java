package vswe.stevescarts.helpers.storages;

import org.lwjgl.opengl.GL11;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vswe.stevescarts.guis.GuiBase;
import vswe.stevescarts.helpers.IconData;
import vswe.stevescarts.helpers.Localization;

public class Tank implements IFluidTank {
	private FluidStack fluid;
	private int tankSize;
	private ITankHolder owner;
	private int tankid;
	private boolean isLocked;

	public Tank(final ITankHolder owner, final int tankSize, final int tankid) {
		this.owner = owner;
		this.tankSize = tankSize;
		this.tankid = tankid;
	}

	public Tank copy() {
		final Tank tank = new Tank(this.owner, this.tankSize, this.tankid);
		if (this.getFluid() != null) {
			tank.setFluid(this.getFluid().copy());
		}
		return tank;
	}

	@Override
	public FluidStack getFluid() {
		return this.fluid;
	}

	public void setFluid(final FluidStack fluid) {
		this.fluid = fluid;
	}

	@Override
	public int getCapacity() {
		return this.tankSize;
	}

	public int getTankPressure() {
		return 0;
	}

	public void containerTransfer() {
		final ItemStack item = this.owner.getInputContainer(this.tankid);
		if (item != null) {
			if (FluidContainerRegistry.isFilledContainer(item)) {
				final FluidStack fluidContent = FluidContainerRegistry.getFluidForFilledItem(item);
				if (fluidContent != null) {
					final int fill = this.fill(fluidContent, false, false);
					if (fill == fluidContent.amount) {
						final Item container = item.getItem().getContainerItem();
						ItemStack containerStack = null;
						if (container != null) {
							containerStack = new ItemStack(container, 1);
							this.owner.addToOutputContainer(this.tankid, containerStack);
						}
						if (containerStack == null || containerStack.stackSize == 0) {
							final ItemStack itemStack = item;
							--itemStack.stackSize;
							if (item.stackSize <= 0) {
								this.owner.clearInputContainer(this.tankid);
							}
							this.fill(fluidContent, true, false);
						}
					}
				}
			} else if (FluidContainerRegistry.isEmptyContainer(item)) {
				final ItemStack full = FluidContainerRegistry.fillFluidContainer(this.fluid, item);
				if (full != null) {
					final FluidStack fluidContent2 = FluidContainerRegistry.getFluidForFilledItem(full);
					if (fluidContent2 != null) {
						this.owner.addToOutputContainer(this.tankid, full);
						if (full.stackSize == 0) {
							final ItemStack itemStack2 = item;
							--itemStack2.stackSize;
							if (item.stackSize <= 0) {
								this.owner.clearInputContainer(this.tankid);
							}
							this.drain(fluidContent2.amount, true, false);
						}
					}
				}
			}
		}
	}

	@Override
	public int fill(final FluidStack resource, final boolean doFill) {
		return this.fill(resource, doFill, false);
	}

	public int fill(final FluidStack resource, final boolean doFill, final boolean isRemote) {
		if (resource == null || (this.fluid != null && !resource.isFluidEqual(this.fluid))) {
			return 0;
		}
		final int free = this.tankSize - ((this.fluid == null) ? 0 : this.fluid.amount);
		final int fill = Math.min(free, resource.amount);
		if (doFill && !isRemote) {
			if (this.fluid == null) {
				this.fluid = resource.copy();
				this.fluid.amount = 0;
			}
			final FluidStack fluid = this.fluid;
			fluid.amount += fill;
			this.owner.onFluidUpdated(this.tankid);
		}
		return fill;
	}

	@Override
	public FluidStack drain(final int maxDrain, final boolean doDrain) {
		return this.drain(maxDrain, doDrain, false);
	}

	public FluidStack drain(final int maxDrain, final boolean doDrain, final boolean isRemote) {
		if (this.fluid == null) {
			return null;
		}
		final int amount = this.fluid.amount;
		final int drain = Math.min(amount, maxDrain);
		final FluidStack ret = this.fluid.copy();
		ret.amount = drain;
		if (doDrain && !isRemote) {
			final FluidStack fluid = this.fluid;
			fluid.amount -= drain;
			if (this.fluid.amount <= 0 && !this.isLocked) {
				this.fluid = null;
			}
			this.owner.onFluidUpdated(this.tankid);
		}
		return ret;
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
		return "�F" + name + "\n�7" + this.formatNumber(amount) + " / " + this.formatNumber(this.tankSize);
	}

	private String formatNumber(final int number) {
		return String.format("%,d", number)/*.replace('�', ' ')*/;
	}

	@Deprecated
	public static IconData getIconAndTexture(final FluidStack stack) {
		//		IIcon icon = null;
		//		String texture = null;
		//		if (stack != null) {
		//			final Fluid fluid = stack.getFluid();
		//			if (fluid != null) {
		//				icon = fluid.getIcon();
		//				if (icon == null) {
		//					if (FluidRegistry.WATER.equals(fluid)) {
		//						icon = Blocks.water.getIcon(0, 0);
		//					} else if (FluidRegistry.LAVA.equals(fluid)) {
		//						icon = Blocks.lava.getIcon(0, 0);
		//					}
		//				}
		//				if (icon != null) {
		//					texture = "/atlas/blocks.png";
		//				}
		//			}
		//		}
		//		return new IconData(icon, texture);
		return null;
	}

	private static float getColorComponent(final int color, final int id) {
		return ((color & 255 << id * 8) >> id * 8) / 255.0f;
	}

	public static void applyColorFilter(final FluidStack fluid) {
		final int color = fluid.getFluid().getColor(fluid);
		GL11.glColor4f(getColorComponent(color, 2), getColorComponent(color, 1), getColorComponent(color, 0), 1.0f);
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
