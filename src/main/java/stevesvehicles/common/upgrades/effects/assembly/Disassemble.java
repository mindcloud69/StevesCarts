package stevesvehicles.common.upgrades.effects.assembly;

import java.util.List;

import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import stevesvehicles.common.blocks.tileentitys.TileEntityCartAssembler;
import stevesvehicles.common.blocks.tileentitys.TileEntityUpgrade;
import stevesvehicles.common.container.ContainerCartAssembler;
import stevesvehicles.common.container.slots.SlotAssembler;
import stevesvehicles.common.container.slots.SlotCart;
import stevesvehicles.common.container.slots.SlotModule;
import stevesvehicles.common.container.slots.SlotOutput;
import stevesvehicles.common.items.ItemVehicles;
import stevesvehicles.common.items.ModItems;
import stevesvehicles.common.modules.datas.ModuleDataItemHandler;
import stevesvehicles.common.transfer.TransferHandler;
import stevesvehicles.common.upgrades.effects.BaseEffect;
import stevesvehicles.common.upgrades.effects.util.InventoryEffect;
import stevesvehicles.common.vehicles.VehicleBase;

public class Disassemble extends InventoryEffect {
	public Disassemble(TileEntityUpgrade upgrade) {
		super(upgrade);
	}

	@Override
	public int getInventorySize() {
		return 31;
	}

	@Override
	public int getSlotX(int id) {
		if (id == 0) {
			return ((256 - 18) * 3) / 4;
		} else {
			return (256 - 18 * 10) / 2 + ((id - 1) % 10) * 18;
		}
	}

	@Override
	public int getSlotY(int id) {
		int y;
		if (id == 0) {
			y = 0;
		} else {
			y = (id - 1) / 10 + 2;
		}
		return (107 - 18 * 5) / 2 + y * 18;
	}

	@Override
	public Class<? extends Slot> getSlot(int i) {
		if (i == 0) {
			return SlotCart.class;
		} else {
			return SlotModule.class;
		}
	}

	private ItemStack lastVehicle = ItemStack.EMPTY;

	@Override
	public void onInventoryChanged() {
		ItemStack cart = upgrade.getStackInSlot(0);
		if (!updateCart(upgrade, cart)) {
			boolean needsToPuke = true;
			for (int i = 1; i < getInventorySize(); i++) {
				if (upgrade.getStackInSlot(i).isEmpty()) {
					ItemStack item = upgrade.getStackInSlot(0);
					upgrade.setInventorySlotContents(0, ItemStack.EMPTY);
					upgrade.setInventorySlotContents(i, item);
					needsToPuke = false;
					break;
				}
			}
			if (needsToPuke) {
				if (!upgrade.getWorld().isRemote) {
					upgrade.getMaster().puke(upgrade.getStackInSlot(0).copy());
				}
				upgrade.setInventorySlotContents(0, ItemStack.EMPTY);
			}
		}
	}

	@Override
	public void removed() {
		updateCart(upgrade, null);
	}

	private void resetMaster(TileEntityCartAssembler master, boolean full) {
		for (int i = 0; i < master.getSizeInventory() - master.nonModularSlots(); i++) {
			if (!master.getStackInSlot(i).isEmpty()) {
				if (master.getStackInSlot(i).getCount() <= 0) {
					master.setInventorySlotContents(i, ItemStack.EMPTY);
				} else if (full) {
					if (!master.getWorld().isRemote) {
						master.puke(master.getStackInSlot(i).copy());
					}
					master.setInventorySlotContents(i, ItemStack.EMPTY);
				}
			}
		}
	}

	private boolean updateCart(TileEntityUpgrade upgrade, ItemStack cart) {
		if (upgrade.getMaster() != null) {
			if (cart.isEmpty() || cart.getItem() != ModItems.vehicles || cart.getTagCompound() == null || cart.getTagCompound().hasKey(VehicleBase.NBT_INTERRUPT_MAX_TIME)) {
				resetMaster(upgrade.getMaster(), false);
				lastVehicle = ItemStack.EMPTY;
				if (!cart.isEmpty()) {
					upgrade.getMaster().puke(cart);
					upgrade.setInventorySlotContents(0, ItemStack.EMPTY);
				}
			} else {
				ItemStack last = lastVehicle;
				lastVehicle = cart.copy();
				int result = canDisassemble(upgrade);
				boolean reset = false;
				if (result > 0 && !last.isEmpty() && !ItemStack.areItemStacksEqual(cart, last)) {
					result = 2;
					reset = true;
				}
				if (result != 2) {
					return result == 1 && !upgrade.getMaster().getStackInSlot(0).isEmpty();
				}
				if (reset) {
					resetMaster(upgrade.getMaster(), true);
				}
				boolean addedHull = false;
				List<ItemStack> modules = ModuleDataItemHandler.getModularItems(cart);
				for (ItemStack item : modules) {
					item.setCount(1);
					TransferHandler.TransferItem(item, upgrade.getMaster(), new ContainerCartAssembler(null, upgrade.getMaster()), SlotAssembler.class, SlotOutput.class, 1);
					if (!addedHull) {
						addedHull = true;
						upgrade.getMaster().updateSlots();
					}
				}
			}
		}
		return true;
	}

	public int canDisassemble(TileEntityUpgrade upgrade) {
		int disassembleCount = 0;
		for (BaseEffect effect : upgrade.getMaster().getEffects()) {
			if (effect instanceof Disassemble) {
				disassembleCount++;
			}
		}
		if (disassembleCount != 1) {
			return 0;
		}
		for (int i = 0; i < upgrade.getMaster().getSizeInventory() - upgrade.getMaster().nonModularSlots(); i++) {
			if (!upgrade.getMaster().getStackInSlot(i).isEmpty() && upgrade.getMaster().getStackInSlot(i).getCount() <= 0) {
				return 1;
			}
		}
		for (int i = 0; i < upgrade.getMaster().getSizeInventory() - upgrade.getMaster().nonModularSlots(); i++) {
			if (!upgrade.getMaster().getStackInSlot(i).isEmpty()) {
				return 0;
			}
		}
		return 2;
	}

	public void onVehicleCreation(ItemStack vehicle) {
		ItemStack oldCart = getStack(0);
		if (!oldCart.isEmpty() && oldCart.getItem() instanceof ItemVehicles) {
			if (oldCart.hasDisplayName()) {
				vehicle.setStackDisplayName(oldCart.getDisplayName());
			}
		}
		setStack(0, ItemStack.EMPTY);
	}
}
