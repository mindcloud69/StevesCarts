package vswe.stevescarts.Containers;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import vswe.stevescarts.TileEntities.TileEntityBase;
import vswe.stevescarts.TileEntities.TileEntityUpgrade;
import vswe.stevescarts.Upgrades.InventoryEffect;

public class ContainerUpgrade extends ContainerBase {
	private TileEntityUpgrade upgrade;
	public Object olddata;

	@Override
	public IInventory getMyInventory() {
		return this.upgrade;
	}

	@Override
	public TileEntityBase getTileEntity() {
		return this.upgrade;
	}

	public ContainerUpgrade(final IInventory invPlayer, final TileEntityUpgrade upgrade) {
		this.upgrade = upgrade;
		if (upgrade.getUpgrade() == null || upgrade.getUpgrade().getInventoryEffect() == null) {
			return;
		}
		final InventoryEffect inventory = upgrade.getUpgrade().getInventoryEffect();
		inventory.clear();
		for (int id = 0; id < inventory.getInventorySize(); ++id) {
			final Slot slot = inventory.createSlot(upgrade, id);
			this.addSlotToContainer(slot);
			inventory.addSlot(slot);
		}
		for (int i = 0; i < 3; ++i) {
			for (int k = 0; k < 9; ++k) {
				this.addSlotToContainer(new Slot(invPlayer, k + i * 9 + 9, this.offsetX() + k * 18, i * 18 + this.offsetY()));
			}
		}
		for (int j = 0; j < 9; ++j) {
			this.addSlotToContainer(new Slot(invPlayer, j, this.offsetX() + j * 18, 58 + this.offsetY()));
		}
	}

	protected int offsetX() {
		return 48;
	}

	protected int offsetY() {
		return 108;
	}
}
