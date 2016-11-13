package vswe.stevesvehicles.container;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vswe.stevesvehicles.container.slots.SlotAssembler;
import vswe.stevesvehicles.container.slots.SlotHull;
import vswe.stevesvehicles.tileentity.TileEntityBase;
import vswe.stevesvehicles.tileentity.TileEntityCartAssembler;

public class ContainerCartAssembler extends ContainerBase {

	@Override
	public IInventory getMyInventory() {
		return assembler;
	}

	@Override
	public TileEntityBase getTileEntity() {
		return assembler;
	}

	private TileEntityCartAssembler assembler;
	public ContainerCartAssembler(IInventory invPlayer, TileEntityCartAssembler assembler) {
		this.assembler = assembler;

		List<SlotAssembler> slots = assembler.getSlots();
		for (SlotAssembler slot : slots) {
			addSlotToContainer(slot);
		}



		for (int i = 0; i < 3; i++) {
			for (int k = 0; k < 9; k++) {
				addSlotToContainer(new Slot(invPlayer, k + i * 9 + 9, offsetX() + k * 18, i * 18 + offsetY()));
			}
		}

		for (int j = 0; j < 9; j++)
		{
			addSlotToContainer(new Slot(invPlayer, j, offsetX() + j * 18, 58 + offsetY()));
		}
	}


	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return assembler.isUseableByPlayer(player);
	}

	@Override
	public void addListener(IContainerListener listener) {
		super.addListener(listener);

		assembler.initGuiData(this, listener);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void updateProgressBar(int id, int data) {
		data &= 65535;

		assembler.receiveGuiData(id, (short)data);
	}

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		Iterator<IContainerListener> players = listeners.iterator();

		while (players.hasNext()) {
			IContainerListener player = players.next();

			assembler.checkGuiData(this,player);
		}
	}

	protected int offsetX() {
		return 176;
	}

	protected int offsetY() {
		return 174;
	}

	public int lastMaxAssemblingTime;
	public boolean lastIsAssembling;
	public int lastFuelLevel;

	@Override
	public ItemStack slotClick(int slotID, int button, ClickType clickType, EntityPlayer player) {
		if (slotID >= 0 && slotID < inventorySlots.size()) {
			Slot hullSlot = inventorySlots.get(slotID);

			if (hullSlot != null && hullSlot instanceof SlotHull) {
				InventoryPlayer playerInventory = player.inventory;
				ItemStack playerItem = playerInventory.getItemStack();
				ItemStack slotItem = hullSlot.getStack();

				ArrayList<SlotAssembler> newSlots = assembler.getValidSlotFromHullItem(playerItem);
				ArrayList<SlotAssembler> oldSlots = assembler.getValidSlotFromHullItem(slotItem);

				if (oldSlots != null) {
					if (newSlots != null) {
						for(SlotAssembler slot : newSlots) {
							int index = oldSlots.indexOf(slot);
							if (index != -1) {
								oldSlots.remove(index);
							}
						}
					}

					for (SlotAssembler slot : oldSlots) {
						if (slot.getHasStack()) {
							return null;
						}
					}

				}
			}
		}

		return super.slotClick(slotID,button, clickType, player);
	}

}
