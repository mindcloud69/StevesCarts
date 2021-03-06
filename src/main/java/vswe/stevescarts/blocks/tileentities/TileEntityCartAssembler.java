package vswe.stevescarts.blocks.tileentities;

import net.minecraft.block.BlockRailBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vswe.stevescarts.Constants;
import vswe.stevescarts.SCConfig;
import vswe.stevescarts.blocks.BlockCartAssembler;
import vswe.stevescarts.blocks.ModBlocks;
import vswe.stevescarts.containers.ContainerBase;
import vswe.stevescarts.containers.ContainerCartAssembler;
import vswe.stevescarts.containers.ContainerUpgrade;
import vswe.stevescarts.containers.slots.SlotAssembler;
import vswe.stevescarts.containers.slots.SlotAssemblerFuel;
import vswe.stevescarts.containers.slots.SlotHull;
import vswe.stevescarts.containers.slots.SlotOutput;
import vswe.stevescarts.entitys.EntityMinecartModular;
import vswe.stevescarts.guis.GuiBase;
import vswe.stevescarts.guis.GuiCartAssembler;
import vswe.stevescarts.helpers.*;
import vswe.stevescarts.helpers.storages.TransferHandler;
import vswe.stevescarts.helpers.storages.TransferManager;
import vswe.stevescarts.items.ItemCarts;
import vswe.stevescarts.items.ModItems;
import vswe.stevescarts.modules.data.ModuleData;
import vswe.stevescarts.modules.data.ModuleDataHull;
import vswe.stevescarts.upgrades.*;

import javax.annotation.Nonnull;
import java.util.ArrayList;

public class TileEntityCartAssembler extends TileEntityBase implements IInventory, ISidedInventory {
	private int maxAssemblingTime;
	private float currentAssemblingTime;
	@Nonnull
	protected ItemStack outputItem = ItemStack.EMPTY;
	protected NonNullList<ItemStack> spareModules;
	private boolean isAssembling;
	public boolean isErrorListOutdated;
	private ArrayList<TitleBox> titleBoxes;
	private ArrayList<DropDownMenuItem> dropDownItems;
	private SimulationInfo info;
	private boolean shouldSpin;
	private EntityMinecartModular placeholder;
	private float yaw;
	private float roll;
	private boolean rolldown;
	private ArrayList<SlotAssembler> slots;
	private ArrayList<SlotAssembler> engineSlots;
	private ArrayList<SlotAssembler> addonSlots;
	private ArrayList<SlotAssembler> chestSlots;
	private ArrayList<SlotAssembler> funcSlots;
	private SlotHull hullSlot;
	private SlotAssembler toolSlot;
	private SlotOutput outputSlot;
	private SlotAssemblerFuel fuelSlot;
	private final int[] topbotSlots;
	private final int[] sideSlots;
	@Nonnull
	private ItemStack lastHull = ItemStack.EMPTY;
	private float fuelLevel;
	private ArrayList<TileEntityUpgrade> upgrades;
	public boolean isDead;
	private boolean loaded;
	NonNullList<ItemStack> inventoryStacks;

	@SideOnly(Side.CLIENT)
	@Override
	public GuiBase getGui(final InventoryPlayer inv) {
		return new GuiCartAssembler(inv, this);
	}

	@Override
	public ContainerBase getContainer(final InventoryPlayer inv) {
		return new ContainerCartAssembler(inv, this);
	}

	public TileEntityCartAssembler() {
		currentAssemblingTime = -1.0f;
		shouldSpin = true;
		yaw = 0.0f;
		roll = 0.0f;
		rolldown = false;
		upgrades = new ArrayList<>();
		spareModules = NonNullList.create();
		dropDownItems = new ArrayList<>();
		slots = new ArrayList<>();
		engineSlots = new ArrayList<>();
		addonSlots = new ArrayList<>();
		chestSlots = new ArrayList<>();
		funcSlots = new ArrayList<>();
		titleBoxes = new ArrayList<>();
		int slotID = 0;
		hullSlot = new SlotHull(this, slotID++, 18, 25);
		slots.add(hullSlot);
		final TitleBox engineBox = new TitleBox(0, 65, 16225309);
		final TitleBox toolBox = new TitleBox(1, 100, 6696337);
		final TitleBox attachBox = new TitleBox(2, 135, 23423);
		final TitleBox storageBox = new TitleBox(3, 170, 10357518);
		final TitleBox addonBox = new TitleBox(4, 205, 22566);
		final TitleBox infoBox = new TitleBox(5, 375, 30, 13417984);
		titleBoxes.add(engineBox);
		titleBoxes.add(toolBox);
		titleBoxes.add(attachBox);
		titleBoxes.add(storageBox);
		titleBoxes.add(addonBox);
		titleBoxes.add(infoBox);
		for (int i = 0; i < 5; ++i) {
			final SlotAssembler slot = new SlotAssembler(this, slotID++, engineBox.getX() + 2 + 18 * i, engineBox.getY(), 1, false, i);
			slot.invalidate();
			slots.add(slot);
			engineSlots.add(slot);
		}
		toolSlot = new SlotAssembler(this, slotID++, toolBox.getX() + 2, toolBox.getY(), 2, false, 0);
		slots.add(toolSlot);
		toolSlot.invalidate();
		for (int i = 0; i < 6; ++i) {
			final SlotAssembler slot = new SlotAssembler(this, slotID++, attachBox.getX() + 2 + 18 * i, attachBox.getY(), -1, false, i);
			slot.invalidate();
			slots.add(slot);
			funcSlots.add(slot);
		}
		for (int i = 0; i < 4; ++i) {
			final SlotAssembler slot = new SlotAssembler(this, slotID++, storageBox.getX() + 2 + 18 * i, storageBox.getY(), 3, false, i);
			slot.invalidate();
			slots.add(slot);
			chestSlots.add(slot);
		}
		for (int i = 0; i < 12; ++i) {
			final SlotAssembler slot = new SlotAssembler(this, slotID++, addonBox.getX() + 2 + 18 * (i % 6), addonBox.getY() + 18 * (i / 6), 4, false, i);
			slot.invalidate();
			slots.add(slot);
			addonSlots.add(slot);
		}
		fuelSlot = new SlotAssemblerFuel(this, slotID++, 395, 220);
		slots.add(fuelSlot);
		outputSlot = new SlotOutput(this, slotID++, 450, 220);
		slots.add(outputSlot);
		info = new SimulationInfo();
		inventoryStacks = NonNullList.withSize(slots.size(), ItemStack.EMPTY);
		topbotSlots = new int[] { getSizeInventory() - nonModularSlots() };
		sideSlots = new int[] { getSizeInventory() - nonModularSlots() + 1 };
	}

	public void clearUpgrades() {
		upgrades.clear();
	}

	public void addUpgrade(final TileEntityUpgrade upgrade) {
		upgrades.add(upgrade);
	}

	public void removeUpgrade(final TileEntityUpgrade upgrade) {
		upgrades.remove(upgrade);
	}

	public ArrayList<TileEntityUpgrade> getUpgradeTiles() {
		return upgrades;
	}

	public ArrayList<AssemblerUpgrade> getUpgrades() {
		final ArrayList<AssemblerUpgrade> lst = new ArrayList<>();
		for (final TileEntityUpgrade tile : upgrades) {
			lst.add(tile.getUpgrade());
		}
		return lst;
	}

	public ArrayList<BaseEffect> getEffects() {
		final ArrayList<BaseEffect> lst = new ArrayList<>();
		for (final TileEntityUpgrade tile : upgrades) {
			final AssemblerUpgrade upgrade = tile.getUpgrade();
			if (upgrade != null) {
				for (final BaseEffect effect : upgrade.getEffects()) {
					lst.add(effect);
				}
			}
		}
		return lst;
	}

	public SimulationInfo getSimulationInfo() {
		return info;
	}

	public ArrayList<DropDownMenuItem> getDropDown() {
		return dropDownItems;
	}

	public ArrayList<TitleBox> getTitleBoxes() {
		return titleBoxes;
	}

	public static int getRemovedSize() {
		return -1;
	}

	public static int getKeepSize() {
		return 0;
	}

	public ArrayList<SlotAssembler> getSlots() {
		return slots;
	}

	public ArrayList<SlotAssembler> getEngines() {
		return engineSlots;
	}

	public ArrayList<SlotAssembler> getChests() {
		return chestSlots;
	}

	public ArrayList<SlotAssembler> getAddons() {
		return addonSlots;
	}

	public ArrayList<SlotAssembler> getFuncs() {
		return funcSlots;
	}

	public SlotAssembler getToolSlot() {
		return toolSlot;
	}

	public int getMaxAssemblingTime() {
		return maxAssemblingTime;
	}

	public int getAssemblingTime() {
		return (int) currentAssemblingTime;
	}

	private void setAssemblingTime(final int val) {
		currentAssemblingTime = val;
	}

	public boolean getIsAssembling() {
		return isAssembling;
	}

	public void doAssemble() {
		if (!hasErrors()) {
			maxAssemblingTime = generateAssemblingTime();
			createCartFromModules();
			isAssembling = true;
			for (final TileEntityUpgrade tile : getUpgradeTiles()) {
				if (tile.getUpgrade() != null) {
					for (final BaseEffect effect : tile.getUpgrade().getEffects()) {
						if (effect instanceof Disassemble) {
							@Nonnull
							ItemStack oldcart = tile.getStackInSlot(0);
							if (!oldcart.isEmpty() && oldcart.getItem() instanceof ItemCarts && oldcart.hasDisplayName()) {
								outputItem.setStackDisplayName(oldcart.getDisplayName());
							}
							tile.setInventorySlotContents(0, ItemStack.EMPTY);
						}
					}
				}
			}
		}
	}

	@Override
	public void receivePacket(final int id, final byte[] data, final EntityPlayer player) {
		if (id == 0) {
			doAssemble();
		} else if (id == 1) {
			final int slotId = data[0];
			if (slotId >= 1 && slotId < getSlots().size()) {
				final SlotAssembler slot = getSlots().get(slotId);
				if (!slot.getStack().isEmpty()) {
					if (slot.getStack().getCount() == getKeepSize()) {
						slot.getStack().setCount(getRemovedSize());
					} else {
						slot.getStack().setCount(getKeepSize());
					}
				}
			}
		}
	}

	public void onUpgradeUpdate() {
	}

	public int generateAssemblingTime() {
		if (SCConfig.disableTimedCrafting) {
			return 1;
		}
		return generateAssemblingTime(getModules(true, new int[] { getKeepSize(), getRemovedSize() }), getModules(true, new int[] { getKeepSize(), 1 }));
	}

	private int generateAssemblingTime(final ArrayList<ModuleData> modules, final ArrayList<ModuleData> removed) {
		int timeRequired = 100;
		for (final ModuleData module : modules) {
			timeRequired += getAssemblingTime(module, false);
		}
		for (final ModuleData module : removed) {
			timeRequired += getAssemblingTime(module, true);
		}
		for (final BaseEffect effect : getEffects()) {
			if (effect instanceof TimeFlatCart) {
				timeRequired += ((TimeFlatCart) effect).getTicks();
			}
		}
		return Math.max(0, timeRequired);
	}

	private int getAssemblingTime(final ModuleData module, final boolean isRemoved) {
		int time = (int) (5.0 * Math.pow(module.getCost(), 2.2));
		time += getTimeDecreased(isRemoved);
		return Math.max(0, time);
	}

	@Nonnull
	public ItemStack getCartFromModules(final boolean isSimulated) {
		final NonNullList<ItemStack> items = NonNullList.create();
		for (int i = 0; i < getSizeInventory() - nonModularSlots(); ++i) {
			@Nonnull
			ItemStack item = getStackInSlot(i);
			if (!item.isEmpty()) {
				if (item.getCount() != getRemovedSize()) {
					items.add(item);
				} else if (!isSimulated) {
					@Nonnull
					ItemStack spare = item.copy();
					spare.setCount(1);
					spareModules.add(spare);
				}
			}
		}
		return ModuleData.createModularCartFromItems(items);
	}

	private void createCartFromModules() {
		spareModules.clear();
		outputItem = getCartFromModules(false);
		if (!outputItem.isEmpty()) {
			for (int i = 0; i < getSizeInventory() - nonModularSlots(); ++i) {
				setInventorySlotContents(i, ItemStack.EMPTY);
			}
		} else {
			spareModules.clear();
		}
	}

	public ArrayList<ModuleData> getNonHullModules() {
		return getModules(false);
	}

	public ArrayList<ModuleData> getModules(final boolean includeHull) {
		return getModules(includeHull, new int[] { getRemovedSize() });
	}

	public ArrayList<ModuleData> getModules(final boolean includeHull, final int[] invalid) {
		final ArrayList<ModuleData> modules = new ArrayList<>();
		for (int i = includeHull ? 0 : 1; i < getSizeInventory() - nonModularSlots(); ++i) {
			@Nonnull
			ItemStack item = getStackInSlot(i);
			if (!item.isEmpty()) {
				boolean validSize = true;
				for (int j = 0; j < invalid.length; ++j) {
					if (invalid[j] == item.getCount() || (invalid[j] > 0 && item.getCount() > 0)) {
						validSize = false;
						break;
					}
				}
				if (validSize) {
					final ModuleData module = ModItems.modules.getModuleData(item, true);
					if (module != null) {
						modules.add(module);
					}
				}
			}
		}
		return modules;
	}

	public ModuleDataHull getHullModule() {
		if (!getStackInSlot(0).isEmpty()) {
			final ModuleData hulldata = ModItems.modules.getModuleData(getStackInSlot(0));
			if (hulldata instanceof ModuleDataHull) {
				return (ModuleDataHull) hulldata;
			}
		}
		return null;
	}

	private boolean hasErrors() {
		return getErrors().size() > 0;
	}

	public ArrayList<String> getErrors() {
		final ArrayList<String> errors = new ArrayList<>();
		if (hullSlot.getStack().isEmpty()) {
			errors.add(Localization.GUI.ASSEMBLER.HULL_ERROR.translate());
		} else {
			final ModuleData hulldata = ModItems.modules.getModuleData(getStackInSlot(0));
			if (hulldata == null || !(hulldata instanceof ModuleDataHull)) {
				errors.add(Localization.GUI.ASSEMBLER.INVALID_HULL_SHORT.translate());
			} else {
				if (isAssembling) {
					errors.add(Localization.GUI.ASSEMBLER.BUSY.translate());
				} else if (outputSlot != null && !outputSlot.getStack().isEmpty()) {
					errors.add(Localization.GUI.ASSEMBLER.DEPARTURE_BAY.translate());
				}
				final ArrayList<ModuleData> modules = new ArrayList<>();
				for (int i = 0; i < getSizeInventory() - nonModularSlots(); ++i) {
					if (!getStackInSlot(i).isEmpty()) {
						final ModuleData data = ModItems.modules.getModuleData(getStackInSlot(i));
						if (data != null) {
							modules.add(data);
						}
					}
				}
				final String error = ModuleData.checkForErrors((ModuleDataHull) hulldata, modules);
				if (error != null) {
					errors.add(error);
				}
			}
		}
		return errors;
	}

	public int getTotalCost() {
		final ArrayList<ModuleData> modules = new ArrayList<>();
		for (int i = 0; i < getSizeInventory() - nonModularSlots(); ++i) {
			if (!getStackInSlot(i).isEmpty()) {
				final ModuleData data = ModItems.modules.getModuleData(getStackInSlot(i));
				if (data != null) {
					modules.add(data);
				}
			}
		}
		return ModuleData.getTotalCost(modules);
	}

	@Override
	public void initGuiData(final Container con, final IContainerListener crafting) {
		updateGuiData(con, crafting, 0, getShortFromInt(true, maxAssemblingTime));
		updateGuiData(con, crafting, 1, getShortFromInt(false, maxAssemblingTime));
		updateGuiData(con, crafting, 2, getShortFromInt(true, getAssemblingTime()));
		updateGuiData(con, crafting, 3, getShortFromInt(false, getAssemblingTime()));
		updateGuiData(con, crafting, 4, (short) (isAssembling ? 1 : 0));
		updateGuiData(con, crafting, 5, getShortFromInt(true, getFuelLevel()));
		updateGuiData(con, crafting, 6, getShortFromInt(false, getFuelLevel()));
	}

	@Override
	public void checkGuiData(final Container container, final IContainerListener crafting) {
		final ContainerCartAssembler con = (ContainerCartAssembler) container;
		if (con.lastMaxAssemblingTime != maxAssemblingTime) {
			updateGuiData(con, crafting, 0, getShortFromInt(true, maxAssemblingTime));
			updateGuiData(con, crafting, 1, getShortFromInt(false, maxAssemblingTime));
			con.lastMaxAssemblingTime = maxAssemblingTime;
		}
		if (con.lastIsAssembling != isAssembling) {
			updateGuiData(con, crafting, 4, (short) (isAssembling ? 1 : 0));
			con.lastIsAssembling = isAssembling;
		}
		if (con.lastFuelLevel != getFuelLevel()) {
			updateGuiData(con, crafting, 5, getShortFromInt(true, getFuelLevel()));
			updateGuiData(con, crafting, 6, getShortFromInt(false, getFuelLevel()));
			con.lastFuelLevel = getFuelLevel();
		}
	}

	@Override
	public void receiveGuiData(final int id, final short data) {
		if (id == 0) {
			maxAssemblingTime = getIntFromShort(true, maxAssemblingTime, data);
		} else if (id == 1) {
			maxAssemblingTime = getIntFromShort(false, maxAssemblingTime, data);
		} else if (id == 2) {
			setAssemblingTime(getIntFromShort(true, getAssemblingTime(), data));
		} else if (id == 3) {
			setAssemblingTime(getIntFromShort(false, getAssemblingTime(), data));
		} else if (id == 4) {
			if (!(isAssembling = (data != 0))) {
				setAssemblingTime(0);
			}
		} else if (id == 5) {
			setFuelLevel(getIntFromShort(true, getFuelLevel(), data));
		} else if (id == 6) {
			setFuelLevel(getIntFromShort(false, getFuelLevel(), data));
		}
	}

	private void invalidateAll() {
		for (int i = 0; i < getEngines().size(); ++i) {
			getEngines().get(i).invalidate();
		}
		for (int i = 0; i < getAddons().size(); ++i) {
			getAddons().get(i).invalidate();
		}
		for (int i = 0; i < getChests().size(); ++i) {
			getChests().get(i).invalidate();
		}
		for (int i = 0; i < getFuncs().size(); ++i) {
			getFuncs().get(i).invalidate();
		}
		getToolSlot().invalidate();
	}

	private void validateAll() {
		if (hullSlot == null) {
			return;
		}
		final ArrayList<SlotAssembler> slots = getValidSlotFromHullItem(hullSlot.getStack());
		if (slots != null) {
			for (final SlotAssembler slot : slots) {
				slot.validate();
			}
		}
	}

	public ArrayList<SlotAssembler> getValidSlotFromHullItem(
		@Nonnull
			ItemStack hullitem) {
		if (!hullitem.isEmpty()) {
			final ModuleData data = ModItems.modules.getModuleData(hullitem);
			if (data != null && data instanceof ModuleDataHull) {
				final ModuleDataHull hull = (ModuleDataHull) data;
				return getValidSlotFromHull(hull);
			}
		}
		return null;
	}

	private ArrayList<SlotAssembler> getValidSlotFromHull(final ModuleDataHull hull) {
		final ArrayList<SlotAssembler> slots = new ArrayList<>();
		for (int i = 0; i < hull.getEngineMax(); ++i) {
			slots.add(getEngines().get(i));
		}
		for (int i = 0; i < hull.getAddonMax(); ++i) {
			slots.add(getAddons().get(i));
		}
		for (int i = 0; i < getChests().size(); ++i) {
			slots.add(getChests().get(i));
		}
		for (int i = 0; i < getFuncs().size(); ++i) {
			slots.add(getFuncs().get(i));
		}
		slots.add(getToolSlot());
		return slots;
	}

	public int getMaxFuelLevel() {
		int capacity = 4000;
		for (final BaseEffect effect : getEffects()) {
			if (effect instanceof FuelCapacity) {
				capacity += ((FuelCapacity) effect).getFuelCapacity();
			}
		}
		if (capacity > 200000) {
			capacity = 200000;
		} else if (capacity < 1) {
			capacity = 1;
		}
		return capacity;
	}

	public boolean isCombustionFuelValid() {
		for (final BaseEffect effect : getEffects()) {
			if (effect instanceof CombustionFuel) {
				return true;
			}
		}
		return false;
	}

	public int getFuelLevel() {
		return (int) fuelLevel;
	}

	public void setFuelLevel(final int val) {
		fuelLevel = val;
	}

	private int getTimeDecreased(final boolean isRemoved) {
		int timeDecr = 0;
		for (final BaseEffect effect : getEffects()) {
			if (effect instanceof TimeFlat && !(effect instanceof TimeFlatRemoved)) {
				timeDecr += ((TimeFlat) effect).getTicks();
			}
		}
		if (isRemoved) {
			for (final BaseEffect effect : getEffects()) {
				if (effect instanceof TimeFlatRemoved) {
					timeDecr += ((TimeFlat) effect).getTicks();
				}
			}
		}
		return timeDecr;
	}

	private float getFuelCost() {
		float cost = 1.0f;
		for (final BaseEffect effect : getEffects()) {
			if (effect instanceof FuelCost) {
				cost += ((FuelCost) effect).getCost();
			}
		}
		if (cost < 0.05f) {}
		return cost;
	}

	public float getEfficiency() {
		float efficiency = 1.0f;
		for (final BaseEffect effect : getEffects()) {
			if (effect instanceof WorkEfficiency) {
				efficiency += ((WorkEfficiency) effect).getEfficiency();
			}
		}
		return efficiency;
	}

	private void deployCart() {
		for (final TileEntityUpgrade tile : getUpgradeTiles()) {
			for (final BaseEffect effect : tile.getUpgrade().getEffects()) {
				if (effect instanceof Deployer) {
					BlockPos tilePos = tile.getPos();
					final int xPos = 2 * tilePos.getX() - pos.getX();
					int yPos = 2 * tilePos.getY() - pos.getY();
					final int zPos = 2 * tilePos.getZ() - pos.getZ();
					if (tilePos.getY() > pos.getY()) {
						++yPos;
					}
					if (!BlockRailBase.isRailBlock(world, new BlockPos(xPos, yPos, zPos))) {
						continue;
					}
					try {
						final NBTTagCompound info = outputItem.getTagCompound();
						if (info != null) {
							final EntityMinecartModular cart = new EntityMinecartModular(world, xPos + 0.5f, yPos + 0.5f, zPos + 0.5f, info, outputItem.getDisplayName());
							world.spawnEntity(cart);
							cart.temppushX = tilePos.getX() - pos.getX();
							cart.temppushZ = tilePos.getZ() - pos.getZ();
							managerInteract(cart, true);
							return;
						}
						continue;
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
		outputSlot.putStack(outputItem);
	}

	public void managerInteract(final EntityMinecartModular cart, final boolean toCart) {
		for (final TileEntityUpgrade tile : getUpgradeTiles()) {
			for (final BaseEffect effect : tile.getUpgrade().getEffects()) {
				if (effect instanceof Manager) {
					BlockPos tilePos = tile.getPos();
					final int xPos = 2 * tilePos.getX() - pos.getX();
					int yPos = 2 * tilePos.getY() - pos.getY();
					final int zPos = 2 * tilePos.getZ() - pos.getZ();
					if (tilePos.getY() > pos.getY()) {
						++yPos;
					}
					final TileEntity managerentity = world.getTileEntity(new BlockPos(xPos, yPos, zPos));
					if (managerentity == null || !(managerentity instanceof TileEntityManager)) {
						continue;
					}
					final TransferManager transfer = new TransferManager();
					transfer.setCart(cart);
					if (tilePos.getY() != pos.getY()) {
						transfer.setSide(-1);
					} else if (tilePos.getX() < pos.getX()) {
						transfer.setSide(0);
					} else if (tilePos.getX() > pos.getX()) {
						transfer.setSide(3);
					} else if (tilePos.getZ() < pos.getZ()) {
						transfer.setSide(1);
					} else if (tilePos.getZ() > pos.getZ()) {
						transfer.setSide(2);
					}
					if (toCart) {
						transfer.setFromCartEnabled(false);
					} else {
						transfer.setToCartEnabled(false);
					}
					final TileEntityManager manager = (TileEntityManager) managerentity;
					while (manager.exchangeItems(transfer)) {}
				}
			}
		}
	}

	private void deploySpares() {
		for (final TileEntityUpgrade tile : getUpgradeTiles()) {
			if (tile.getUpgrade() != null) {
				for (final BaseEffect effect : tile.getUpgrade().getEffects()) {
					if (effect instanceof Disassemble) {
						for (
							@Nonnull
								ItemStack item : spareModules) {
							TransferHandler.TransferItem(item, tile, new ContainerUpgrade(null, tile), 1);
							if (item.getCount() > 0) {
								puke(item);
							}
						}
					}
				}
			}
		}
	}

	public void puke(
		@Nonnull
			ItemStack item) {
		final EntityItem entityitem = new EntityItem(world, pos.getX(), pos.getY() + 0.25, pos.getZ(), item);
		entityitem.motionX = (0.5f - world.rand.nextFloat()) / 10.0f;
		entityitem.motionY = 0.15000000596046448;
		entityitem.motionZ = (0.5f - world.rand.nextFloat()) / 10.0f;
		world.spawnEntity(entityitem);
	}

	@Override
	public void updateEntity() {
		if (!loaded) {
			((BlockCartAssembler) ModBlocks.CART_ASSEMBLER.getBlock()).updateMultiBlock(world, pos);
			loaded = true;
		}
		if (!isAssembling && outputSlot != null && !outputSlot.getStack().isEmpty()) {
			@Nonnull
			ItemStack itemInSlot = outputSlot.getStack();
			if (itemInSlot.getItem() == ModItems.carts) {
				final NBTTagCompound info = itemInSlot.getTagCompound();
				if (info != null && info.hasKey("maxTime")) {
					@Nonnull
					ItemStack newItem = new ItemStack(ModItems.carts);
					final NBTTagCompound save = new NBTTagCompound();
					save.setByteArray("Modules", info.getByteArray("Modules"));
					newItem.setTagCompound(save);
					final int modulecount = info.getByteArray("Modules").length;
					maxAssemblingTime = info.getInteger("maxTime");
					setAssemblingTime(info.getInteger("currentTime"));
					spareModules.clear();
					if (info.hasKey("Spares")) {
						final byte[] moduleIDs = info.getByteArray("Spares");
						for (int i = 0; i < moduleIDs.length; ++i) {
							final byte id = moduleIDs[i];
							@Nonnull
							ItemStack module = new ItemStack(ModItems.modules, 1, id);
							ModItems.modules.addExtraDataToModule(module, info, i + modulecount);
							spareModules.add(module);
						}
					}
					if (itemInSlot.hasDisplayName()) {
						newItem.setStackDisplayName(itemInSlot.getDisplayName());
					}
					isAssembling = true;
					outputItem = newItem;
					outputSlot.putStack(ItemStack.EMPTY);
				}
			}
		}
		if (getFuelLevel() > getMaxFuelLevel()) {
			setFuelLevel(getMaxFuelLevel());
		}
		if (isAssembling && outputSlot != null && getFuelLevel() >= getFuelCost()) {
			currentAssemblingTime += getEfficiency();
			fuelLevel -= getFuelCost();
			if (getFuelLevel() <= 0) {
				setFuelLevel(0);
			}
			if (getAssemblingTime() >= maxAssemblingTime) {
				isAssembling = false;
				setAssemblingTime(0);
				if (!world.isRemote) {
					deployCart();
					outputItem = ItemStack.EMPTY;
					deploySpares();
					spareModules.clear();
				}
			}
		}
		if (!world.isRemote && fuelSlot != null && !fuelSlot.getStack().isEmpty()) {
			final int fuel = fuelSlot.getFuelLevel(fuelSlot.getStack());
			if (fuel > 0 && getFuelLevel() + fuel <= getMaxFuelLevel()) {
				setFuelLevel(getFuelLevel() + fuel);
				if (fuelSlot.getStack().getItem().hasContainerItem(fuelSlot.getStack())) {
					fuelSlot.putStack(new ItemStack(fuelSlot.getStack().getItem().getContainerItem()));
				} else {
					@Nonnull
					ItemStack stack = fuelSlot.getStack();
					stack.shrink(1);
				}
				if (fuelSlot.getStack().getCount() <= 0) {
					fuelSlot.putStack(ItemStack.EMPTY);
				}
			}
		}
		updateSlots();
		handlePlaceholder();
	}

	public void updateSlots() {
		if (hullSlot != null) {
			if (!lastHull.isEmpty() && hullSlot.getStack().isEmpty()) {
				invalidateAll();
			} else if (lastHull.isEmpty() && !hullSlot.getStack().isEmpty()) {
				validateAll();
			} else if (lastHull != hullSlot.getStack()) {
				invalidateAll();
				validateAll();
			}
			lastHull = hullSlot.getStack();
		}
		for (final SlotAssembler slot : slots) {
			slot.update();
		}
	}

	public void resetPlaceholder() {
		placeholder = null;
	}

	public EntityMinecartModular getPlaceholder() {
		return placeholder;
	}

	public float getYaw() {
		return yaw;
	}

	public float getRoll() {
		return roll;
	}

	public void setYaw(final float val) {
		yaw = val;
	}

	public void setRoll(final float val) {
		roll = val;
	}

	public void setSpinning(final boolean val) {
		shouldSpin = val;
	}

	public int nonModularSlots() {
		return 2;
	}

	private void handlePlaceholder() {
		if (world.isRemote) {
			if (placeholder == null) {
				return;
			}
			if (!Constants.freezeCartSimulation) {
				final int minRoll = -5;
				final int maxRoll = 25;
				if (shouldSpin) {
					yaw += 2.0f;
					roll %= 360.0f;
					if (!rolldown) {
						if (roll < minRoll - 3) {
							roll += 5.0f;
						} else {
							roll += 0.2f;
						}
						if (roll > maxRoll) {
							rolldown = true;
						}
					} else {
						if (roll > maxRoll + 3) {
							roll -= 5.0f;
						} else {
							roll -= 0.2f;
						}
						if (roll < minRoll) {
							rolldown = false;
						}
					}
				}
			}
			placeholder.onCartUpdate();
			if (placeholder == null) {
				return;
			}
			placeholder.updateFuel();
		}
	}

	public void createPlaceholder() {
		if (placeholder == null) {
			placeholder = new EntityMinecartModular(world, this, getModularInfoBytes());
			updateRenderMenu();
			isErrorListOutdated = true;
		}
	}

	public void updatePlaceholder() {
		if (placeholder != null) {
			placeholder.updateSimulationModules(getModularInfoBytes());
			updateRenderMenu();
			isErrorListOutdated = true;
		}
	}

	private void updateRenderMenu() {
		final ArrayList<DropDownMenuItem> list = info.getList();
		dropDownItems.clear();
		for (final DropDownMenuItem item : list) {
			if (item.getModuleClass() == null) {
				dropDownItems.add(item);
			} else {
				for (int i = 0; i < getSizeInventory() - nonModularSlots(); ++i) {
					if (!getStackInSlot(i).isEmpty() && ModuleData.isItemOfModularType(getStackInSlot(i), item.getModuleClass()) && (item.getExcludedClass() == null || !ModuleData.isItemOfModularType(getStackInSlot(i), item.getExcludedClass()))) {
						dropDownItems.add(item);
						break;
					}
				}
			}
		}
	}

	private byte[] getModularInfoBytes() {
		final ArrayList<Byte> datalist = new ArrayList<>();
		for (int i = 0; i < getSizeInventory() - nonModularSlots(); ++i) {
			if (!getStackInSlot(i).isEmpty()) {
				final ModuleData data = ModItems.modules.getModuleData(getStackInSlot(i));
				if (data != null) {
					datalist.add((byte) getStackInSlot(i).getItemDamage());
				}
			}
		}
		final byte[] bytes = new byte[datalist.size()];
		for (int j = 0; j < datalist.size(); ++j) {
			bytes[j] = datalist.get(j);
		}
		return bytes;
	}

	public boolean getIsDisassembling() {
		for (int i = 0; i < getSizeInventory() - nonModularSlots(); ++i) {
			if (!getStackInSlot(i).isEmpty() && getStackInSlot(i).getCount() <= 0) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean isUsableByPlayer(final EntityPlayer entityplayer) {
		return world.getTileEntity(pos) == this && entityplayer.getDistanceSqToCenter(pos) <= 64.0;
	}

	@Override
	@Nonnull
	public ItemStack removeStackFromSlot(int index) {
		@Nonnull
		ItemStack item = getStackInSlot(index);
		if (item.isEmpty()) {
			return ItemStack.EMPTY;
		}
		setInventorySlotContents(index, ItemStack.EMPTY);
		if (item.getCount() == 0) {
			return ItemStack.EMPTY;
		}
		return item;
	}

	@Override
	public int getSizeInventory() {
		return inventoryStacks.size();
	}

	@Override
	public boolean isEmpty() {
		for (ItemStack stack : inventoryStacks) {
			if (!stack.isEmpty()) {
				return false;
			}
		}
		return true;
	}

	@Override
	@Nonnull
	public ItemStack getStackInSlot(final int i) {
		return inventoryStacks.get(i);
	}

	@Override
	@Nonnull
	public ItemStack decrStackSize(final int i, final int j) {
		if (inventoryStacks.get(i) == ItemStack.EMPTY) {
			return ItemStack.EMPTY;
		}
		if (inventoryStacks.get(i).getCount() <= j) {
			@Nonnull
			ItemStack itemstack = inventoryStacks.get(i);
			inventoryStacks.set(i, ItemStack.EMPTY);
			markDirty();
			return itemstack;
		}
		@Nonnull
		ItemStack itemstack2 = inventoryStacks.get(i).splitStack(j);
		if (inventoryStacks.get(i).getCount() == 0) {
			inventoryStacks.set(i, ItemStack.EMPTY);
		}
		markDirty();
		return itemstack2;
	}

	@Override
	public void setInventorySlotContents(final int i,
	                                     @Nonnull
		                                     ItemStack itemstack) {
		inventoryStacks.set(i, itemstack);
		if (!itemstack.isEmpty() && itemstack.getCount() > getInventoryStackLimit()) {
			itemstack.setCount(getInventoryStackLimit());
		}
		markDirty();
	}

	@Override
	public String getName() {
		return "container.cartassembler";
	}

	@Override
	public ITextComponent getDisplayName() {
		return new TextComponentString(getName());
	}

	@Override
	public boolean hasCustomName() {
		return false;
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public void closeInventory(EntityPlayer player) {
	}

	@Override
	public void openInventory(EntityPlayer player) {
	}

	@Override
	public void readFromNBT(final NBTTagCompound tagCompound) {
		super.readFromNBT(tagCompound);
		final NBTTagList items = tagCompound.getTagList("Items", NBTHelper.COMPOUND.getId());
		for (int i = 0; i < items.tagCount(); ++i) {
			final NBTTagCompound item = items.getCompoundTagAt(i);
			final int slot = item.getByte("Slot") & 0xFF;
			@Nonnull
			ItemStack iStack = new ItemStack(item);
			if (slot >= 0 && slot < getSizeInventory()) {
				setInventorySlotContents(slot, iStack);
			}
		}
		final NBTTagList spares = tagCompound.getTagList("Spares", NBTHelper.COMPOUND.getId());
		spareModules.clear();
		for (int j = 0; j < spares.tagCount(); ++j) {
			final NBTTagCompound item2 = spares.getCompoundTagAt(j);
			@Nonnull
			ItemStack iStack = new ItemStack(item2);
			spareModules.add(iStack);
		}
		final NBTTagCompound outputTag = (NBTTagCompound) tagCompound.getTag("Output");
		if (outputTag != null) {
			outputItem = new ItemStack(outputTag);
		}
		if (tagCompound.hasKey("Fuel")) {
			setFuelLevel(tagCompound.getShort("Fuel"));
		} else {
			setFuelLevel(tagCompound.getInteger("IntFuel"));
		}
		maxAssemblingTime = tagCompound.getInteger("maxTime");
		setAssemblingTime(tagCompound.getInteger("currentTime"));
		isAssembling = tagCompound.getBoolean("isAssembling");
	}

	@Override
	public NBTTagCompound writeToNBT(final NBTTagCompound tagCompound) {
		super.writeToNBT(tagCompound);
		final NBTTagList items = new NBTTagList();
		for (int i = 0; i < getSizeInventory(); ++i) {
			@Nonnull
			ItemStack iStack = getStackInSlot(i);
			if (!iStack.isEmpty()) {
				final NBTTagCompound item = new NBTTagCompound();
				item.setByte("Slot", (byte) i);
				iStack.writeToNBT(item);
				items.appendTag(item);
			}
		}
		tagCompound.setTag("Items", items);
		final NBTTagList spares = new NBTTagList();
		for (int j = 0; j < spareModules.size(); ++j) {
			@Nonnull
			ItemStack iStack2 = spareModules.get(j);
			if (!iStack2.isEmpty()) {
				final NBTTagCompound item2 = new NBTTagCompound();
				iStack2.writeToNBT(item2);
				spares.appendTag(item2);
			}
		}
		tagCompound.setTag("Spares", spares);
		if (!outputItem.isEmpty()) {
			final NBTTagCompound outputTag = new NBTTagCompound();
			outputItem.writeToNBT(outputTag);
			tagCompound.setTag("Output", outputTag);
		}
		tagCompound.setInteger("IntFuel", getFuelLevel());
		tagCompound.setInteger("maxTime", maxAssemblingTime);
		tagCompound.setInteger("currentTime", getAssemblingTime());
		tagCompound.setBoolean("isAssembling", isAssembling);
		return tagCompound;
	}

	@Nonnull
	public ItemStack getOutputOnInterupt() {
		if (outputItem.isEmpty()) {
			return ItemStack.EMPTY;
		}
		if (!outputItem.hasTagCompound()) {
			return ItemStack.EMPTY;
		}
		final NBTTagCompound info = outputItem.getTagCompound();
		if (info == null) {
			return ItemStack.EMPTY;
		}
		info.setInteger("currentTime", getAssemblingTime());
		info.setInteger("maxTime", maxAssemblingTime);
		final int modulecount = info.getByteArray("Modules").length;
		final NBTTagCompound spares = new NBTTagCompound();
		final byte[] moduleIDs = new byte[spareModules.size()];
		for (int i = 0; i < spareModules.size(); ++i) {
			@Nonnull
			ItemStack item = spareModules.get(i);
			final ModuleData data = ModItems.modules.getModuleData(item);
			if (data != null) {
				moduleIDs[i] = data.getID();
				ModItems.modules.addExtraDataToCart(info, item, i + modulecount);
			}
		}
		info.setByteArray("Spares", moduleIDs);
		return outputItem;
	}

	@Override
	public boolean isItemValidForSlot(final int slotId,
	                                  @Nonnull
		                                  ItemStack item) {
		return slotId >= 0 && slotId < slots.size() && slots.get(slotId).isItemValid(item);
	}

	@Override
	public int[] getSlotsForFace(EnumFacing side) {
		return (side == EnumFacing.DOWN || side == EnumFacing.UP) ? topbotSlots : sideSlots;
	}

	@Override
	public boolean canInsertItem(final int slot,
	                             @Nonnull
		                             ItemStack item, EnumFacing side) {
		return (side == EnumFacing.DOWN || side == EnumFacing.UP) && isItemValidForSlot(slot, item);
	}

	@Override
	public boolean canExtractItem(final int slot,
	                              @Nonnull
		                              ItemStack item, EnumFacing side) {
		return true;
	}

	public void increaseFuel(final int val) {
		fuelLevel += val;
		if (fuelLevel > getMaxFuelLevel()) {
			fuelLevel = getMaxFuelLevel();
		}
	}

	@Override
	public int getField(int id) {
		return 0;
	}

	@Override
	public void setField(int id, int value) {
	}

	@Override
	public int getFieldCount() {
		return 0;
	}

	@Override
	public void clear() {
	}
}
