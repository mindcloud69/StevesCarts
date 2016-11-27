package stevesvehicles.common.container;

import java.util.Iterator;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import stevesvehicles.common.blocks.tileentitys.TileEntityBase;
import stevesvehicles.common.blocks.tileentitys.assembler.UpgradeContainer;
import stevesvehicles.common.upgrades.effects.util.InventoryEffect;

public class ContainerUpgrade extends ContainerBase {
	@Override
	public IInventory getMyInventory() {
		return container;
	}

	@Override
	public TileEntityBase getTileEntity() {
		return container.getMaster();
	}

	private UpgradeContainer container;

	public ContainerUpgrade(IInventory invPlayer, UpgradeContainer container) {
		this.container = container;
		if (container.getEffects() == null || container.getInventoryEffect() == null) {
			return;
		}
		InventoryEffect inventory = container.getInventoryEffect();
		inventory.clear();
		for (int id = 0; id < inventory.getInventorySize(); id++) {
			Slot slot = inventory.createSlot(id);
			addSlotToContainer(slot);
			inventory.addSlot(slot);
		}
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 9; j++) {
				addSlotToContainer(new Slot(invPlayer, j + i * 9 + 9, offsetX() + j * 18, i * 18 + offsetY()));
			}
		}
		for (int i = 0; i < 9; i++) {
			addSlotToContainer(new Slot(invPlayer, i, offsetX() + i * 18, 58 + offsetY()));
		}
	}

	protected int offsetX() {
		return 48;
	}

	protected int offsetY() {
		return 108;
	}

	// temporary solution, make a proper one later
	public Object olddata;
	
	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return container != null && container.isUsableByPlayer(player);
	}

	@Override
	public void addListener(IContainerListener listener) {
		super.addListener(listener);
		if (container != null) {
			container.initGuiData(this, listener);
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void updateProgressBar(int id, int val) {
		val &= 65535;
		if (container != null) {
			container.receiveGuiData(id, (short) val);
		}
	}

	@Override
	public void detectAndSendChanges() {
        for (int i = 0; i < this.inventorySlots.size(); ++i){
            ItemStack itemstack = ((Slot)this.inventorySlots.get(i)).getStack();
            ItemStack itemstack1 = (ItemStack)this.inventoryItemStacks.get(i);

            if (!ItemStack.areItemStacksEqual(itemstack1, itemstack))
            {
                itemstack1 = itemstack.isEmpty() ? ItemStack.EMPTY : itemstack.copy();
                this.inventoryItemStacks.set(i, itemstack1);

                for (int j = 0; j < this.listeners.size(); ++j)
                {
                    ((IContainerListener)this.listeners.get(j)).sendSlotContents(this, i, itemstack1);
                }
            }
        }
		if (container != null) {
			Iterator<IContainerListener> playerIterator = this.listeners.iterator();
			while (playerIterator.hasNext()) {
				IContainerListener player = playerIterator.next();
				container.checkGuiData(this, player);
			}
		}
	}
}
