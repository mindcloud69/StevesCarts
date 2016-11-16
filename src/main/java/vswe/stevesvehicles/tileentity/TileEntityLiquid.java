package vswe.stevesvehicles.tileentity;

import java.util.ArrayList;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.FluidTankProperties;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vswe.stevesvehicles.client.gui.screen.GuiBase;
import vswe.stevesvehicles.client.gui.screen.GuiLiquid;
import vswe.stevesvehicles.container.ContainerBase;
import vswe.stevesvehicles.container.ContainerLiquid;
import vswe.stevesvehicles.container.ContainerManager;
import vswe.stevesvehicles.container.slots.SlotLiquidFilter;
import vswe.stevesvehicles.container.slots.SlotLiquidManagerInput;
import vswe.stevesvehicles.container.slots.SlotLiquidOutput;
import vswe.stevesvehicles.module.common.storage.tank.ModuleTank;
import vswe.stevesvehicles.tank.ITankHolder;
import vswe.stevesvehicles.tank.Tank;
import vswe.stevesvehicles.tileentity.manager.ManagerTransfer;
import vswe.stevesvehicles.transfer.TransferHandler;
import vswe.stevesvehicles.vehicle.entity.EntityModularCart;

public class TileEntityLiquid extends TileEntityManager implements IFluidHandler, ITankHolder, ISidedInventory {
	@SideOnly(Side.CLIENT)
	@Override
	public GuiBase getGui(InventoryPlayer inv) {
		return new GuiLiquid(inv, this);
	}

	@Override
	public ContainerBase getContainer(InventoryPlayer inv) {
		return new ContainerLiquid(inv, this);
	}

	Tank[] tanks;

	public TileEntityLiquid() {
		super(12);
		tanks = new Tank[4];
		for (int i = 0; i < 4; i++) {
			tanks[i] = new Tank(this, 32000, i);
		}
	}

	public Tank[] getTanks() {
		return tanks;
	}

	private int tick;

	@Override
	public void update() {
		super.update();
		if (tick-- <= 0) {
			tick = 5;
		} else {
			return;
		}
		if (!world.isRemote) {
			for (int i = 0; i < 4; i++) {
				tanks[i].containerTransfer();
			}
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
		int amount = 0;
		if (resource != null && resource.amount > 0) {
			FluidStack fluid = resource.copy();
			for (int i = 0; i < 4; i++) {
				int tempAmount = tanks[i].fill(fluid, doFill, world.isRemote);
				amount += tempAmount;
				fluid.amount -= tempAmount;
				if (fluid.amount <= 0) {
					break;
				}
			}
		}
		return amount;
	}

	/**
	 * Fills fluid into the specified internal tank.
	 * 
	 * @param tankIndex
	 *            the index of the tank to fill
	 * @param resource
	 *            FluidStack representing the maximum amount of fluid filled
	 *            into the ITankContainer
	 * @param doFill
	 *            If false filling will only be simulated.
	 * @return Amount of resource that was filled into internal tanks.
	 */
	public int fill(int tankIndex, FluidStack resource, boolean doFill) {
		if (tankIndex < 0 || tankIndex >= 4) {
			return 0;
		}
		return tanks[tankIndex].fill(resource, doFill, world.isRemote);
	}

	@Override
	public FluidStack drain(int maxDrain, boolean doDrain) {
		return drain((FluidStack) null, maxDrain, doDrain);
	}

	@Override
	public FluidStack drain(FluidStack resource, boolean doDrain) {
		return drain(resource, resource == null ? 0 : resource.amount, doDrain);
	}

	private FluidStack drain(FluidStack resource, int maxDrain, boolean doDrain) {
		FluidStack ret = resource;
		if (ret != null) {
			ret = ret.copy();
			ret.amount = 0;
		}
		for (int i = 0; i < 4; i++) {
			FluidStack temp = tanks[i].drain(maxDrain, false, world.isRemote);
			if (temp != null && (ret == null || ret.isFluidEqual(temp))) {
				temp = tanks[i].drain(maxDrain, doDrain, world.isRemote);
				if (ret == null) {
					ret = temp;
				} else {
					ret.amount += temp.amount;
				}
				maxDrain -= temp.amount;
				if (maxDrain <= 0) {
					break;
				}
			}
		}
		if (ret != null && ret.amount == 0) {
			return null;
		}
		return ret;
	}

	@Override
	public String getName() {
		return "container.fluid_manager";
	}

	@Override
	public ItemStack getInputContainer(int tankId) {
		return getStackInSlot(tankId * 3);
	}

	@Override
	public void clearInputContainer(int tankId) {
		setInventorySlotContents(tankId * 3, null);
	}

	@Override
	public void addToOutputContainer(int tankId, ItemStack item) {
		TransferHandler.TransferItem(item, this, tankId * 3 + 1, tankId * 3 + 1, new ContainerLiquid(null, this), Slot.class, null, -1);
	}

	@Override
	public void onFluidUpdated(int tankId) {
		markDirty();
	}

	//TODO: SPRITES
	/*
	@Override
	@SideOnly(Side.CLIENT)
	public void drawImage(int tankId, GuiBase gui, IIcon icon, int targetX, int targetY, int srcX, int srcY, int sizeX, int sizeY) {
		gui.drawIcon(icon, gui.getGuiLeft() + targetX, gui.getGuiTop() + targetY, sizeX / 16F, sizeY / 16F, srcX / 16F, srcY / 16F);
	}*/

	@Override
	protected boolean isTargetValid(ManagerTransfer transfer) {
		return true;
	}

	@Override
	protected boolean doTransfer(ManagerTransfer transfer) {
		int maximumToTransfer = hasMaxAmount(transfer.getSetting()) ? Math.min(getMaxAmount(transfer.getSetting()) - transfer.getWorkload(), Fluid.BUCKET_VOLUME) : Fluid.BUCKET_VOLUME;
		boolean success = false;
		if (toCart[transfer.getSetting()]) {
			for (int i = 0; i < tanks.length; i++) {
				int fill = fillTank(transfer.getCart(), i, transfer.getSetting(), maximumToTransfer, false);
				if (fill > 0) {
					fillTank(transfer.getCart(), i, transfer.getSetting(), fill, true);
					success = true;
					if (hasMaxAmount(transfer.getSetting())) {
						transfer.setWorkload(transfer.getWorkload() + fill);
					}
					break;
				}
			}
		} else {
			ArrayList<ModuleTank> cartTanks = transfer.getCart().getVehicle().getTanks();
			for (IFluidTank cartTank : cartTanks) {
				int drain = drainTank(cartTank, transfer.getSetting(), maximumToTransfer, false);
				if (drain > 0) {
					drainTank(cartTank, transfer.getSetting(), drain, true);
					success = true;
					if (hasMaxAmount(transfer.getSetting())) {
						transfer.setWorkload(transfer.getWorkload() + drain);
					}
					break;
				}
			}
		}
		if (success && hasMaxAmount(transfer.getSetting()) && transfer.getWorkload() == getMaxAmount(transfer.getSetting())) {
			transfer.setLowestSetting(transfer.getSetting() + 1); // this is not
			// available
			// anymore
		}
		return success;
	}

	private int fillTank(EntityModularCart cart, int tankId, int sideId, int fillAmount, boolean doFill) {
		if (isTankValid(tankId, sideId)) {
			FluidStack fluidToFill = tanks[tankId].drain(fillAmount, doFill);
			if (fluidToFill == null) {
				return 0;
			}
			fillAmount = fluidToFill.amount;
			if (isFluidValid(sideId, fluidToFill)) {
				ArrayList<ModuleTank> cartTanks = cart.getVehicle().getTanks();
				for (IFluidTank cartTank : cartTanks) {
					fluidToFill.amount -= cartTank.fill(fluidToFill, doFill);
					if (fluidToFill.amount <= 0) {
						return fillAmount;
					}
				}
				return fillAmount - fluidToFill.amount;
			}
		}
		return 0;
	}

	private int drainTank(IFluidTank cartTank, int sideId, int drainAmount, boolean doDrain) {
		FluidStack drainedFluid = cartTank.drain(drainAmount, doDrain);
		if (drainedFluid == null) {
			return 0;
		}
		drainAmount = drainedFluid.amount;
		if (isFluidValid(sideId, drainedFluid)) {
			for (int i = 0; i < tanks.length; i++) {
				Tank tank = tanks[i];
				if (isTankValid(i, sideId)) {
					drainedFluid.amount -= tank.fill(drainedFluid, doDrain);
					if (drainedFluid.amount <= 0) {
						return drainAmount;
					}
				}
			}
			return drainAmount - drainedFluid.amount;
		}
		return 0;
	}

	private boolean isTankValid(int tankId, int sideId) {
		return !((layoutType == 1 && tankId != sideId) || (layoutType == 2 && color[sideId] != color[tankId]));
	}

	private boolean isFluidValid(int sideId, FluidStack fluid) {
		ItemStack filter = getStackInSlot(sideId * 3 + 2);
		FluidStack filterFluid = FluidUtil.getFluidContained(filter);
		if (filterFluid != null) {
			if (!filterFluid.isFluidEqual(fluid)) {
				return false;
			}
		}
		return true;
	}

	public int getMaxAmount(int id) {
		return (int) (getMaxAmountBuckets(id) * Fluid.BUCKET_VOLUME);
	}

	public float getMaxAmountBuckets(int id) {
		switch (getAmountId(id)) {
			case 1:
				return 0.25F;
			case 2:
				return 0.5F;
			case 3:
				return 0.75F;
			case 4:
				return 1F;
			case 5:
				return 2F;
			case 6:
				return 3F;
			case 7:
				return 5F;
			case 8:
				return 7.5F;
			case 9:
				return 10F;
			case 10:
				return 15F;
			default:
				return 0;
		}
	}

	public boolean hasMaxAmount(int id) {
		return getAmountId(id) != 0;
	}

	@Override
	public int getAmountCount() {
		return 11;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		super.readFromNBT(nbttagcompound);
		for (int i = 0; i < 4; i++) {
			tanks[i].setFluid(FluidStack.loadFluidStackFromNBT(nbttagcompound.getCompoundTag("Fluid" + i)));
		}
		setWorkload(nbttagcompound.getShort("workload"));
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbttagcompound) {
		super.writeToNBT(nbttagcompound);
		for (int i = 0; i < 4; i++) {
			if (tanks[i].getFluid() != null) {
				NBTTagCompound compound = new NBTTagCompound();
				tanks[i].getFluid().writeToNBT(compound);
				nbttagcompound.setTag("Fluid" + i, compound);
			}
		}
		nbttagcompound.setShort("workload", (short) getWorkload());
		return nbttagcompound;
	}

	@Override
	public void checkGuiData(ContainerManager conManager, IContainerListener crafting, boolean isNew) {
		super.checkGuiData(conManager, crafting, isNew);
		ContainerLiquid con = (ContainerLiquid) conManager;
		for (int i = 0; i < 4; i++) {
			boolean changed = false;
			int id = 4 + i * 4;
			int amount1 = 4 + i * 4 + 1;
			int amount2 = 4 + i * 4 + 2;
			if ((isNew || con.oldLiquids[i] != null) && tanks[i].getFluid() == null) {
				updateGuiData(con, crafting, id, (short) -1);
				changed = true;
			} else if (tanks[i].getFluid() != null) {
				if (isNew || con.oldLiquids[i] == null) {
					updateGuiData(con, crafting, id, (short) FluidRegistry.getFluidID(tanks[i].getFluid().getFluid()));
					updateGuiData(con, crafting, amount1, getShortFromInt(true, tanks[i].getFluid().amount));
					updateGuiData(con, crafting, amount2, getShortFromInt(false, tanks[i].getFluid().amount));
					changed = true;
				} else {
					if (!con.oldLiquids[i].getFluid().getName().equals(tanks[i].getFluid().getFluid().getName())) {
						updateGuiData(con, crafting, id, (short) FluidRegistry.getFluidID(tanks[i].getFluid().getFluid()));
						changed = true;
					}
					if (con.oldLiquids[i].amount != tanks[i].getFluid().amount) {
						updateGuiData(con, crafting, amount1, getShortFromInt(true, tanks[i].getFluid().amount));
						updateGuiData(con, crafting, amount2, getShortFromInt(false, tanks[i].getFluid().amount));
						changed = true;
					}
				}
			}
			if (changed) {
				if (tanks[i].getFluid() == null) {
					con.oldLiquids[i] = null;
				} else {
					con.oldLiquids[i] = tanks[i].getFluid().copy();
				}
			}
		}
	}

	// TODO sync the tag somehow :S
	@Override
	public void receiveGuiData(int id, short data) {
		if (id > 3) {
			id -= 4;
			int tankId = id / 4;
			int contentId = id % 4;
			if (contentId == 0) {
				if (data == -1) {
					tanks[tankId].setFluid(null);
				} else if (tanks[tankId].getFluid() == null) {
					tanks[tankId].setFluid(new FluidStack(FluidRegistry.getFluid(data), 0));
				}
			} else if (tanks[tankId].getFluid() != null) {
				tanks[tankId].getFluid().amount = getIntFromShort(contentId == 1, tanks[tankId].getFluid().amount, data);
			}
		} else {
			super.receiveGuiData(id, data);
		}
	}

	private boolean isInput(int id) {
		return id % 3 == 0;
	}

	private boolean isOutput(int id) {
		return id % 3 == 1;
	}

	@Override
	public boolean isItemValidForSlot(int slotId, ItemStack item) {
		if (isInput(slotId)) {
			return SlotLiquidManagerInput.isItemStackValid(item, this, -1);
		} else if (isOutput(slotId)) {
			return SlotLiquidOutput.isItemStackValid(item);
		} else {
			return SlotLiquidFilter.isItemStackValid(item);
		}
	}

	private static final int[] TOP_SLOTS = new int[] { 0, 3, 6, 9 };
	private static final int[] BOT_SLOTS = new int[] { 1, 4, 7, 10 };
	private static final int[] SIDE_SLOTS = new int[] {};

	// slots
	@Override
	public int[] getSlotsForFace(EnumFacing side) {
		if (side == EnumFacing.UP) {
			return TOP_SLOTS;
		} else if (side == EnumFacing.DOWN) {
			return BOT_SLOTS;
		} else {
			return SIDE_SLOTS;
		}
	}

	// in
	@Override
	public boolean canInsertItem(int slot, ItemStack item, EnumFacing side) {
		return side == EnumFacing.UP && isInput(slot) && this.isItemValidForSlot(slot, item);
	}

	// out
	@Override
	public boolean canExtractItem(int slot, ItemStack item, EnumFacing side) {
		return side == EnumFacing.DOWN && isOutput(slot);
	}

	@Override
	public IFluidTankProperties[] getTankProperties() {
		IFluidTankProperties[] info = new IFluidTankProperties[tanks.length];
		for (int i = 0; i < tanks.length; i++) {
			info[i] = new FluidTankProperties(tanks[i].getFluid(), tanks[i].getCapacity());
		}
		return info;
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
