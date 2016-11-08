package vswe.stevescarts.Containers;

import net.minecraft.inventory.IInventory;
import vswe.stevescarts.Helpers.DistributorSide;
import vswe.stevescarts.TileEntities.TileEntityBase;
import vswe.stevescarts.TileEntities.TileEntityDistributor;

import java.util.ArrayList;

public class ContainerDistributor extends ContainerBase {
	private TileEntityDistributor distributor;
	public ArrayList<Short> cachedValues;

	@Override
	public IInventory getMyInventory() {
		return null;
	}

	@Override
	public TileEntityBase getTileEntity() {
		return this.distributor;
	}

	public ContainerDistributor(final IInventory invPlayer, final TileEntityDistributor distributor) {
		this.distributor = distributor;
		this.cachedValues = new ArrayList<Short>();
		for (final DistributorSide side : distributor.getSides()) {
			this.cachedValues.add((short) 0);
			this.cachedValues.add((short) 0);
		}
	}
}
