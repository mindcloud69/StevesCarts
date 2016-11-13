package vswe.stevesvehicles.container;
import net.minecraft.inventory.IInventory;

import vswe.stevesvehicles.tileentity.TileEntityBase;
import vswe.stevesvehicles.tileentity.TileEntityDetector;
import vswe.stevesvehicles.tileentity.detector.LogicObject;
import vswe.stevesvehicles.tileentity.detector.LogicObjectOperator;
import vswe.stevesvehicles.tileentity.detector.OperatorObject;

public class ContainerDetector extends ContainerBase {

	@Override
	public IInventory getMyInventory() {
		return null;
	}

	@Override
	public TileEntityBase getTileEntity() {
		return detector;
	}	

	private TileEntityDetector detector;
	public LogicObject mainObj;
	public ContainerDetector(TileEntityDetector detector) {
		this.detector = detector;


		mainObj = new LogicObjectOperator((byte)0, OperatorObject.MAIN);
	}


}
