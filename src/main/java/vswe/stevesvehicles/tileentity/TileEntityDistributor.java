package vswe.stevesvehicles.tileentity;

import java.util.ArrayList;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vswe.stevesvehicles.client.gui.screen.GuiBase;
import vswe.stevesvehicles.client.gui.screen.GuiDistributor;
import vswe.stevesvehicles.container.ContainerBase;
import vswe.stevesvehicles.container.ContainerDistributor;
import vswe.stevesvehicles.localization.entry.block.LocalizationDistributor;
import vswe.stevesvehicles.network.DataReader;
import vswe.stevesvehicles.tank.Tank;
import vswe.stevesvehicles.tileentity.distributor.DistributorSetting;
import vswe.stevesvehicles.tileentity.distributor.DistributorSide;

public class TileEntityDistributor extends TileEntityBase implements IInventory, ISidedInventory, ITickable {
	@SideOnly(Side.CLIENT)
	@Override
	public GuiBase getGui(InventoryPlayer inv) {
		return new GuiDistributor(this);
	}

	@Override
	public ContainerBase getContainer(InventoryPlayer inv) {
		return new ContainerDistributor(this);
	}

	private final ArrayList<DistributorSide> sides;

	public ArrayList<DistributorSide> getSides() {
		return sides;
	}

	public TileEntityDistributor() {
		sides = new ArrayList<>();
		sides.add(new DistributorSide(0, LocalizationDistributor.ORANGE, EnumFacing.UP, this));
		sides.add(new DistributorSide(1, LocalizationDistributor.PURPLE, EnumFacing.DOWN, this));
		sides.add(new DistributorSide(2, LocalizationDistributor.YELLOW, EnumFacing.NORTH, this));
		sides.add(new DistributorSide(3, LocalizationDistributor.GREEN, EnumFacing.WEST, this));
		sides.add(new DistributorSide(4, LocalizationDistributor.BLUE, EnumFacing.SOUTH, this));
		sides.add(new DistributorSide(5, LocalizationDistributor.RED, EnumFacing.EAST, this));
	}

	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		super.readFromNBT(nbttagcompound);
		for (DistributorSide side : getSides()) {
			side.setData(nbttagcompound.getInteger("Side" + side.getId()));
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbttagcompound) {
		super.writeToNBT(nbttagcompound);
		for (DistributorSide side : getSides()) {
			nbttagcompound.setInteger("Side" + side.getId(), side.getData());
		}
		return nbttagcompound;
	}

	private boolean dirty = true;

	@Override
	public void update() {
		dirty = true;
	}

	@Override
	public void receivePacket(DataReader dr, EntityPlayer player) {
		int settingId = dr.readByte();
		int sideId = dr.readByte();
		if (settingId >= 0 && settingId < DistributorSetting.settings.size() && sideId >= 0 && sideId < getSides().size()) {
			if (dr.readBoolean()) {
				getSides().get(sideId).set(settingId);
			} else {
				getSides().get(sideId).reset(settingId);
			}
		}
	}

	@Override
	public void initGuiData(Container con, IContainerListener crafting) {
	}

	@Override
	public void checkGuiData(Container con, IContainerListener crafting) {
		ContainerDistributor distributor = (ContainerDistributor) con;
		for (int i = 0; i < getSides().size(); i++) {
			DistributorSide side = getSides().get(i);
			if (side.getLowShortData() != distributor.cachedValues.get(i * 2)) {
				updateGuiData(con, crafting, i * 2, side.getLowShortData());
				distributor.cachedValues.set(i * 2, side.getLowShortData());
			}
			if (side.getHighShortData() != distributor.cachedValues.get(i * 2 + 1)) {
				updateGuiData(con, crafting, i * 2 + 1, side.getHighShortData());
				distributor.cachedValues.set(i * 2 + 1, side.getHighShortData());
			}
		}
	}

	@Override
	public void receiveGuiData(int id, short data) {
		int sideId = id / 2;
		boolean isHigh = id % 2 == 1;
		DistributorSide side = getSides().get(sideId);
		if (isHigh) {
			side.setHighShortData(data);
		} else {
			side.setLowShortData(data);
		}
	}

	private TileEntityManager[] inventories;
	public boolean hasTop;
	public boolean hasBot;

	public TileEntityManager[] getInventories() {
		if (dirty) {
			generateInventories();
			dirty = false;
		}
		return inventories;
	}

	private void generateInventories() {
		TileEntityManager bot = generateManager(-1);
		TileEntityManager top = generateManager(+1);
		hasTop = top != null;
		hasBot = bot != null;
		inventories = populateManagers(top, bot, hasTop, hasBot);
	}

	private TileEntityManager[] populateManagers(TileEntityManager topElement, TileEntityManager botElement, boolean hasTopElement, boolean hasBotElement) {
		if (!hasTopElement && !hasBotElement) {
			return new TileEntityManager[] {};
		} else if (!hasBotElement) {
			return new TileEntityManager[] { topElement };
		} else if (!hasTopElement) {
			return new TileEntityManager[] { botElement };
		} else {
			return new TileEntityManager[] { botElement, topElement };
		}
	}

	private TileEntityManager generateManager(int y) {
		TileEntity TE = worldObj.getTileEntity(pos.add(0, y, 0));
		if (TE != null && TE instanceof TileEntityManager) {
			return (TileEntityManager) TE;
		}
		return null;
	}

	private int translateSlotId(int slot) {
		return slot % 60;
	}

	private TileEntityManager getManagerFromSlotId(int slot) {
		TileEntityManager[] inventories = getInventories();
		int id = slot / 60;
		if (!hasTop || !hasBot) {
			id = 0;
		}
		if (id < 0 || id >= inventories.length) {
			return null;
		} else {
			return inventories[id];
		}
	}

	@Override
	public int getSizeInventory() {
		return 120;
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		TileEntityManager manager = getManagerFromSlotId(slot);
		if (manager != null) {
			return manager.getStackInSlot(translateSlotId(slot));
		} else {
			return null;
		}
	}

	@Override
	public ItemStack decrStackSize(int slot, int count) {
		TileEntityManager manager = getManagerFromSlotId(slot);
		if (manager != null) {
			return manager.decrStackSize(translateSlotId(slot), count);
		} else {
			return null;
		}
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack itemstack) {
		TileEntityManager manager = getManagerFromSlotId(slot);
		if (manager != null) {
			manager.setInventorySlotContents(translateSlotId(slot), itemstack);
		}
	}

	@Override
	public String getName() {
		return "container.external_distributor";
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
		TileEntityManager manager = getManagerFromSlotId(index);
		if (manager != null) {
			return manager.removeStackFromSlot(translateSlotId(index));
		} else {
			return null;
		}
	}

	private boolean isChunkValid(DistributorSide side, TileEntityManager manager, int chunkId, boolean top) {
		for (DistributorSetting setting : DistributorSetting.settings) {
			if (setting.isEnabled(this)) {
				if (side.isSet(setting.getId())) {
					if (setting.isValid(manager, chunkId, top)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * @param direction
	 *            tank side: UNKNOWN for default tank set
	 * @return Array of {@link FluidTank}s contained in this ITankContainer for
	 *         this direction
	 */
	public IFluidTank[] getTanks(EnumFacing facing) {
		TileEntityManager[] inventories = getInventories();
		if (inventories.length > 0) {
			for (DistributorSide side : getSides()) {
				if (side.getSide() == facing) {
					ArrayList<IFluidTank> tanks = new ArrayList<>();
					if (hasTop && hasBot) {
						populateTanks(tanks, side, inventories[0], false);
						populateTanks(tanks, side, inventories[1], true);
					} else if (hasTop) {
						populateTanks(tanks, side, inventories[0], true);
					} else if (hasBot) {
						populateTanks(tanks, side, inventories[0], false);
					}
					return tanks.toArray(new IFluidTank[tanks.size()]);
				}
			}
		}
		return new IFluidTank[] {};
	}

	private void populateTanks(ArrayList<IFluidTank> tanks, DistributorSide side, TileEntityManager manager, boolean top) {
		if (manager != null && manager instanceof TileEntityLiquid) {
			TileEntityLiquid fluid = (TileEntityLiquid) manager;
			Tank[] managerTanks = fluid.getTanks();
			for (int i = 0; i < 4; i++) {
				if (isChunkValid(side, manager, i, top)) {
					if (!tanks.contains(managerTanks[i])) {
						tanks.add(managerTanks[i]);
					}
				}
			}
		}
	}

	private void populateSlots(ArrayList<Integer> slotChunks, DistributorSide side, TileEntityManager manager, boolean top) {
		if (manager != null && manager instanceof TileEntityCargo) {
			for (int i = 0; i < 4; i++) {
				if (isChunkValid(side, manager, i, top)) {
					int chunkId = i + (top ? 4 : 0);
					if (!slotChunks.contains(chunkId)) {
						slotChunks.add(chunkId);
					}
				}
			}
		}
	}

	// slots
	@Override
	public int[] getSlotsForFace(EnumFacing direction) {
		TileEntityManager[] inventories = getInventories();
		if (inventories.length > 0) {
			for (DistributorSide side : getSides()) {
				if (side.getSide() == direction) {
					ArrayList<Integer> slotChunks = new ArrayList<>();
					if (hasTop && hasBot) {
						populateSlots(slotChunks, side, inventories[0], false);
						populateSlots(slotChunks, side, inventories[1], true);
					} else if (hasTop) {
						populateSlots(slotChunks, side, inventories[0], true);
					} else if (hasBot) {
						populateSlots(slotChunks, side, inventories[0], false);
					}
					int[] ret = new int[slotChunks.size() * 15];
					int id = 0;
					for (int chunkId : slotChunks) {
						for (int i = 0; i < 15; i++) {
							ret[id] = chunkId * 15 + i;
							id++;
						}
					}
					return ret;
				}
			}
		}
		return new int[] {};
	}

	// in
	@Override
	public boolean canInsertItem(int slot, ItemStack item, EnumFacing side) {
		return true;
	}

	// out
	@Override
	public boolean canExtractItem(int slot, ItemStack item, EnumFacing side) {
		return true;
	}

	@Override
	public boolean isItemValidForSlot(int slotId, ItemStack item) {
		return true;
	}
	
	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		return capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
	}
	
	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if(capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY){
			return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast(getSides().get(facing.getIndex()));
		}
		return super.getCapability(capability, facing);
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
