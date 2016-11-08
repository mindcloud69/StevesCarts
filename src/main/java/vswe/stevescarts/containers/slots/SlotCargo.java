package vswe.stevescarts.containers.slots;

import vswe.stevescarts.blocks.tileentities.TileEntityCargo;

public class SlotCargo extends SlotBase implements ISpecialSlotValidator {
	private TileEntityCargo cargo;
	private int id;

	public SlotCargo(final TileEntityCargo cargo, final int id) {
		super(cargo, id, -3000, -3000);
		this.id = id;
		this.cargo = cargo;
	}

	@Override
	public boolean isSlotValid() {
		if (this.cargo.layoutType == 0) {
			return true;
		}
		int type;
		if (this.cargo.layoutType == 1) {
			type = this.cargo.getCurrentTransferForSlots().getSetting();
		} else {
			type = this.cargo.getCurrentTransferForSlots().getSide();
		}
		int slotType = this.id / 15;
		if (this.cargo.layoutType == 2) {
			slotType = this.cargo.color[slotType] - 1;
		}
		return slotType == type;
	}

	public void updatePosition() {
		int offset;
		if (this.cargo.layoutType == 0) {
			offset = 0;
		} else {
			offset = 5;
		}
		if (this.id < 15) {
			final int x = this.id % 5;
			final int y = this.id / 5;
			this.xDisplayPosition = 8 + x * 18;
			this.yDisplayPosition = 16 + y * 18 - offset;
		} else if (this.id < 30) {
			final int x = (this.id - 15) % 5 + 11;
			final int y = (this.id - 15) / 5;
			this.xDisplayPosition = 8 + x * 18;
			this.yDisplayPosition = 16 + y * 18 - offset;
		} else if (this.id < 45) {
			final int x = (this.id - 30) % 5;
			final int y = (this.id - 30) / 5 + 3;
			this.xDisplayPosition = 8 + x * 18;
			this.yDisplayPosition = 16 + y * 18 + offset;
		} else {
			final int x = (this.id - 45) % 5 + 11;
			final int y = (this.id - 45) / 5 + 3;
			this.xDisplayPosition = 8 + x * 18;
			this.yDisplayPosition = 16 + y * 18 + offset;
		}
	}
}
