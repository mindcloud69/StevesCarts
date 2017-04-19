package vswe.stevescarts.blocks.tileentities;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import vswe.stevescarts.containers.ContainerBase;
import vswe.stevescarts.containers.ContainerCargo;
import vswe.stevescarts.containers.ContainerManager;
import vswe.stevescarts.containers.slots.*;
import vswe.stevescarts.guis.GuiBase;
import vswe.stevescarts.guis.GuiCargo;
import vswe.stevescarts.helpers.CargoItemSelection;
import vswe.stevescarts.helpers.ComponentTypes;
import vswe.stevescarts.helpers.Localization;
import vswe.stevescarts.helpers.storages.TransferHandler;
import vswe.stevescarts.helpers.storages.TransferManager;
import vswe.stevescarts.items.ModItems;

import javax.annotation.Nonnull;
import java.util.ArrayList;

public class TileEntityCargo extends TileEntityManager {
	public static ArrayList<CargoItemSelection> itemSelections;
	public int[] target;
	public ArrayList<SlotCargo> cargoSlots;
	public int lastLayout;
	private TransferManager latestTransferToBeUsed;

	@SideOnly(Side.CLIENT)
	@Override
	public GuiBase getGui(final InventoryPlayer inv) {
		return new GuiCargo(inv, this);
	}

	@Override
	public ContainerBase getContainer(final InventoryPlayer inv) {
		return new ContainerCargo(inv, this);
	}

	public TileEntityCargo() {
		target = new int[] { 0, 0, 0, 0 };
		lastLayout = -1;
	}

	public static void loadSelectionSettings() {
		(TileEntityCargo.itemSelections = new ArrayList<>()).add(new CargoItemSelection(Localization.GUI.CARGO.AREA_ALL, Slot.class, new ItemStack(ModItems.carts, 1, 0)));
		TileEntityCargo.itemSelections.add(new CargoItemSelection(Localization.GUI.CARGO.AREA_ENGINE, SlotFuel.class, new ItemStack(ModItems.modules, 1, 0)));
		TileEntityCargo.itemSelections.add(new CargoItemSelection(Localization.GUI.CARGO.AREA_RAILER, SlotBuilder.class, new ItemStack(ModItems.modules, 1, 10)));
		TileEntityCargo.itemSelections.add(new CargoItemSelection(Localization.GUI.CARGO.AREA_STORAGE, SlotChest.class, new ItemStack(Blocks.CHEST, 1)));
		TileEntityCargo.itemSelections.add(new CargoItemSelection(Localization.GUI.CARGO.AREA_TORCHES, SlotTorch.class, new ItemStack(Blocks.TORCH, 1)));
		TileEntityCargo.itemSelections.add(new CargoItemSelection(Localization.GUI.CARGO.AREA_EXPLOSIVES, ISlotExplosions.class, ComponentTypes.DYNAMITE.getItemStack()));
		TileEntityCargo.itemSelections.add(new CargoItemSelection(Localization.GUI.CARGO.AREA_ARROWS, SlotArrow.class, new ItemStack(Items.ARROW, 1)));
		TileEntityCargo.itemSelections.add(new CargoItemSelection(Localization.GUI.CARGO.AREA_BRIDGE, SlotBridge.class, new ItemStack(Blocks.BRICK_BLOCK, 1)));
		TileEntityCargo.itemSelections.add(new CargoItemSelection(Localization.GUI.CARGO.AREA_SEEDS, SlotSeed.class, new ItemStack(Items.WHEAT_SEEDS, 1)));
		TileEntityCargo.itemSelections.add(new CargoItemSelection(Localization.GUI.CARGO.AREA_FERTILIZER, SlotFertilizer.class, new ItemStack(Items.DYE, 1, 15)));
		TileEntityCargo.itemSelections.add(new CargoItemSelection(null, null, ItemStack.EMPTY));
		TileEntityCargo.itemSelections.add(new CargoItemSelection(Localization.GUI.CARGO.AREA_SAPLINGS, SlotSapling.class, new ItemStack(Blocks.SAPLING, 1)));
		TileEntityCargo.itemSelections.add(new CargoItemSelection(Localization.GUI.CARGO.AREA_FIREWORK, SlotFirework.class, new ItemStack(Items.FIREWORKS, 1)));
		TileEntityCargo.itemSelections.add(new CargoItemSelection(Localization.GUI.CARGO.AREA_BUCKETS, SlotMilker.class, new ItemStack(Items.BUCKET, 1)));
		TileEntityCargo.itemSelections.add(new CargoItemSelection(Localization.GUI.CARGO.AREA_CAKES, SlotCake.class, new ItemStack(Items.CAKE, 1)));
	}

	@Override
	public int getSizeInventory() {
		return 60;
	}

	@Override
	public String getName() {
		return "container.cargomanager";
	}

	@Override
	protected void updateLayout() {
		if (cargoSlots != null && lastLayout != layoutType) {
			for (final SlotCargo slot : cargoSlots) {
				slot.updatePosition();
			}
			lastLayout = layoutType;
		}
	}

	@Override
	protected boolean isTargetValid(final TransferManager transfer) {
		return target[transfer.getSetting()] >= 0 && target[transfer.getSetting()] < TileEntityCargo.itemSelections.size();
	}

	@Override
	protected void receiveClickData(final int packetid, final int id, final int dif) {
		if (packetid == 1) {
			final int[] target = this.target;
			target[id] += dif;
			if (this.target[id] >= TileEntityCargo.itemSelections.size()) {
				this.target[id] = 0;
			} else if (this.target[id] < 0) {
				this.target[id] = TileEntityCargo.itemSelections.size() - 1;
			}
			if (color[id] - 1 == getSide()) {
				reset();
			}
			if (TileEntityCargo.itemSelections.get(this.target[id]).getValidSlot() == null && dif != 0) {
				receiveClickData(packetid, id, dif);
			}
		}
	}

	@Override
	public void checkGuiData(final ContainerManager conManager, final IContainerListener crafting, final boolean isNew) {
		super.checkGuiData(conManager, crafting, isNew);
		final ContainerCargo con = (ContainerCargo) conManager;
		short targetShort = 0;
		for (int i = 0; i < 4; ++i) {
			targetShort |= (short) ((target[i] & 0xF) << i * 4);
		}
		if (isNew || con.lastTarget != targetShort) {
			updateGuiData(con, crafting, 2, targetShort);
			con.lastTarget = targetShort;
		}
	}

	@Override
	public void receiveGuiData(final int id, final short data) {
		if (id == 2) {
			for (int i = 0; i < 4; ++i) {
				target[i] = (data & 15 << i * 4) >> i * 4;
			}
		} else {
			super.receiveGuiData(id, data);
		}
	}

	public int getAmount(final int id) {
		final int val = getAmountId(id);
		switch (val) {
			case 1: {
				return 1;
			}
			case 2: {
				return 3;
			}
			case 3: {
				return 8;
			}
			case 4: {
				return 16;
			}
			case 5: {
				return 32;
			}
			case 6: {
				return 64;
			}
			case 7: {
				return 1;
			}
			case 8: {
				return 2;
			}
			case 9: {
				return 3;
			}
			case 10: {
				return 5;
			}
			default: {
				return 0;
			}
		}
	}

	public int getAmountType(final int id) {
		final int val = getAmountId(id);
		if (val == 0) {
			return 0;
		}
		if (val <= 6) {
			return 1;
		}
		return 2;
	}

	@Override
	public int getAmountCount() {
		return 11;
	}

	@Override
	public void readFromNBT(final NBTTagCompound nbttagcompound) {
		super.readFromNBT(nbttagcompound);
		setWorkload(nbttagcompound.getByte("workload"));
		for (int i = 0; i < 4; ++i) {
			target[i] = nbttagcompound.getByte("target" + i);
		}
	}

	@Override
	public NBTTagCompound writeToNBT(final NBTTagCompound nbttagcompound) {
		super.writeToNBT(nbttagcompound);
		nbttagcompound.setByte("workload", (byte) getWorkload());
		for (int i = 0; i < 4; ++i) {
			nbttagcompound.setByte("target" + i, (byte) target[i]);
		}
		return nbttagcompound;
	}

	@Override
	protected boolean doTransfer(final TransferManager transfer) {
		final Class slotCart = TileEntityCargo.itemSelections.get(target[transfer.getSetting()]).getValidSlot();
		if (slotCart == null) {
			transfer.setLowestSetting(transfer.getSetting() + 1);
			return true;
		}
		final Class slotCargo = SlotCargo.class;
		IInventory fromInv;
		Container fromCont;
		Class fromValid;
		IInventory toInv;
		Container toCont;
		Class toValid;
		if (toCart[transfer.getSetting()]) {
			fromInv = this;
			fromCont = new ContainerCargo(null, this);
			fromValid = slotCargo;
			toInv = transfer.getCart();
			toCont = transfer.getCart().getCon(null);
			toValid = slotCart;
		} else {
			fromInv = transfer.getCart();
			fromCont = transfer.getCart().getCon(null);
			fromValid = slotCart;
			toInv = this;
			toCont = new ContainerCargo(null, this);
			toValid = slotCargo;
		}
		latestTransferToBeUsed = transfer;
		for (int i = 0; i < fromInv.getSizeInventory(); ++i) {
			if (TransferHandler.isSlotOfType(fromCont.getSlot(i), fromValid) && !fromInv.getStackInSlot(i).isEmpty()) {
				@Nonnull
				ItemStack iStack = fromInv.getStackInSlot(i);
				final int stacksize = iStack.getCount();
				int maxNumber;
				if (getAmountType(transfer.getSetting()) == 1) {
					maxNumber = getAmount(transfer.getSetting()) - transfer.getWorkload();
				} else {
					maxNumber = -1;
				}
				TransferHandler.TransferItem(iStack, toInv, toCont, toValid, maxNumber, TransferHandler.TRANSFER_TYPE.MANAGER);
				if (iStack.getCount() != stacksize) {
					if (getAmountType(transfer.getSetting()) == 1) {
						transfer.setWorkload(transfer.getWorkload() + stacksize - iStack.getCount());
					} else if (getAmountType(transfer.getSetting()) == 2) {
						transfer.setWorkload(transfer.getWorkload() + 1);
					}
					markDirty();
					transfer.getCart().markDirty();
					if (iStack.getCount() == 0) {
						fromInv.setInventorySlotContents(i, ItemStack.EMPTY);
					}
					if (transfer.getWorkload() >= getAmount(transfer.getSetting()) && getAmountType(transfer.getSetting()) != 0) {
						transfer.setLowestSetting(transfer.getSetting() + 1);
					}
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public boolean isItemValidForSlot(final int slotId,
	                                  @Nonnull
		                                  ItemStack item) {
		return true;
	}

	public TransferManager getCurrentTransferForSlots() {
		return latestTransferToBeUsed;
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			return true;
		}
		return super.hasCapability(capability, facing);
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			return (T) new InvWrapper(this);
		}
		return super.getCapability(capability, facing);
	}
}
