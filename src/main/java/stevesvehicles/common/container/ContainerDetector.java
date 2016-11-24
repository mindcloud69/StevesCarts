package stevesvehicles.common.container;

import net.minecraft.inventory.IInventory;
import stevesvehicles.common.blocks.tileentitys.TileEntityBase;
import stevesvehicles.common.blocks.tileentitys.TileEntityDetector;
import stevesvehicles.common.blocks.tileentitys.detector.LogicObject;
import stevesvehicles.common.blocks.tileentitys.detector.LogicObjectOperator;
import stevesvehicles.common.blocks.tileentitys.detector.OperatorObject;

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
		mainObj = new LogicObjectOperator((byte) 0, OperatorObject.MAIN);
	}
}
