package vswe.stevescarts.upgrades;

import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import vswe.stevescarts.blocks.tileentities.TileEntityCartAssembler;
import vswe.stevescarts.blocks.tileentities.TileEntityUpgrade;
import vswe.stevescarts.containers.ContainerCartAssembler;
import vswe.stevescarts.containers.slots.SlotAssemblerFuel;
import vswe.stevescarts.containers.slots.SlotModule;
import vswe.stevescarts.helpers.Localization;
import vswe.stevescarts.helpers.storages.TransferHandler;
import vswe.stevescarts.items.ModItems;
import vswe.stevescarts.modules.data.ModuleData;
import vswe.stevescarts.modules.data.ModuleDataHull;

import java.util.ArrayList;

public class InputChest extends SimpleInventoryEffect {
	public InputChest(final int inventoryWidth, final int inventoryHeight) {
		super(inventoryWidth, inventoryHeight);
	}

	@Override
	public String getName() {
		return Localization.UPGRADES.INPUT_CHEST.translate(String.valueOf(this.getInventorySize()));
	}

	@Override
	public void init(final TileEntityUpgrade upgrade) {
		upgrade.getCompound().setByte("TransferCooldown", (byte) 0);
	}

	@Override
	public Class<? extends Slot> getSlot(final int i) {
		return SlotModule.class;
	}

	@Override
	public void update(final TileEntityUpgrade upgrade) {
		if (!upgrade.getWorld().isRemote && upgrade.getMaster() != null) {
			final NBTTagCompound comp = upgrade.getCompound();
			if (comp.getByte("TransferCooldown") != 0) {
				comp.setByte("TransferCooldown", (byte) (comp.getByte("TransferCooldown") - 1));
			} else {
				comp.setByte("TransferCooldown", (byte) 20);
				for (int slotId = 0; slotId < upgrade.getUpgrade().getInventorySize(); ++slotId) {
					@Nonnull
					ItemStack itemstack = upgrade.getStackInSlot(slotId);
					if (itemstack != null) {
						final ModuleData module = ModItems.modules.getModuleData(itemstack);
						if (module != null) {
							if (this.isValidForBluePrint(upgrade.getMaster(), module)) {
								if (!this.willInvalidate(upgrade.getMaster(), module)) {
									final int stackSize = itemstack.stackSize;
									TransferHandler.TransferItem(itemstack, upgrade.getMaster(), new ContainerCartAssembler(null, upgrade.getMaster()), Slot.class, SlotAssemblerFuel.class, 1);
									if (itemstack.stackSize == 0) {
										upgrade.setInventorySlotContents(slotId, null);
									}
									if (stackSize != itemstack.stackSize) {
										break;
									}
								}
							}
						}
					}
				}
			}
		}
	}

	private boolean willInvalidate(final TileEntityCartAssembler assembler, final ModuleData module) {
		final ModuleDataHull hull = assembler.getHullModule();
		if (hull == null) {
			return false;
		}
		final ArrayList<ModuleData> modules = assembler.getNonHullModules();
		modules.add(module);
		return ModuleData.checkForErrors(hull, modules) != null;
	}

	private boolean isValidForBluePrint(final TileEntityCartAssembler assembler, final ModuleData module) {
		for (final TileEntityUpgrade tile : assembler.getUpgradeTiles()) {
			for (final BaseEffect effect : tile.getUpgrade().getEffects()) {
				if (effect instanceof Blueprint) {
					return ((Blueprint) effect).isValidForBluePrint(tile, assembler.getModules(true), module);
				}
			}
		}
		return true;
	}
}
