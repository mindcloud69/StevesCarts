package vswe.stevesvehicles.container;

import java.util.ArrayList;

import net.minecraft.inventory.IInventory;

import vswe.stevesvehicles.tileentity.TileEntityActivator;
import vswe.stevesvehicles.tileentity.TileEntityBase;
import vswe.stevesvehicles.tileentity.toggler.TogglerOption;

public class ContainerActivator extends ContainerBase {
	@Override
	public IInventory getMyInventory() {
		return null;
	}

	@Override
	public TileEntityBase getTileEntity() {
		return activator;
	}

	private TileEntityActivator activator;

	public ContainerActivator(TileEntityActivator activator) {
		this.activator = activator;
		lastOptions = new ArrayList<>();
		for (TogglerOption option : activator.getOptions()) {
			lastOptions.add(option.getOption());
		}
	}

	public ArrayList<Integer> lastOptions;
}
