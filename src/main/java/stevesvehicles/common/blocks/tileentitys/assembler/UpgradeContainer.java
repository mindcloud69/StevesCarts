package stevesvehicles.common.blocks.tileentitys.assembler;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.FluidTankProperties;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import stevesvehicles.client.gui.screen.GuiBase;
import stevesvehicles.client.gui.screen.GuiUpgrade;
import stevesvehicles.common.blocks.ModBlocks;
import stevesvehicles.common.blocks.tileentitys.TileEntityCartAssembler;
import stevesvehicles.common.container.ContainerBase;
import stevesvehicles.common.container.ContainerUpgrade;
import stevesvehicles.common.tanks.Tank;
import stevesvehicles.common.upgrades.EffectType;
import stevesvehicles.common.upgrades.Upgrade;
import stevesvehicles.common.upgrades.effects.BaseEffect;
import stevesvehicles.common.upgrades.effects.util.InterfaceEffect;
import stevesvehicles.common.upgrades.effects.util.InventoryEffect;
import stevesvehicles.common.upgrades.effects.util.TankEffect;
import stevesvehicles.common.upgrades.registries.UpgradeRegistry;
import stevesvehicles.common.utils.NBTHelper;

public class UpgradeContainer implements ISidedInventory, IFluidHandler, IFluidTank, ITickable {
	public static final ItemStack INVALID_STACK = ItemStack.EMPTY;
	protected NonNullList<ItemStack> inventoryStacks;
	public static final NonNullList<ItemStack> DEFAULT_STACKS = NonNullList.withSize(0, INVALID_STACK);
	private final TileEntityCartAssembler master;
	private final EnumFacing facing;
	private final Upgrade upgrade;
	private List<BaseEffect> effects;
	private InterfaceEffect interfaceEffect;
	private InventoryEffect inventoryEffect;
	private TankEffect tankEffect;
	// INVENTORY STUFF BELOW
	private int[] slotsForSide;

	public UpgradeContainer(EnumFacing facing, TileEntityCartAssembler master, Upgrade upgrade) {
		this.facing = facing;
		this.master = master;
		this.upgrade = upgrade;
		inventoryStacks = DEFAULT_STACKS;

		if (upgrade != null) {
			createEffects(upgrade);
			for (BaseEffect effect : effects) {
				if (effect instanceof InterfaceEffect) {
					interfaceEffect = (InterfaceEffect) effect;
				}
				if (effect instanceof InventoryEffect) {
					inventoryEffect = (InventoryEffect) effect;
				}
				if (effect instanceof TankEffect) {
					tankEffect = (TankEffect) effect;
				}
			}
			int inventorySize = inventoryEffect != null ? inventoryEffect.getInventorySize() : 0;
			slotsForSide = new int[inventorySize];
			init();
			if (inventorySize > 0) {
				inventoryStacks = NonNullList.withSize(inventorySize, ItemStack.EMPTY);
				for (int i = 0; i < slotsForSide.length; i++) {
					slotsForSide[i] = i;
				}
			}
		} else {
			effects = null;
		}
	}

	public UpgradeContainer(EnumFacing facing, TileEntityCartAssembler master, NBTTagCompound tagCompound) {
		this.facing = facing;
		this.master = master;

		upgrade = UpgradeRegistry.getUpgradeFromId(tagCompound.getByte("Upgrade"));
		if (upgrade != null) {
			createEffects(upgrade);
			for (BaseEffect effect : effects) {
				if (effect instanceof InterfaceEffect) {
					interfaceEffect = (InterfaceEffect) effect;
				}
				if (effect instanceof InventoryEffect) {
					inventoryEffect = (InventoryEffect) effect;
				}
				if (effect instanceof TankEffect) {
					tankEffect = (TankEffect) effect;
				}
			}
			int inventorySize = inventoryEffect != null ? inventoryEffect.getInventorySize() : 0;
			slotsForSide = new int[inventorySize];
			init();
			if (inventorySize > 0) {
				inventoryStacks = NonNullList.withSize(inventorySize, ItemStack.EMPTY);
				for (int i = 0; i < slotsForSide.length; i++) {
					slotsForSide[i] = i;
				}
			}
		} else {
			effects = null;
			inventoryStacks = DEFAULT_STACKS;
		}
		inventoryStacks.clear();
		ItemStackHelper.loadAllItems(tagCompound, inventoryStacks);
		if (effects != null) {
			load(tagCompound);
		}	
	}

	public TileEntityCartAssembler getMaster() {
		return master;
	}

	public EnumFacing getFacing() {
		return facing;
	}

	private boolean initialized;

	private void createEffects(Upgrade upgrade) {
		effects = new ArrayList<>();
		for (EffectType effectType : upgrade.getEffectTypes()) {
			try {
				Object[] params = new Object[effectType.getParams().length + 1];
				params[0] = this;
				for (int i = 0; i < effectType.getParams().length; i++) {
					params[i + 1] = effectType.getParams()[i];
				}
				Class[] paramClasses = new Class[params.length];
				for (int i = 0; i < params.length; i++) {
					paramClasses[i] = params[i].getClass();
				}
				Constructor<? extends BaseEffect> constructor = effectType.getClazz().getConstructor(paramClasses);
				Object obj = constructor.newInstance(params);
				effects.add((BaseEffect) obj);
			} catch (Exception ex) {
				System.err.println("Failed to create a new instance of " + effectType.getClazz().getName());
				ex.printStackTrace();
			}
		}
	}

	public Upgrade getUpgrade() {
		return upgrade;
	}

	public boolean hasInventory() {
		return !inventoryStacks.isEmpty();
	}

	public NBTTagCompound writeToNBT(NBTTagCompound tagCompound) {
		ItemStackHelper.saveAllItems(tagCompound, inventoryStacks);
		tagCompound.setByte("Upgrade", (byte) UpgradeRegistry.getIdFromUpgrade(upgrade));
		if (effects != null) {
			save(tagCompound);
		}
		return tagCompound;
	}

	public void initGuiData(Container con, IContainerListener crafting) {
		if (effects != null && interfaceEffect != null) {
			interfaceEffect.checkGuiData((ContainerUpgrade) con, crafting, true);
		}
	}

	public void checkGuiData(Container con, IContainerListener crafting) {
		if (effects != null && interfaceEffect != null) {
			interfaceEffect.checkGuiData((ContainerUpgrade) con, crafting, false);
		}
	}

	public void receiveGuiData(int id, short data) {
		if (effects != null && interfaceEffect != null) {
			interfaceEffect.receiveGuiData(id, data);
		}
	}

	@Override
	public int getSizeInventory() {
		if (!hasInventory()) {
			if (master == null) {
				return 0;
			} else {
				return master.getSizeInventory();
			}
		}
		return inventoryStacks.size();
	}

	@Override
	public boolean isEmpty() {
		if (hasInventory()) {
			if (master == null) {
				return false;
			} else {
				return master.isEmpty();
			}
		}
		for (ItemStack itemstack : inventoryStacks) {
			if (!itemstack.isEmpty()) {
				return false;
			}
		}
		return true;
	}

	@Override
	public ItemStack getStackInSlot(int index) {
		if (!hasInventory()) {
			if (master == null) {
				return INVALID_STACK;
			} else {
				return master.getStackInSlot(index);
			}
		}
		return inventoryStacks.get(index);
	}

	@Override
	public ItemStack decrStackSize(int index, int count) {
		if (!hasInventory()) {
			if (master == null) {
				return INVALID_STACK;
			} else {
				return master.decrStackSize(index, count);
			}
		} else if (index < 0 || index >= getSizeInventory()) {
			return null;
		}
		ItemStack stack = ItemStackHelper.getAndSplit(inventoryStacks, index, count);
		markDirty();
		return stack;
	}

	@Override
	public void setInventorySlotContents(int index, ItemStack itemStack) {
		if (!hasInventory()) {
			if (master != null) {
				master.setInventorySlotContents(index, itemStack);
			} else {
				return;
			}
		}
		inventoryStacks.set(index, itemStack);
		if (itemStack.getCount() > getInventoryStackLimit()) {
			itemStack.setCount(getInventoryStackLimit());
		}
		markDirty();
	}

	@Override
	public String getName() {
		return "container.assembler_upgrade";
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
	public ItemStack removeStackFromSlot(int index) {
		if (!hasInventory()) {
			return null;
		} else {
			return ItemStackHelper.getAndRemove(inventoryStacks, index);
		}
	}

	@Override
	public void markDirty() {
		if (effects != null && inventoryEffect != null) {
			inventoryEffect.onInventoryChanged();
		}
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack item) {
		if (effects != null && inventoryEffect != null) {
			return inventoryEffect.isItemValid(slot, item);
		}
		return getMaster() != null && getMaster().isItemValidForSlot(slot, item);
	}

	// slots
	@Override
	public int[] getSlotsForFace(EnumFacing side) {
		if (effects != null && inventoryEffect != null) {
			return slotsForSide;
		} else if (getMaster() != null) {
			return getMaster().getSlotsForFace(side);
		} else {
			return new int[0];
		}
	}

	// in
	@Override
	public boolean canInsertItem(int slot, ItemStack item, EnumFacing side) {
		if (effects != null && inventoryEffect != null) {
			return isItemValidForSlot(slot, item);
		}
		return getMaster() != null && getMaster().canInsertItem(slot, item, side);
	}

	// out
	@Override
	public boolean canExtractItem(int slot, ItemStack item, EnumFacing side) {
		return effects != null && inventoryEffect != null || getMaster() != null && getMaster().canExtractItem(slot, item, side);
	}

	// tank stuff
	@Override
	public FluidStack drain(FluidStack resource, boolean doDrain) {
		if (resource != null && resource.isFluidEqual(getFluid())) {
			return drain(resource.amount, doDrain);
		} else {
			return null;
		}
	}

	@Override
	public FluidStack getFluid() {
		if (getTank() == null) {
			return null;
		} else {
			return getTank().getFluid();
		}
	}

	@Override
	public int getCapacity() {
		if (getTank() == null) {
			return 0;
		} else {
			return getTank().getCapacity();
		}
	}

	/**
	 * Fills fluid into internal tanks, distribution is left to the
	 * ITankContainer.
	 * 
	 * @param from
	 *            Orientation the fluid is pumped in from.
	 * @param resource
	 *            FluidStack representing the maximum amount of fluid filled
	 *            into the ITankContainer
	 * @param doFill
	 *            If false filling will only be simulated.
	 * @return Amount of resource that was filled into internal tanks.
	 */
	@Override
	public int fill(FluidStack resource, boolean doFill) {
		if (getTank() == null) {
			return 0;
		} else {
			return getTank().fill(resource, doFill);
		}
	}

	/**
	 * Drains fluid out of internal tanks, distribution is left to the
	 * ITankContainer.
	 * 
	 * @param from
	 *            Orientation the fluid is drained to.
	 * @param maxDrain
	 *            Maximum amount of fluid to drain.
	 * @param doDrain
	 *            If false draining will only be simulated.
	 * @return FluidStack representing the fluid and amount actually drained
	 *         from the ITankContainer
	 */
	@Override
	public FluidStack drain(int maxDrain, boolean doDrain) {
		if (getTank() == null) {
			return null;
		} else {
			return getTank().drain(maxDrain, doDrain);
		}
	}

	@Override
	public int getFluidAmount() {
		return getTank() == null ? 0 : getTank().getFluidAmount();
	}

	@Override
	public FluidTankInfo getInfo() {
		return getTank() == null ? null : getTank().getInfo();
	}

	private Tank getTank() {
		return tankEffect != null ? tankEffect.getTank() : null;
	}

	@Override
	public IFluidTankProperties[] getTankProperties() {
		IFluidTank tank = getTank();
		return new FluidTankProperties[] { new FluidTankProperties(tank.getFluid(), tank.getCapacity()) };
	}

	private void init() {
		for (BaseEffect effect : effects) {
			effect.init();
		}
	}

	private static final String NBT_EFFECT = "Effects";

	private void load(NBTTagCompound compound) {
		NBTTagList list = compound.getTagList(NBT_EFFECT, NBTHelper.COMPOUND.getId());
		int len = Math.min(list.tagCount(), effects.size());
		for (int i = 0; i < len; i++) {
			effects.get(i).load(compound);
		}
	}

	private void save(NBTTagCompound compound) {
		NBTTagList list = new NBTTagList();
		for (BaseEffect effect : effects) {
			NBTTagCompound effectCompound = new NBTTagCompound();
			effect.save(effectCompound);
			list.appendTag(effectCompound);
		}
		compound.setTag(NBT_EFFECT, list);
	}

	@Override
	public void update() {
		if (effects != null && getMaster() != null) {
			for (BaseEffect effect : effects) {
				effect.update();
			}
		}
	}

	public void removed() {
		if (effects != null) {
			for (BaseEffect effect : effects) {
				effect.removed();
			}
		}
	}

	public boolean useStandardInterface() {
		return effects == null || interfaceEffect == null;
	}

	public List<BaseEffect> getEffects() {
		return effects;
	}

	public InterfaceEffect getInterfaceEffect() {
		return interfaceEffect;
	}

	public InventoryEffect getInventoryEffect() {
		return inventoryEffect;
	}

	@SideOnly(Side.CLIENT)
	public GuiBase getGui(InventoryPlayer inv) {
		return new GuiUpgrade(inv, this);
	}

	public ContainerBase getContainer(InventoryPlayer inv) {
		return new ContainerUpgrade(inv, this);
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

	@Override
	public ITextComponent getDisplayName() {
		return null;
	}

	@Override
	public boolean isUsableByPlayer(EntityPlayer player) {
		return true;
	}
}
