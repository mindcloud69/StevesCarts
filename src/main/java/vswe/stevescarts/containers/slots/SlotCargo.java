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
		if (cargo.layoutType == 0) {
			return true;
		}
		int type;
		if (cargo.layoutType == 1) {
			type = cargo.getCurrentTransferForSlots().getSetting();
		} else {
			type = cargo.getCurrentTransferForSlots().getSide();
		}
		int slotType = id / 15;
		if (cargo.layoutType == 2) {
			slotType = cargo.color[slotType] - 1;
		}
		return slotType == type;
	}

	public void updatePosition() {
		int offset;
		if (cargo.layoutType == 0) {
			offset = 0;
		} else {
			offset = 5;
		}
		if (id < 15) {
			final int x = id % 5;
			final int y = id / 5;
			xPos = 8 + x * 18;
			yPos = 16 + y * 18 - offset;
		} else if (id < 30) {
			final int x = (id - 15) % 5 + 11;
			final int y = (id - 15) / 5;
			xPos = 8 + x * 18;
			yPos = 16 + y * 18 - offset;
		} else if (id < 45) {
			final int x = (id - 30) % 5;
			final int y = (id - 30) / 5 + 3;
			xPos = 8 + x * 18;
			yPos = 16 + y * 18 + offset;
		} else {
			final int x = (id - 45) % 5 + 11;
			final int y = (id - 45) / 5 + 3;
			xPos = 8 + x * 18;
			yPos = 16 + y * 18 + offset;
		}
	}
}
