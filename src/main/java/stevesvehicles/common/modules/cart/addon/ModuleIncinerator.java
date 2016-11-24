package stevesvehicles.common.modules.cart.addon;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidRegistry;
import stevesvehicles.client.gui.screen.GuiVehicle;
import stevesvehicles.common.container.slots.SlotBase;
import stevesvehicles.common.container.slots.SlotIncinerator;
import stevesvehicles.common.modules.common.addon.ModuleAddon;
import stevesvehicles.common.vehicles.VehicleBase;

public class ModuleIncinerator extends ModuleAddon {
	public ModuleIncinerator(VehicleBase vehicleBase) {
		super(vehicleBase);
	}

	public void incinerate(ItemStack item) {
		if (isItemValid(item)) {
			if (getIncinerationCost() != 0) {
				int amount = item.getCount() * getIncinerationCost();
				amount = getVehicle().drain(FluidRegistry.LAVA, amount, false);
				int incinerated = amount / getIncinerationCost();
				getVehicle().drain(FluidRegistry.LAVA, incinerated * getIncinerationCost(), true);
				item.shrink(incinerated);
			} else {
				item.setCount(0);
			}
		}
	}

	protected int getIncinerationCost() {
		return 3;
	}

	protected boolean isItemValid(ItemStack item) {
		if (item != null) {
			for (int i = 0; i < getInventorySize(); i++) {
				if (getStack(i) != null && item.isItemEqual(getStack(i))) {
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
	public void drawForeground(GuiVehicle gui) {
		drawString(gui, getModuleName(), 8, 6, 0x404040);
	}

	@Override
	protected int getInventoryWidth() {
		return 4;
	}

	@Override
	protected SlotBase getSlot(int slotId, int x, int y) {
		return new SlotIncinerator(getVehicle().getVehicleEntity(), slotId, 8 + x * 18, 23 + y * 18);
	}
}
