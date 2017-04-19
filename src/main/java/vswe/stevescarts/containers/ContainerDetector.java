package vswe.stevescarts.containers;

import net.minecraft.inventory.IInventory;
import vswe.stevescarts.blocks.tileentities.TileEntityBase;
import vswe.stevescarts.blocks.tileentities.TileEntityDetector;
import vswe.stevescarts.helpers.LogicObject;

public class ContainerDetector extends ContainerBase {
	private TileEntityDetector detector;
	public LogicObject mainObj;

	@Override
	public IInventory getMyInventory() {
		return null;
	}

	@Override
	public TileEntityBase getTileEntity() {
		return detector;
	}

	public ContainerDetector(final IInventory invPlayer, final TileEntityDetector detector) {
		this.detector = detector;
		mainObj = new LogicObject((byte) 1, (byte) 0);
	}
}
