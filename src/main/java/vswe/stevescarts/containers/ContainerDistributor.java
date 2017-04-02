package vswe.stevescarts.containers;

import net.minecraft.inventory.IInventory;
import vswe.stevescarts.blocks.tileentities.TileEntityBase;
import vswe.stevescarts.blocks.tileentities.TileEntityDistributor;
import vswe.stevescarts.helpers.DistributorSide;

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
		this.cachedValues = new ArrayList<>();
		for (final DistributorSide side : distributor.getSides()) {
			this.cachedValues.add((short) 0);
			this.cachedValues.add((short) 0);
		}
	}
}
