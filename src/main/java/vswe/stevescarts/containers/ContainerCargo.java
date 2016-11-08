package vswe.stevescarts.containers;

import java.util.ArrayList;

import net.minecraft.inventory.IInventory;
import vswe.stevescarts.blocks.tileentities.TileEntityCargo;
import vswe.stevescarts.containers.slots.SlotCargo;

public class ContainerCargo extends ContainerManager {
	public short lastTarget;

	public ContainerCargo(final IInventory invPlayer, final TileEntityCargo cargo) {
		super(cargo);
		cargo.cargoSlots = new ArrayList<SlotCargo>();
		cargo.lastLayout = -1;
		for (int i = 0; i < 60; ++i) {
			final SlotCargo slot = new SlotCargo(cargo, i);
			this.addSlotToContainer(slot);
			cargo.cargoSlots.add(slot);
		}
		this.addPlayer(invPlayer);
	}

	@Override
	protected int offsetX() {
		return 73;
	}
}
