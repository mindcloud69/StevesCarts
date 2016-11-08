package vswe.stevescarts.Containers;

import java.util.ArrayList;

import net.minecraft.inventory.IInventory;
import vswe.stevescarts.Helpers.ActivatorOption;
import vswe.stevescarts.TileEntities.TileEntityActivator;
import vswe.stevescarts.TileEntities.TileEntityBase;

public class ContainerActivator extends ContainerBase {
	private TileEntityActivator activator;
	public ArrayList<Integer> lastOptions;

	@Override
	public IInventory getMyInventory() {
		return null;
	}

	@Override
	public TileEntityBase getTileEntity() {
		return this.activator;
	}

	public ContainerActivator(final IInventory invPlayer, final TileEntityActivator activator) {
		this.activator = activator;
		this.lastOptions = new ArrayList<Integer>();
		for (final ActivatorOption option : activator.getOptions()) {
			this.lastOptions.add(option.getOption());
		}
	}
}
