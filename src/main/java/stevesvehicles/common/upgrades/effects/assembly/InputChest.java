package stevesvehicles.common.upgrades.effects.assembly;

import java.util.ArrayList;

import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import stevesvehicles.common.blocks.tileentitys.TileEntityCartAssembler;
import stevesvehicles.common.blocks.tileentitys.TileEntityUpgrade;
import stevesvehicles.common.container.ContainerCartAssembler;
import stevesvehicles.common.container.slots.SlotAssemblerFuel;
import stevesvehicles.common.container.slots.SlotModule;
import stevesvehicles.common.items.ModItems;
import stevesvehicles.common.modules.datas.ModuleData;
import stevesvehicles.common.modules.datas.ModuleDataHull;
import stevesvehicles.common.modules.datas.ModuleDataItemHandler;
import stevesvehicles.common.transfer.TransferHandler;
import stevesvehicles.common.upgrades.effects.BaseEffect;
import stevesvehicles.common.upgrades.effects.util.SimpleInventoryEffect;

public class InputChest extends SimpleInventoryEffect {
	public InputChest(TileEntityUpgrade upgrade, Integer inventoryWidth, Integer inventoryHeight) {
		super(upgrade, inventoryWidth, inventoryHeight);
	}

	private int cooldown;

	@Override
	public void init() {
		cooldown = 0;
	}

	@Override
	public Class<? extends Slot> getSlot(int i) {
		return SlotModule.class;
	}

	@Override
	public void update() {
		if (!upgrade.getWorld().isRemote && upgrade.getMaster() != null) {
			if (cooldown > 0) {
				cooldown--;
			} else {
				cooldown = 20;
				for (int slotId = 0; slotId < getInventorySize(); slotId++) {
					ItemStack itemstack = upgrade.getStackInSlot(slotId);
					if (!itemstack.isEmpty()) {
						ModuleData module = ModItems.modules.getModuleData(itemstack);
						if (module == null) {
							continue;
						}
						if (!isValidForBluePrint(upgrade.getMaster(), module)) {
							continue;
						}
						if (willInvalidate(upgrade.getMaster(), module)) {
							continue;
						}
						int stackSize = itemstack.getCount();
						TransferHandler.TransferItem(itemstack, upgrade.getMaster(), new ContainerCartAssembler(null, upgrade.getMaster()), Slot.class, SlotAssemblerFuel.class, 1);
						if (itemstack.getCount() == 0) {
							upgrade.setInventorySlotContents(slotId, ItemStack.EMPTY);
						}
						if (stackSize != itemstack.getCount()) {
							break;
						}
					}
				}
			}
		}
	}

	private boolean willInvalidate(TileEntityCartAssembler assembler, ModuleData module) {
		ModuleDataHull hull = assembler.getHullModule();
		if (hull == null) {
			return false;
		}
		ArrayList<ModuleData> modules = assembler.getNonHullModules();
		modules.add(module);
		if (ModuleDataItemHandler.checkForErrors(hull, modules) != null) {
			return true;
		}
		return false;
	}

	private boolean isValidForBluePrint(TileEntityCartAssembler assembler, ModuleData module) {
		for (BaseEffect effect : assembler.getEffects()) {
			if (effect instanceof Blueprint) {
				return ((Blueprint) effect).isValidForBluePrint(assembler.getModules(true), module);
			}
		}
		return true;
	}
}
