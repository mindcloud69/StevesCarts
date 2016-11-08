package vswe.stevescarts.blocks.tileentities;

import java.util.ArrayList;

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
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vswe.stevescarts.Constants;
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
import vswe.stevescarts.helpers.DropDownMenuItem;
import vswe.stevescarts.helpers.Localization;
import vswe.stevescarts.helpers.NBTHelper;
import vswe.stevescarts.helpers.SimulationInfo;
import vswe.stevescarts.helpers.TitleBox;
import vswe.stevescarts.helpers.storages.TransferHandler;
import vswe.stevescarts.helpers.storages.TransferManager;
import vswe.stevescarts.items.ItemCarts;
import vswe.stevescarts.items.ModItems;
import vswe.stevescarts.modules.data.ModuleData;
import vswe.stevescarts.modules.data.ModuleDataHull;
import vswe.stevescarts.upgrades.AssemblerUpgrade;
import vswe.stevescarts.upgrades.BaseEffect;
import vswe.stevescarts.upgrades.CombustionFuel;
import vswe.stevescarts.upgrades.Deployer;
import vswe.stevescarts.upgrades.Disassemble;
import vswe.stevescarts.upgrades.FuelCapacity;
import vswe.stevescarts.upgrades.FuelCost;
import vswe.stevescarts.upgrades.Manager;
import vswe.stevescarts.upgrades.TimeFlat;
import vswe.stevescarts.upgrades.TimeFlatCart;
import vswe.stevescarts.upgrades.TimeFlatRemoved;
import vswe.stevescarts.upgrades.WorkEfficiency;

public class TileEntityCartAssembler extends TileEntityBase implements IInventory, ISidedInventory {
	private int maxAssemblingTime;
	private float currentAssemblingTime;
	protected ItemStack outputItem;
	protected ArrayList<ItemStack> spareModules;
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
	private ItemStack lastHull;
	private float fuelLevel;
	private ArrayList<TileEntityUpgrade> upgrades;
	public boolean isDead;
	private boolean loaded;
	ItemStack[] inventoryStacks;

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
		this.currentAssemblingTime = -1.0f;
		this.shouldSpin = true;
		this.yaw = 0.0f;
		this.roll = 0.0f;
		this.rolldown = false;
		this.upgrades = new ArrayList<TileEntityUpgrade>();
		this.spareModules = new ArrayList<ItemStack>();
		this.dropDownItems = new ArrayList<DropDownMenuItem>();
		this.slots = new ArrayList<SlotAssembler>();
		this.engineSlots = new ArrayList<SlotAssembler>();
		this.addonSlots = new ArrayList<SlotAssembler>();
		this.chestSlots = new ArrayList<SlotAssembler>();
		this.funcSlots = new ArrayList<SlotAssembler>();
		this.titleBoxes = new ArrayList<TitleBox>();
		int slotID = 0;
		this.hullSlot = new SlotHull(this, slotID++, 18, 25);
		this.slots.add(this.hullSlot);
		final TitleBox engineBox = new TitleBox(0, 65, 16225309);
		final TitleBox toolBox = new TitleBox(1, 100, 6696337);
		final TitleBox attachBox = new TitleBox(2, 135, 23423);
		final TitleBox storageBox = new TitleBox(3, 170, 10357518);
		final TitleBox addonBox = new TitleBox(4, 205, 22566);
		final TitleBox infoBox = new TitleBox(5, 375, 30, 13417984);
		this.titleBoxes.add(engineBox);
		this.titleBoxes.add(toolBox);
		this.titleBoxes.add(attachBox);
		this.titleBoxes.add(storageBox);
		this.titleBoxes.add(addonBox);
		this.titleBoxes.add(infoBox);
		for (int i = 0; i < 5; ++i) {
			final SlotAssembler slot = new SlotAssembler(this, slotID++, engineBox.getX() + 2 + 18 * i, engineBox.getY(), 1, false, i);
			slot.invalidate();
			this.slots.add(slot);
			this.engineSlots.add(slot);
		}
		this.toolSlot = new SlotAssembler(this, slotID++, toolBox.getX() + 2, toolBox.getY(), 2, false, 0);
		this.slots.add(this.toolSlot);
		this.toolSlot.invalidate();
		for (int i = 0; i < 6; ++i) {
			final SlotAssembler slot = new SlotAssembler(this, slotID++, attachBox.getX() + 2 + 18 * i, attachBox.getY(), -1, false, i);
			slot.invalidate();
			this.slots.add(slot);
			this.funcSlots.add(slot);
		}
		for (int i = 0; i < 4; ++i) {
			final SlotAssembler slot = new SlotAssembler(this, slotID++, storageBox.getX() + 2 + 18 * i, storageBox.getY(), 3, false, i);
			slot.invalidate();
			this.slots.add(slot);
			this.chestSlots.add(slot);
		}
		for (int i = 0; i < 12; ++i) {
			final SlotAssembler slot = new SlotAssembler(this, slotID++, addonBox.getX() + 2 + 18 * (i % 6), addonBox.getY() + 18 * (i / 6), 4, false, i);
			slot.invalidate();
			this.slots.add(slot);
			this.addonSlots.add(slot);
		}
		this.fuelSlot = new SlotAssemblerFuel(this, slotID++, 395, 220);
		this.slots.add(this.fuelSlot);
		this.outputSlot = new SlotOutput(this, slotID++, 450, 220);
		this.slots.add(this.outputSlot);
		this.info = new SimulationInfo();
		this.inventoryStacks = new ItemStack[this.slots.size()];
		this.topbotSlots = new int[] { this.getSizeInventory() - this.nonModularSlots() };
		this.sideSlots = new int[] { this.getSizeInventory() - this.nonModularSlots() + 1 };
	}

	public void clearUpgrades() {
		this.upgrades.clear();
	}

	public void addUpgrade(final TileEntityUpgrade upgrade) {
		this.upgrades.add(upgrade);
	}

	public void removeUpgrade(final TileEntityUpgrade upgrade) {
		this.upgrades.remove(upgrade);
	}

	public ArrayList<TileEntityUpgrade> getUpgradeTiles() {
		return this.upgrades;
	}

	public ArrayList<AssemblerUpgrade> getUpgrades() {
		final ArrayList<AssemblerUpgrade> lst = new ArrayList<AssemblerUpgrade>();
		for (final TileEntityUpgrade tile : this.upgrades) {
			lst.add(tile.getUpgrade());
		}
		return lst;
	}

	public ArrayList<BaseEffect> getEffects() {
		final ArrayList<BaseEffect> lst = new ArrayList<BaseEffect>();
		for (final TileEntityUpgrade tile : this.upgrades) {
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
		return this.info;
	}

	public ArrayList<DropDownMenuItem> getDropDown() {
		return this.dropDownItems;
	}

	public ArrayList<TitleBox> getTitleBoxes() {
		return this.titleBoxes;
	}

	public static int getRemovedSize() {
		return -1;
	}

	public static int getKeepSize() {
		return 0;
	}

	public ArrayList<SlotAssembler> getSlots() {
		return this.slots;
	}

	public ArrayList<SlotAssembler> getEngines() {
		return this.engineSlots;
	}

	public ArrayList<SlotAssembler> getChests() {
		return this.chestSlots;
	}

	public ArrayList<SlotAssembler> getAddons() {
		return this.addonSlots;
	}

	public ArrayList<SlotAssembler> getFuncs() {
		return this.funcSlots;
	}

	public SlotAssembler getToolSlot() {
		return this.toolSlot;
	}

	public int getMaxAssemblingTime() {
		return this.maxAssemblingTime;
	}

	public int getAssemblingTime() {
		return (int) this.currentAssemblingTime;
	}

	private void setAssemblingTime(final int val) {
		this.currentAssemblingTime = val;
	}

	public boolean getIsAssembling() {
		return this.isAssembling;
	}

	public void doAssemble() {
		if (!this.hasErrors()) {
			this.maxAssemblingTime = this.generateAssemblingTime();
			this.createCartFromModules();
			this.isAssembling = true;
			for (final TileEntityUpgrade tile : this.getUpgradeTiles()) {
				if (tile.getUpgrade() != null) {
					for (final BaseEffect effect : tile.getUpgrade().getEffects()) {
						if (effect instanceof Disassemble) {
							final ItemStack oldcart = tile.getStackInSlot(0);
							if (oldcart != null && oldcart.getItem() instanceof ItemCarts && oldcart.hasDisplayName()) {
								this.outputItem.setStackDisplayName(oldcart.getDisplayName());
							}
							tile.setInventorySlotContents(0, null);
						}
					}
				}
			}
		}
	}

	@Override
	public void receivePacket(final int id, final byte[] data, final EntityPlayer player) {
		if (id == 0) {
			this.doAssemble();
		} else if (id == 1) {
			final int slotId = data[0];
			if (slotId >= 1 && slotId < this.getSlots().size()) {
				final SlotAssembler slot = this.getSlots().get(slotId);
				if (slot.getStack() != null) {
					if (slot.getStack().stackSize == getKeepSize()) {
						slot.getStack().stackSize = getRemovedSize();
					} else {
						slot.getStack().stackSize = getKeepSize();
					}
				}
			}
		}
	}

	public void onUpgradeUpdate() {
	}

	public int generateAssemblingTime() {
		return this.generateAssemblingTime(this.getModules(true, new int[] { getKeepSize(), getRemovedSize() }), this.getModules(true, new int[] { getKeepSize(), 1 }));
	}

	private int generateAssemblingTime(final ArrayList<ModuleData> modules, final ArrayList<ModuleData> removed) {
		int timeRequired = 100;
		for (final ModuleData module : modules) {
			timeRequired += this.getAssemblingTime(module, false);
		}
		for (final ModuleData module : removed) {
			timeRequired += this.getAssemblingTime(module, true);
		}
		for (final BaseEffect effect : this.getEffects()) {
			if (effect instanceof TimeFlatCart) {
				timeRequired += ((TimeFlatCart) effect).getTicks();
			}
		}
		return Math.max(0, timeRequired);
	}

	private int getAssemblingTime(final ModuleData module, final boolean isRemoved) {
		int time = (int) (5.0 * Math.pow(module.getCost(), 2.2));
		time += this.getTimeDecreased(isRemoved);
		return Math.max(0, time);
	}

	public ItemStack getCartFromModules(final boolean isSimulated) {
		final ArrayList<ItemStack> items = new ArrayList<ItemStack>();
		for (int i = 0; i < this.getSizeInventory() - this.nonModularSlots(); ++i) {
			final ItemStack item = this.getStackInSlot(i);
			if (item != null) {
				if (item.stackSize != getRemovedSize()) {
					items.add(item);
				} else if (!isSimulated) {
					final ItemStack spare = item.copy();
					spare.stackSize = 1;
					this.spareModules.add(spare);
				}
			}
		}
		return ModuleData.createModularCartFromItems(items);
	}

	private void createCartFromModules() {
		this.spareModules.clear();
		this.outputItem = this.getCartFromModules(false);
		if (this.outputItem != null) {
			for (int i = 0; i < this.getSizeInventory() - this.nonModularSlots(); ++i) {
				this.setInventorySlotContents(i, null);
			}
		} else {
			this.spareModules.clear();
		}
	}

	public ArrayList<ModuleData> getNonHullModules() {
		return this.getModules(false);
	}

	public ArrayList<ModuleData> getModules(final boolean includeHull) {
		return this.getModules(includeHull, new int[] { getRemovedSize() });
	}

	public ArrayList<ModuleData> getModules(final boolean includeHull, final int[] invalid) {
		final ArrayList<ModuleData> modules = new ArrayList<ModuleData>();
		for (int i = includeHull ? 0 : 1; i < this.getSizeInventory() - this.nonModularSlots(); ++i) {
			final ItemStack item = this.getStackInSlot(i);
			if (item != null) {
				boolean validSize = true;
				for (int j = 0; j < invalid.length; ++j) {
					if (invalid[j] == item.stackSize || (invalid[j] > 0 && item.stackSize > 0)) {
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
		if (this.getStackInSlot(0) != null) {
			final ModuleData hulldata = ModItems.modules.getModuleData(this.getStackInSlot(0));
			if (hulldata instanceof ModuleDataHull) {
				return (ModuleDataHull) hulldata;
			}
		}
		return null;
	}

	private boolean hasErrors() {
		return this.getErrors().size() > 0;
	}

	public ArrayList<String> getErrors() {
		final ArrayList<String> errors = new ArrayList<String>();
		if (this.hullSlot.getStack() == null) {
			errors.add(Localization.GUI.ASSEMBLER.HULL_ERROR.translate());
		} else {
			final ModuleData hulldata = ModItems.modules.getModuleData(this.getStackInSlot(0));
			if (hulldata == null || !(hulldata instanceof ModuleDataHull)) {
				errors.add(Localization.GUI.ASSEMBLER.INVALID_HULL_SHORT.translate());
			} else {
				if (this.isAssembling) {
					errors.add(Localization.GUI.ASSEMBLER.BUSY.translate());
				} else if (this.outputSlot != null && this.outputSlot.getStack() != null) {
					errors.add(Localization.GUI.ASSEMBLER.DEPARTURE_BAY.translate());
				}
				final ArrayList<ModuleData> modules = new ArrayList<ModuleData>();
				for (int i = 0; i < this.getSizeInventory() - this.nonModularSlots(); ++i) {
					if (this.getStackInSlot(i) != null) {
						final ModuleData data = ModItems.modules.getModuleData(this.getStackInSlot(i));
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
		final ArrayList<ModuleData> modules = new ArrayList<ModuleData>();
		for (int i = 0; i < this.getSizeInventory() - this.nonModularSlots(); ++i) {
			if (this.getStackInSlot(i) != null) {
				final ModuleData data = ModItems.modules.getModuleData(this.getStackInSlot(i));
				if (data != null) {
					modules.add(data);
				}
			}
		}
		return ModuleData.getTotalCost(modules);
	}

	@Override
	public void initGuiData(final Container con, final IContainerListener crafting) {
		this.updateGuiData(con, crafting, 0, this.getShortFromInt(true, this.maxAssemblingTime));
		this.updateGuiData(con, crafting, 1, this.getShortFromInt(false, this.maxAssemblingTime));
		this.updateGuiData(con, crafting, 2, this.getShortFromInt(true, this.getAssemblingTime()));
		this.updateGuiData(con, crafting, 3, this.getShortFromInt(false, this.getAssemblingTime()));
		this.updateGuiData(con, crafting, 4, (short) (this.isAssembling ? 1 : 0));
		this.updateGuiData(con, crafting, 5, this.getShortFromInt(true, this.getFuelLevel()));
		this.updateGuiData(con, crafting, 6, this.getShortFromInt(false, this.getFuelLevel()));
	}

	@Override
	public void checkGuiData(final Container container, final IContainerListener crafting) {
		final ContainerCartAssembler con = (ContainerCartAssembler) container;
		if (con.lastMaxAssemblingTime != this.maxAssemblingTime) {
			this.updateGuiData(con, crafting, 0, this.getShortFromInt(true, this.maxAssemblingTime));
			this.updateGuiData(con, crafting, 1, this.getShortFromInt(false, this.maxAssemblingTime));
			con.lastMaxAssemblingTime = this.maxAssemblingTime;
		}
		if (con.lastIsAssembling != this.isAssembling) {
			this.updateGuiData(con, crafting, 4, (short) (this.isAssembling ? 1 : 0));
			con.lastIsAssembling = this.isAssembling;
		}
		if (con.lastFuelLevel != this.getFuelLevel()) {
			this.updateGuiData(con, crafting, 5, this.getShortFromInt(true, this.getFuelLevel()));
			this.updateGuiData(con, crafting, 6, this.getShortFromInt(false, this.getFuelLevel()));
			con.lastFuelLevel = this.getFuelLevel();
		}
	}

	@Override
	public void receiveGuiData(final int id, final short data) {
		if (id == 0) {
			this.maxAssemblingTime = this.getIntFromShort(true, this.maxAssemblingTime, data);
		} else if (id == 1) {
			this.maxAssemblingTime = this.getIntFromShort(false, this.maxAssemblingTime, data);
		} else if (id == 2) {
			this.setAssemblingTime(this.getIntFromShort(true, this.getAssemblingTime(), data));
		} else if (id == 3) {
			this.setAssemblingTime(this.getIntFromShort(false, this.getAssemblingTime(), data));
		} else if (id == 4) {
			if (!(this.isAssembling = (data != 0))) {
				this.setAssemblingTime(0);
			}
		} else if (id == 5) {
			this.setFuelLevel(this.getIntFromShort(true, this.getFuelLevel(), data));
		} else if (id == 6) {
			this.setFuelLevel(this.getIntFromShort(false, this.getFuelLevel(), data));
		}
	}

	private void invalidateAll() {
		for (int i = 0; i < this.getEngines().size(); ++i) {
			this.getEngines().get(i).invalidate();
		}
		for (int i = 0; i < this.getAddons().size(); ++i) {
			this.getAddons().get(i).invalidate();
		}
		for (int i = 0; i < this.getChests().size(); ++i) {
			this.getChests().get(i).invalidate();
		}
		for (int i = 0; i < this.getFuncs().size(); ++i) {
			this.getFuncs().get(i).invalidate();
		}
		this.getToolSlot().invalidate();
	}

	private void validateAll() {
		if (this.hullSlot == null) {
			return;
		}
		final ArrayList<SlotAssembler> slots = this.getValidSlotFromHullItem(this.hullSlot.getStack());
		if (slots != null) {
			for (final SlotAssembler slot : slots) {
				slot.validate();
			}
		}
	}

	public ArrayList<SlotAssembler> getValidSlotFromHullItem(final ItemStack hullitem) {
		if (hullitem != null) {
			final ModuleData data = ModItems.modules.getModuleData(hullitem);
			if (data != null && data instanceof ModuleDataHull) {
				final ModuleDataHull hull = (ModuleDataHull) data;
				return this.getValidSlotFromHull(hull);
			}
		}
		return null;
	}

	private ArrayList<SlotAssembler> getValidSlotFromHull(final ModuleDataHull hull) {
		final ArrayList<SlotAssembler> slots = new ArrayList<SlotAssembler>();
		for (int i = 0; i < hull.getEngineMax(); ++i) {
			slots.add(this.getEngines().get(i));
		}
		for (int i = 0; i < hull.getAddonMax(); ++i) {
			slots.add(this.getAddons().get(i));
		}
		for (int i = 0; i < this.getChests().size(); ++i) {
			slots.add(this.getChests().get(i));
		}
		for (int i = 0; i < this.getFuncs().size(); ++i) {
			slots.add(this.getFuncs().get(i));
		}
		slots.add(this.getToolSlot());
		return slots;
	}

	public int getMaxFuelLevel() {
		int capacity = 4000;
		for (final BaseEffect effect : this.getEffects()) {
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
		for (final BaseEffect effect : this.getEffects()) {
			if (effect instanceof CombustionFuel) {
				return true;
			}
		}
		return false;
	}

	public int getFuelLevel() {
		return (int) this.fuelLevel;
	}

	public void setFuelLevel(final int val) {
		this.fuelLevel = val;
	}

	private int getTimeDecreased(final boolean isRemoved) {
		int timeDecr = 0;
		for (final BaseEffect effect : this.getEffects()) {
			if (effect instanceof TimeFlat && !(effect instanceof TimeFlatRemoved)) {
				timeDecr += ((TimeFlat) effect).getTicks();
			}
		}
		if (isRemoved) {
			for (final BaseEffect effect : this.getEffects()) {
				if (effect instanceof TimeFlatRemoved) {
					timeDecr += ((TimeFlat) effect).getTicks();
				}
			}
		}
		return timeDecr;
	}

	private float getFuelCost() {
		float cost = 1.0f;
		for (final BaseEffect effect : this.getEffects()) {
			if (effect instanceof FuelCost) {
				cost += ((FuelCost) effect).getCost();
			}
		}
		if (cost < 0.05f) {}
		return cost;
	}

	public float getEfficiency() {
		float efficiency = 1.0f;
		for (final BaseEffect effect : this.getEffects()) {
			if (effect instanceof WorkEfficiency) {
				efficiency += ((WorkEfficiency) effect).getEfficiency();
			}
		}
		return efficiency;
	}

	private void deployCart() {
		for (final TileEntityUpgrade tile : this.getUpgradeTiles()) {
			for (final BaseEffect effect : tile.getUpgrade().getEffects()) {
				if (effect instanceof Deployer) {
					BlockPos tilePos = tile.getPos();
					final int xPos = 2 * tilePos.getX() - pos.getX();
					int yPos = 2 * tilePos.getY() - pos.getY();
					final int zPos = 2 * tilePos.getZ() - pos.getZ();
					if (tilePos.getY() > pos.getY()) {
						++yPos;
					}
					if (!BlockRailBase.isRailBlock(worldObj, new BlockPos(xPos, yPos, zPos))) {
						continue;
					}
					try {
						final NBTTagCompound info = this.outputItem.getTagCompound();
						if (info != null) {
							final EntityMinecartModular cart = new EntityMinecartModular(this.worldObj, xPos + 0.5f, yPos + 0.5f, zPos + 0.5f, info, this.outputItem.getDisplayName());
							this.worldObj.spawnEntityInWorld(cart);
							cart.temppushX = tilePos.getX() - pos.getX();
							cart.temppushZ = tilePos.getZ() - pos.getZ();
							this.managerInteract(cart, true);
							return;
						}
						continue;
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
		this.outputSlot.putStack(this.outputItem);
	}

	public void managerInteract(final EntityMinecartModular cart, final boolean toCart) {
		for (final TileEntityUpgrade tile : this.getUpgradeTiles()) {
			for (final BaseEffect effect : tile.getUpgrade().getEffects()) {
				if (effect instanceof Manager) {
					BlockPos tilePos = tile.getPos();
					final int xPos = 2 * tilePos.getX() - pos.getX();
					int yPos = 2 * tilePos.getY() - pos.getY();
					final int zPos = 2 * tilePos.getZ() - pos.getZ();
					if (tilePos.getY() > pos.getY()) {
						++yPos;
					}
					final TileEntity managerentity = this.worldObj.getTileEntity(new BlockPos(xPos, yPos, zPos));
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
		for (final TileEntityUpgrade tile : this.getUpgradeTiles()) {
			if (tile.getUpgrade() != null) {
				for (final BaseEffect effect : tile.getUpgrade().getEffects()) {
					if (effect instanceof Disassemble) {
						for (final ItemStack item : this.spareModules) {
							TransferHandler.TransferItem(item, tile, new ContainerUpgrade(null, tile), 1);
							if (item.stackSize > 0) {
								this.puke(item);
							}
						}
					}
				}
			}
		}
	}

	public void puke(final ItemStack item) {
		final EntityItem entityitem = new EntityItem(this.worldObj, pos.getX(), pos.getY() + 0.25, pos.getZ(), item);
		entityitem.motionX = (0.5f - this.worldObj.rand.nextFloat()) / 10.0f;
		entityitem.motionY = 0.15000000596046448;
		entityitem.motionZ = (0.5f - this.worldObj.rand.nextFloat()) / 10.0f;
		this.worldObj.spawnEntityInWorld(entityitem);
	}

	public void updateEntity() {
		if (!this.loaded) {
			((BlockCartAssembler) ModBlocks.CART_ASSEMBLER.getBlock()).updateMultiBlock(worldObj, pos);
			this.loaded = true;
		}
		if (!this.isAssembling && this.outputSlot != null && this.outputSlot.getStack() != null) {
			final ItemStack itemInSlot = this.outputSlot.getStack();
			if (itemInSlot.getItem() == ModItems.carts) {
				final NBTTagCompound info = itemInSlot.getTagCompound();
				if (info != null && info.hasKey("maxTime")) {
					final ItemStack newItem = new ItemStack(ModItems.carts);
					final NBTTagCompound save = new NBTTagCompound();
					save.setByteArray("Modules", info.getByteArray("Modules"));
					newItem.setTagCompound(save);
					final int modulecount = info.getByteArray("Modules").length;
					this.maxAssemblingTime = info.getInteger("maxTime");
					this.setAssemblingTime(info.getInteger("currentTime"));
					this.spareModules.clear();
					if (info.hasKey("Spares")) {
						final byte[] moduleIDs = info.getByteArray("Spares");
						for (int i = 0; i < moduleIDs.length; ++i) {
							final byte id = moduleIDs[i];
							final ItemStack module = new ItemStack(ModItems.modules, 1, id);
							ModItems.modules.addExtraDataToModule(module, info, i + modulecount);
							this.spareModules.add(module);
						}
					}
					if (itemInSlot.hasDisplayName()) {
						newItem.setStackDisplayName(itemInSlot.getDisplayName());
					}
					this.isAssembling = true;
					this.outputItem = newItem;
					this.outputSlot.putStack(null);
				}
			}
		}
		if (this.getFuelLevel() > this.getMaxFuelLevel()) {
			this.setFuelLevel(this.getMaxFuelLevel());
		}
		if (this.isAssembling && this.outputSlot != null && this.getFuelLevel() >= this.getFuelCost()) {
			this.currentAssemblingTime += this.getEfficiency();
			this.fuelLevel -= this.getFuelCost();
			if (this.getFuelLevel() <= 0) {
				this.setFuelLevel(0);
			}
			if (this.getAssemblingTime() >= this.maxAssemblingTime) {
				this.isAssembling = false;
				this.setAssemblingTime(0);
				if (!this.worldObj.isRemote) {
					this.deployCart();
					this.outputItem = null;
					this.deploySpares();
					this.spareModules.clear();
				}
			}
		}
		if (!this.worldObj.isRemote && this.fuelSlot != null && this.fuelSlot.getStack() != null) {
			final int fuel = this.fuelSlot.getFuelLevel(this.fuelSlot.getStack());
			if (fuel > 0 && this.getFuelLevel() + fuel <= this.getMaxFuelLevel()) {
				this.setFuelLevel(this.getFuelLevel() + fuel);
				if (this.fuelSlot.getStack().getItem().hasContainerItem(this.fuelSlot.getStack())) {
					this.fuelSlot.putStack(new ItemStack(this.fuelSlot.getStack().getItem().getContainerItem()));
				} else {
					final ItemStack stack = this.fuelSlot.getStack();
					--stack.stackSize;
				}
				if (this.fuelSlot.getStack().stackSize <= 0) {
					this.fuelSlot.putStack(null);
				}
			}
		}
		this.updateSlots();
		this.handlePlaceholder();
	}

	public void updateSlots() {
		if (this.hullSlot != null) {
			if (this.lastHull != null && this.hullSlot.getStack() == null) {
				this.invalidateAll();
			} else if (this.lastHull == null && this.hullSlot.getStack() != null) {
				this.validateAll();
			} else if (this.lastHull != this.hullSlot.getStack()) {
				this.invalidateAll();
				this.validateAll();
			}
			this.lastHull = this.hullSlot.getStack();
		}
		for (final SlotAssembler slot : this.slots) {
			slot.update();
		}
	}

	public void resetPlaceholder() {
		this.placeholder = null;
	}

	public EntityMinecartModular getPlaceholder() {
		return this.placeholder;
	}

	public float getYaw() {
		return this.yaw;
	}

	public float getRoll() {
		return this.roll;
	}

	public void setYaw(final float val) {
		this.yaw = val;
	}

	public void setRoll(final float val) {
		this.roll = val;
	}

	public void setSpinning(final boolean val) {
		this.shouldSpin = val;
	}

	public int nonModularSlots() {
		return 2;
	}

	private void handlePlaceholder() {
		if (this.worldObj.isRemote) {
			if (this.placeholder == null) {
				return;
			}
			if (!Constants.freezeCartSimulation) {
				final int minRoll = -5;
				final int maxRoll = 25;
				if (this.shouldSpin) {
					this.yaw += 2.0f;
					this.roll %= 360.0f;
					if (!this.rolldown) {
						if (this.roll < minRoll - 3) {
							this.roll += 5.0f;
						} else {
							this.roll += 0.2f;
						}
						if (this.roll > maxRoll) {
							this.rolldown = true;
						}
					} else {
						if (this.roll > maxRoll + 3) {
							this.roll -= 5.0f;
						} else {
							this.roll -= 0.2f;
						}
						if (this.roll < minRoll) {
							this.rolldown = false;
						}
					}
				}
			}
			this.placeholder.onCartUpdate();
			if (this.placeholder == null) {
				return;
			}
			this.placeholder.updateFuel();
		}
	}

	public void createPlaceholder() {
		if (this.placeholder == null) {
			this.placeholder = new EntityMinecartModular(this.worldObj, this, this.getModularInfoBytes());
			this.updateRenderMenu();
			this.isErrorListOutdated = true;
		}
	}

	public void updatePlaceholder() {
		if (this.placeholder != null) {
			this.placeholder.updateSimulationModules(this.getModularInfoBytes());
			this.updateRenderMenu();
			this.isErrorListOutdated = true;
		}
	}

	private void updateRenderMenu() {
		final ArrayList<DropDownMenuItem> list = this.info.getList();
		this.dropDownItems.clear();
		for (final DropDownMenuItem item : list) {
			if (item.getModuleClass() == null) {
				this.dropDownItems.add(item);
			} else {
				for (int i = 0; i < this.getSizeInventory() - this.nonModularSlots(); ++i) {
					if (this.getStackInSlot(i) != null && ModuleData.isItemOfModularType(this.getStackInSlot(i), item.getModuleClass()) && (item.getExcludedClass() == null || !ModuleData.isItemOfModularType(this.getStackInSlot(i), item.getExcludedClass()))) {
						this.dropDownItems.add(item);
						break;
					}
				}
			}
		}
	}

	private byte[] getModularInfoBytes() {
		final ArrayList<Byte> datalist = new ArrayList<Byte>();
		for (int i = 0; i < this.getSizeInventory() - this.nonModularSlots(); ++i) {
			if (this.getStackInSlot(i) != null) {
				final ModuleData data = ModItems.modules.getModuleData(this.getStackInSlot(i));
				if (data != null) {
					datalist.add((byte) this.getStackInSlot(i).getItemDamage());
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
		for (int i = 0; i < this.getSizeInventory() - this.nonModularSlots(); ++i) {
			if (this.getStackInSlot(i) != null && this.getStackInSlot(i).stackSize <= 0) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean isUseableByPlayer(final EntityPlayer entityplayer) {
		return this.worldObj.getTileEntity(this.pos) == this && entityplayer.getDistanceSqToCenter(pos) <= 64.0;
	}

	@Override
	public ItemStack removeStackFromSlot(int index) {
		final ItemStack item = this.getStackInSlot(index);
		if (item == null) {
			return null;
		}
		this.setInventorySlotContents(index, null);
		if (item.stackSize == 0) {
			return null;
		}
		return item;
	}

	@Override
	public int getSizeInventory() {
		return this.inventoryStacks.length;
	}

	@Override
	public ItemStack getStackInSlot(final int i) {
		return this.inventoryStacks[i];
	}

	@Override
	public ItemStack decrStackSize(final int i, final int j) {
		if (this.inventoryStacks[i] == null) {
			return null;
		}
		if (this.inventoryStacks[i].stackSize <= j) {
			final ItemStack itemstack = this.inventoryStacks[i];
			this.inventoryStacks[i] = null;
			this.markDirty();
			return itemstack;
		}
		final ItemStack itemstack2 = this.inventoryStacks[i].splitStack(j);
		if (this.inventoryStacks[i].stackSize == 0) {
			this.inventoryStacks[i] = null;
		}
		this.markDirty();
		return itemstack2;
	}

	@Override
	public void setInventorySlotContents(final int i, final ItemStack itemstack) {
		this.inventoryStacks[i] = itemstack;
		if (itemstack != null && itemstack.stackSize > this.getInventoryStackLimit()) {
			itemstack.stackSize = this.getInventoryStackLimit();
		}
		this.markDirty();
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
			final ItemStack iStack = ItemStack.loadItemStackFromNBT(item);
			if (slot >= 0 && slot < this.getSizeInventory()) {
				this.setInventorySlotContents(slot, iStack);
			}
		}
		final NBTTagList spares = tagCompound.getTagList("Spares", NBTHelper.COMPOUND.getId());
		this.spareModules.clear();
		for (int j = 0; j < spares.tagCount(); ++j) {
			final NBTTagCompound item2 = spares.getCompoundTagAt(j);
			final ItemStack iStack = ItemStack.loadItemStackFromNBT(item2);
			this.spareModules.add(iStack);
		}
		final NBTTagCompound outputTag = (NBTTagCompound) tagCompound.getTag("Output");
		if (outputTag != null) {
			this.outputItem = ItemStack.loadItemStackFromNBT(outputTag);
		}
		if (tagCompound.hasKey("Fuel")) {
			this.setFuelLevel(tagCompound.getShort("Fuel"));
		} else {
			this.setFuelLevel(tagCompound.getInteger("IntFuel"));
		}
		this.maxAssemblingTime = tagCompound.getInteger("maxTime");
		this.setAssemblingTime(tagCompound.getInteger("currentTime"));
		this.isAssembling = tagCompound.getBoolean("isAssembling");
	}

	@Override
	public NBTTagCompound writeToNBT(final NBTTagCompound tagCompound) {
		super.writeToNBT(tagCompound);
		final NBTTagList items = new NBTTagList();
		for (int i = 0; i < this.getSizeInventory(); ++i) {
			final ItemStack iStack = this.getStackInSlot(i);
			if (iStack != null) {
				final NBTTagCompound item = new NBTTagCompound();
				item.setByte("Slot", (byte) i);
				iStack.writeToNBT(item);
				items.appendTag(item);
			}
		}
		tagCompound.setTag("Items", items);
		final NBTTagList spares = new NBTTagList();
		for (int j = 0; j < this.spareModules.size(); ++j) {
			final ItemStack iStack2 = this.spareModules.get(j);
			if (iStack2 != null) {
				final NBTTagCompound item2 = new NBTTagCompound();
				iStack2.writeToNBT(item2);
				spares.appendTag(item2);
			}
		}
		tagCompound.setTag("Spares", spares);
		if (this.outputItem != null) {
			final NBTTagCompound outputTag = new NBTTagCompound();
			this.outputItem.writeToNBT(outputTag);
			tagCompound.setTag("Output", outputTag);
		}
		tagCompound.setInteger("IntFuel", this.getFuelLevel());
		tagCompound.setInteger("maxTime", this.maxAssemblingTime);
		tagCompound.setInteger("currentTime", this.getAssemblingTime());
		tagCompound.setBoolean("isAssembling", this.isAssembling);
		return tagCompound;
	}

	public ItemStack getOutputOnInterupt() {
		if (this.outputItem == null) {
			return null;
		}
		if (!this.outputItem.hasTagCompound()) {
			return null;
		}
		final NBTTagCompound info = this.outputItem.getTagCompound();
		if (info == null) {
			return null;
		}
		info.setInteger("currentTime", this.getAssemblingTime());
		info.setInteger("maxTime", this.maxAssemblingTime);
		final int modulecount = info.getByteArray("Modules").length;
		final NBTTagCompound spares = new NBTTagCompound();
		final byte[] moduleIDs = new byte[this.spareModules.size()];
		for (int i = 0; i < this.spareModules.size(); ++i) {
			final ItemStack item = this.spareModules.get(i);
			final ModuleData data = ModItems.modules.getModuleData(item);
			if (data != null) {
				moduleIDs[i] = data.getID();
				ModItems.modules.addExtraDataToCart(info, item, i + modulecount);
			}
		}
		info.setByteArray("Spares", moduleIDs);
		return this.outputItem;
	}

	@Override
	public boolean isItemValidForSlot(final int slotId, final ItemStack item) {
		return slotId >= 0 && slotId < this.slots.size() && this.slots.get(slotId).isItemValid(item);
	}

	@Override
	public int[] getSlotsForFace(EnumFacing side) {
		return (side == EnumFacing.DOWN || side == EnumFacing.UP) ? this.topbotSlots : this.sideSlots;
	}

	@Override
	public boolean canInsertItem(final int slot, final ItemStack item, EnumFacing side) {
		return (side == EnumFacing.DOWN || side == EnumFacing.UP) && this.isItemValidForSlot(slot, item);
	}

	@Override
	public boolean canExtractItem(final int slot, final ItemStack item, EnumFacing side) {
		return true;
	}

	public void increaseFuel(final int val) {
		this.fuelLevel += val;
		if (this.fuelLevel > this.getMaxFuelLevel()) {
			this.fuelLevel = this.getMaxFuelLevel();
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
