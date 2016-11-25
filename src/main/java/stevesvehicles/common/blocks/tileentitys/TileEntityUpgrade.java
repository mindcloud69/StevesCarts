package stevesvehicles.common.blocks.tileentitys;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.NonNullList;
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
import stevesvehicles.common.blocks.BlockUpgrade;
import stevesvehicles.common.blocks.ModBlocks;
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

public class TileEntityUpgrade extends TileEntityInventory implements ISidedInventory, IFluidHandler, IFluidTank, ITickable {
	public static final NonNullList<ItemStack> DEFAULT_STACKS = NonNullList.withSize(0, INVALID_STACK);
	private TileEntityCartAssembler master;
	private int type;
	private List<BaseEffect> effects;
	private InterfaceEffect interfaceEffect;
	private InventoryEffect inventoryEffect;
	private TankEffect tankEffect;
	BlockUpgrade blockUpgrade = (BlockUpgrade) ModBlocks.UPGRADE.getBlock();
	// INVENTORY STUFF BELOW
	private int[] slotsForSide;

	public TileEntityUpgrade() {
		super(DEFAULT_STACKS);
	}

	public void setMaster(TileEntityCartAssembler master, EnumFacing side) {
		if (this.master != master) {
			if (!world.isRemote) {
				IBlockState state = world.getBlockState(pos);
				if (side != null) {
					state = blockUpgrade.getDefaultState().withProperty(BlockUpgrade.FACING, side);
					world.setBlockState(pos, state);
				}
			}
		}
		this.master = master;
	}

	public EnumFacing getSide() {
		return world.getBlockState(pos).getValue(BlockUpgrade.FACING);
	}

	public TileEntityCartAssembler getMaster() {
		return master;
	}

	private boolean initialized;

	public void setType(int type) {
		this.type = type;
		if (!initialized) {
			initialized = true;
			Upgrade upgrade = getUpgrade();
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
		}
	}

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

	@Override
	public NBTTagCompound getUpdateTag() {
		NBTTagCompound tagCompound = super.getUpdateTag();
		tagCompound.setByte("Type", (byte) getType());
		return tagCompound;
	}

	@Override
	public SPacketUpdateTileEntity getUpdatePacket() {
		return new SPacketUpdateTileEntity(getPos(), 0, writeToNBT(new NBTTagCompound()));
	}

	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
		this.readFromNBT(pkt.getNbtCompound());
	}

	public Upgrade getUpgrade() {
		return UpgradeRegistry.getUpgradeFromId(type);
	}

	public int getType() {
		return type;
	}

	public boolean hasInventory() {
		return !inventoryStacks.isEmpty();
	}

	@Override
	public void readFromNBT(NBTTagCompound tagCompound) {
		if (tagCompound.hasKey("Type")) {
			setType(tagCompound.getByte("Type"));
		}
		super.readFromNBT(tagCompound);
		if (effects != null) {
			load(tagCompound);
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tagCompound) {
		super.writeToNBT(tagCompound);
		tagCompound.setByte("Type", (byte) getType());
		if (effects != null) {
			save(tagCompound);
		}
		return tagCompound;
	}

	@Override
	public void initGuiData(Container con, IContainerListener crafting) {
		if (effects != null && interfaceEffect != null) {
			interfaceEffect.checkGuiData((ContainerUpgrade) con, crafting, true);
		}
	}

	@Override
	public void checkGuiData(Container con, IContainerListener crafting) {
		if (effects != null && interfaceEffect != null) {
			interfaceEffect.checkGuiData((ContainerUpgrade) con, crafting, false);
		}
	}

	@Override
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
		return super.getSizeInventory();
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
		return super.isEmpty();
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
		return super.getStackInSlot(index);
	}

	@Override
	public ItemStack decrStackSize(int index, int size) {
		if (!hasInventory()) {
			if (master == null) {
				return INVALID_STACK;
			} else {
				return master.decrStackSize(index, size);
			}
		} else if (index < 0 || index >= getSizeInventory()) {
			return null;
		}
		ItemStack stack = super.decrStackSize(index, size);
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
		super.setInventorySlotContents(index, itemStack);
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
			ItemStack item = getStackInSlot(index);
			if (item != null) {
				setInventorySlotContents(index, ItemStack.EMPTY);
				return item;
			} else {
				return null;
			}
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
	@Override
	public GuiBase getGui(InventoryPlayer inv) {
		return new GuiUpgrade(inv, this);
	}

	@Override
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
}
