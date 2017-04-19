package vswe.stevescarts.containers;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import vswe.stevescarts.blocks.tileentities.TileEntityBase;
import vswe.stevescarts.blocks.tileentities.TileEntityUpgrade;
import vswe.stevescarts.upgrades.InventoryEffect;

public class ContainerUpgrade extends ContainerBase {
	private TileEntityUpgrade upgrade;
	public Object olddata;

	@Override
	public IInventory getMyInventory() {
		return upgrade;
	}

	@Override
	public TileEntityBase getTileEntity() {
		return upgrade;
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
			addSlotToContainer(slot);
			inventory.addSlot(slot);
		}
		for (int i = 0; i < 3; ++i) {
			for (int k = 0; k < 9; ++k) {
				addSlotToContainer(new Slot(invPlayer, k + i * 9 + 9, offsetX() + k * 18, i * 18 + offsetY()));
			}
		}
		for (int j = 0; j < 9; ++j) {
			addSlotToContainer(new Slot(invPlayer, j, offsetX() + j * 18, 58 + offsetY()));
		}
	}

	protected int offsetX() {
		return 48;
	}

	protected int offsetY() {
		return 108;
	}
}
