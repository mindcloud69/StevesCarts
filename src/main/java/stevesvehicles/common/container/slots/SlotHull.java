package stevesvehicles.common.container.slots;

import stevesvehicles.common.blocks.tileentitys.TileEntityCartAssembler;
import stevesvehicles.common.modules.datas.ModuleType;

public class SlotHull extends SlotAssembler {
	public SlotHull(TileEntityCartAssembler assembler, int id, int x, int y) {
		super(assembler, id, x, y, ModuleType.HULL, true, 0);
	}
}
