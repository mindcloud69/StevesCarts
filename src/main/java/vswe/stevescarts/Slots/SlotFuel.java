package vswe.stevescarts.Slots;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraftforge.fluids.FluidContainerRegistry;
import vswe.stevescarts.Modules.Engines.ModuleCoalBase;

public class SlotFuel extends SlotBase {
	public SlotFuel(final IInventory iinventory, final int i, final int j, final int k) {
		super(iinventory, i, j, k);
	}

	public boolean isItemValid(final ItemStack itemstack) {
		return this.getItemBurnTime(itemstack) > 0;
	}

	private boolean isValid(final ItemStack itemstack) {
		return !FluidContainerRegistry.isFilledContainer(itemstack);
	}

	private int getItemBurnTime(final ItemStack itemstack) {
		return this.isValid(itemstack) ? TileEntityFurnace.getItemBurnTime(itemstack) : 0;
	}

	public static int getItemBurnTime(final ModuleCoalBase engine, final ItemStack itemstack) {
		return (int) (TileEntityFurnace.getItemBurnTime(itemstack) * engine.getFuelMultiplier());
	}
}
