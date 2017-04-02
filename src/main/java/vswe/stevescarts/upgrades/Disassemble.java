package vswe.stevescarts.upgrades;

import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import vswe.stevescarts.blocks.tileentities.TileEntityCartAssembler;
import vswe.stevescarts.blocks.tileentities.TileEntityUpgrade;
import vswe.stevescarts.containers.ContainerCartAssembler;
import vswe.stevescarts.containers.slots.SlotCart;
import vswe.stevescarts.containers.slots.SlotModule;
import vswe.stevescarts.helpers.Localization;
import vswe.stevescarts.helpers.storages.TransferHandler;
import vswe.stevescarts.items.ModItems;
import vswe.stevescarts.modules.data.ModuleData;

import javax.annotation.Nonnull;
import java.util.ArrayList;

public class Disassemble extends InventoryEffect {
	@Override
	public int getInventorySize() {
		return 31;
	}

	@Override
	public int getSlotX(final int id) {
		if (id == 0) {
			return 178;
		}
		return 38 + (id - 1) % 10 * 18;
	}

	@Override
	public int getSlotY(final int id) {
		int y;
		if (id == 0) {
			y = 0;
		} else {
			y = (id - 1) / 10 + 2;
		}
		return 8 + y * 18;
	}

	@Override
	public Class<? extends Slot> getSlot(final int i) {
		if (i == 0) {
			return SlotCart.class;
		}
		return SlotModule.class;
	}

	@Override
	public void load(final TileEntityUpgrade upgrade, final NBTTagCompound compound) {
		this.setLastCart(upgrade, upgrade.getStackInSlot(0));
	}

	@Override
	public String getName() {
		return Localization.UPGRADES.DISASSEMBLE.translate();
	}

	@Override
	public void onInventoryChanged(final TileEntityUpgrade upgrade) {
		@Nonnull
		ItemStack cart = upgrade.getStackInSlot(0);
		if (!this.updateCart(upgrade, cart)) {
			boolean needsToPuke = true;
			for (int i = 1; i < this.getInventorySize(); ++i) {
				if (upgrade.getStackInSlot(i).isEmpty()) {
					@Nonnull
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
	public void removed(final TileEntityUpgrade upgrade) {
		this.updateCart(upgrade, null);
	}

	private void resetMaster(final TileEntityCartAssembler master, final boolean full) {
		for (int i = 0; i < master.getSizeInventory() - master.nonModularSlots(); ++i) {
			if (!master.getStackInSlot(i).isEmpty()) {
				if (master.getStackInSlot(i).getCount() <= 0) {
					master.setInventorySlotContents(i, null);
				} else if (full) {
					if (!master.getWorld().isRemote) {
						master.puke(master.getStackInSlot(i).copy());
					}
					master.setInventorySlotContents(i, null);
				}
			}
		}
	}

	private void setLastCart(final TileEntityUpgrade upgrade,
	                         @Nonnull
		                         ItemStack cart) {
		if (cart == null) {
			upgrade.getCompound().setShort("id", (short) 0);
		} else {
			cart.writeToNBT(upgrade.getCompound());
		}
	}

	@Nonnull
	private ItemStack getLastCart(final TileEntityUpgrade upgrade) {
		return new ItemStack(upgrade.getCompound());
	}

	private boolean updateCart(final TileEntityUpgrade upgrade,
	                           @Nonnull
		                           ItemStack cart) {
		if (upgrade.getMaster() != null) {
			if (cart.isEmpty() || cart.getItem() != ModItems.carts || cart.getTagCompound() == null || cart.getTagCompound().hasKey("maxTime")) {
				this.resetMaster(upgrade.getMaster(), false);
				this.setLastCart(upgrade, ItemStack.EMPTY);
				if (!cart.isEmpty()) {
					upgrade.getMaster().puke(cart);
					upgrade.setInventorySlotContents(0, ItemStack.EMPTY);
				}
			} else {
				@Nonnull
				ItemStack last = this.getLastCart(upgrade);
				this.setLastCart(upgrade, cart);
				int result = this.canDisassemble(upgrade);
				boolean reset = false;
				if (result > 0 && !last.isEmpty() && !ItemStack.areItemStacksEqual(cart, last)) {
					result = 2;
					reset = true;
				}
				if (result != 2) {
					return result == 1 && !upgrade.getMaster().getStackInSlot(0).isEmpty();
				}
				if (reset) {
					this.resetMaster(upgrade.getMaster(), true);
				}
				boolean addedHull = false;
				final NonNullList<ItemStack> modules = ModuleData.getModularItems(cart);
				for (
					@Nonnull
						ItemStack item : modules) {
					item.setCount(0);
					TransferHandler.TransferItem(item, upgrade.getMaster(), new ContainerCartAssembler(null, upgrade.getMaster()), 1);
					if (!addedHull) {
						addedHull = true;
						upgrade.getMaster().updateSlots();
					}
				}
			}
		}
		return true;
	}

	public int canDisassemble(final TileEntityUpgrade upgrade) {
		int disassembleCount = 0;
		for (final BaseEffect effect : upgrade.getMaster().getEffects()) {
			if (effect instanceof Disassemble) {
				++disassembleCount;
			}
		}
		if (disassembleCount != 1) {
			return 0;
		}
		for (int i = 0; i < upgrade.getMaster().getSizeInventory() - upgrade.getMaster().nonModularSlots(); ++i) {
			if (!upgrade.getMaster().getStackInSlot(i) .isEmpty() && upgrade.getMaster().getStackInSlot(i).getCount() <= 0) {
				return 1;
			}
		}
		for (int i = 0; i < upgrade.getMaster().getSizeInventory() - upgrade.getMaster().nonModularSlots(); ++i) {
			if (!upgrade.getMaster().getStackInSlot(i).isEmpty()) {
				return 0;
			}
		}
		return 2;
	}
}
