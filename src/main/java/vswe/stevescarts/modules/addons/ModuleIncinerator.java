package vswe.stevescarts.modules.addons;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidRegistry;
import vswe.stevescarts.containers.slots.SlotBase;
import vswe.stevescarts.containers.slots.SlotIncinerator;
import vswe.stevescarts.entitys.EntityMinecartModular;
import vswe.stevescarts.guis.GuiMinecart;

import javax.annotation.Nonnull;

public class ModuleIncinerator extends ModuleAddon {
	public ModuleIncinerator(final EntityMinecartModular cart) {
		super(cart);
	}

	public void incinerate(
		@Nonnull
			ItemStack item) {
		if (this.isItemValid(item)) {
			if (this.getIncinerationCost() != 0) {
				int amount = item.getCount() * this.getIncinerationCost();
				amount = this.getCart().drain(FluidRegistry.LAVA, amount, false);
				final int incinerated = amount / this.getIncinerationCost();
				this.getCart().drain(FluidRegistry.LAVA, incinerated * this.getIncinerationCost(), true);
				item.shrink(incinerated);
			} else {
				item.setCount(0);
			}
		}
	}

	protected int getIncinerationCost() {
		return 3;
	}

	protected boolean isItemValid(
		@Nonnull
			ItemStack item) {
		if (!item.isEmpty()) {
			for (int i = 0; i < this.getInventorySize(); ++i) {
				if (!this.getStack(i).isEmpty() && item.isItemEqual(this.getStack(i))) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public boolean hasGui() {
		return true;
	}

	@Override
	public void drawForeground(final GuiMinecart gui) {
		this.drawString(gui, this.getModuleName(), 8, 6, 4210752);
	}

	@Override
	protected int getInventoryWidth() {
		return 4;
	}

	@Override
	protected SlotBase getSlot(final int slotId, final int x, final int y) {
		return new SlotIncinerator(this.getCart(), slotId, 8 + x * 18, 23 + y * 18);
	}
}
