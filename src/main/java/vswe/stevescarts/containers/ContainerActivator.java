package vswe.stevescarts.containers;

import net.minecraft.inventory.IInventory;
import vswe.stevescarts.blocks.tileentities.TileEntityActivator;
import vswe.stevescarts.blocks.tileentities.TileEntityBase;
import vswe.stevescarts.helpers.ActivatorOption;

import java.util.ArrayList;

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
		this.lastOptions = new ArrayList<>();
		for (final ActivatorOption option : activator.getOptions()) {
			this.lastOptions.add(option.getOption());
		}
	}
}
