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
		return distributor;
	}

	public ContainerDistributor(final IInventory invPlayer, final TileEntityDistributor distributor) {
		this.distributor = distributor;
		cachedValues = new ArrayList<>();
		for (final DistributorSide side : distributor.getSides()) {
			cachedValues.add((short) 0);
			cachedValues.add((short) 0);
		}
	}
}
