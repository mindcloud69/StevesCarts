package vswe.stevesvehicles.container;
import java.util.ArrayList;

import net.minecraft.inventory.IInventory;

import vswe.stevesvehicles.tileentity.TileEntityBase;
import vswe.stevesvehicles.tileentity.TileEntityDistributor;
import vswe.stevesvehicles.tileentity.distributor.DistributorSide;

public class ContainerDistributor extends ContainerBase {

	@Override
	public IInventory getMyInventory() {
		return null;
	}

	@Override
	public TileEntityBase getTileEntity() {
		return distributor;
	}		


	private TileEntityDistributor distributor;
	public ContainerDistributor(TileEntityDistributor distributor) {
		this.distributor = distributor;

		cachedValues = new ArrayList<>();
		for (DistributorSide ignored : distributor.getSides()) {
			cachedValues.add((short) 0);
			cachedValues.add((short) 0);
		}
	}



	public ArrayList<Short> cachedValues;

}
