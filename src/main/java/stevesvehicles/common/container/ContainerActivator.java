package stevesvehicles.common.container;

import java.util.ArrayList;

import net.minecraft.inventory.IInventory;
import stevesvehicles.common.blocks.tileentitys.TileEntityActivator;
import stevesvehicles.common.blocks.tileentitys.TileEntityBase;
import stevesvehicles.common.blocks.tileentitys.toggler.TogglerOption;

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
