package vswe.stevescarts.Containers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vswe.stevescarts.Slots.SlotAssembler;
import vswe.stevescarts.Slots.SlotHull;
import vswe.stevescarts.TileEntities.TileEntityBase;
import vswe.stevescarts.TileEntities.TileEntityCartAssembler;

import java.util.ArrayList;

public class ContainerCartAssembler extends ContainerBase {
	private TileEntityCartAssembler assembler;
	public int lastMaxAssemblingTime;
	public int lastCurrentAssemblingTime;
	public boolean lastIsAssembling;
	public int lastFuelLevel;

	@Override
	public IInventory getMyInventory() {
		return this.assembler;
	}

	@Override
	public TileEntityBase getTileEntity() {
		return this.assembler;
	}

	public ContainerCartAssembler(final IInventory invPlayer, final TileEntityCartAssembler assembler) {
		this.assembler = assembler;
		final ArrayList<SlotAssembler> slots = assembler.getSlots();
		for (final SlotAssembler slot : slots) {
			this.addSlotToContainer(slot);
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

	@Override
	public boolean canInteractWith(final EntityPlayer entityplayer) {
		return this.assembler.isUseableByPlayer(entityplayer);
	}

	@Override
	public void addListener(final IContainerListener par1ICrafting) {
		super.addListener(par1ICrafting);
		this.assembler.initGuiData(this, par1ICrafting);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void updateProgressBar(final int par1, int par2) {
		par2 &= 0xFFFF;
		this.assembler.receiveGuiData(par1, (short) par2);
	}

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		for (final IContainerListener var2 : this.listeners) {
			this.assembler.checkGuiData(this, var2);
		}
	}

	protected int offsetX() {
		return 176;
	}

	protected int offsetY() {
		return 174;
	}

	public ItemStack slotClick(final int slotID, final int button, final ClickType keyflag, final EntityPlayer player) {
		if (slotID >= 0 && slotID < this.inventorySlots.size()) {
			final Slot hullSlot = this.inventorySlots.get(slotID);
			if (hullSlot != null && hullSlot instanceof SlotHull) {
				final InventoryPlayer playerInventory = player.inventory;
				final ItemStack playerItem = playerInventory.getItemStack();
				final ItemStack slotItem = hullSlot.getStack();
				final ArrayList<SlotAssembler> newSlots = this.assembler.getValidSlotFromHullItem(playerItem);
				final ArrayList<SlotAssembler> oldSlots = this.assembler.getValidSlotFromHullItem(slotItem);
				if (oldSlots != null) {
					if (newSlots != null) {
						for (final SlotAssembler slot : newSlots) {
							final int index = oldSlots.indexOf(slot);
							if (index != -1) {
								oldSlots.remove(index);
							}
						}
					}
					for (final SlotAssembler slot : oldSlots) {
						if (slot.getHasStack()) {
							return null;
						}
					}
				}
			}
		}
		return super.slotClick(slotID, button, keyflag, player);
	}
}
