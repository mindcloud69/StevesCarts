package vswe.stevescarts.Containers;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import vswe.stevescarts.TileEntities.TileEntityBase;
import vswe.stevescarts.TileEntities.TileEntityManager;

public abstract class ContainerManager extends ContainerBase {
	private TileEntityManager manager;
	public short lastHeader;
	public short lastColor;
	public short lastAmount;

	@Override
	public IInventory getMyInventory() {
		return this.manager;
	}

	@Override
	public TileEntityBase getTileEntity() {
		return this.manager;
	}

	public ContainerManager(final TileEntityManager manager) {
		this.manager = manager;
	}

	protected void addPlayer(final IInventory invPlayer) {
		for (int k = 0; k < 3; ++k) {
			for (int j1 = 0; j1 < 9; ++j1) {
				this.addSlotToContainer(new Slot(invPlayer, j1 + k * 9 + 9, j1 * 18 + this.offsetX(), 104 + k * 18 + 36));
			}
		}
		for (int l = 0; l < 9; ++l) {
			this.addSlotToContainer(new Slot(invPlayer, l, l * 18 + this.offsetX(), 198));
		}
	}

	protected abstract int offsetX();
}
