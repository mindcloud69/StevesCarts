package vswe.stevesvehicles.module.cart.tool;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.BlockRailBase;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vswe.stevesvehicles.block.BlockCoordinate;
import vswe.stevesvehicles.block.ModBlocks;
import vswe.stevesvehicles.client.gui.assembler.SimulationInfo;
import vswe.stevesvehicles.client.gui.assembler.SimulationInfoBoolean;
import vswe.stevesvehicles.client.gui.screen.GuiVehicle;
import vswe.stevesvehicles.localization.entry.block.LocalizationAssembler;
import vswe.stevesvehicles.localization.entry.module.cart.LocalizationCartTool;
import vswe.stevesvehicles.module.IActivatorModule;
import vswe.stevesvehicles.module.ModuleBase;
import vswe.stevesvehicles.module.cart.addon.ModuleDrillIntelligence;
import vswe.stevesvehicles.module.cart.addon.ModuleIncinerator;
import vswe.stevesvehicles.module.cart.addon.ModuleLiquidSensors;
import vswe.stevesvehicles.module.cart.addon.ModuleOreTracker;
import vswe.stevesvehicles.module.common.storage.chest.ModuleChest;
import vswe.stevesvehicles.network.DataReader;
import vswe.stevesvehicles.vehicle.VehicleBase;

public abstract class ModuleDrill extends ModuleTool implements IActivatorModule {
	private DataParameter<Boolean> IS_MINING;
	private DataParameter<Boolean> IS_ENABLED;
	public ModuleDrill(VehicleBase vehicleBase) {
		super(vehicleBase);
	}

	// lower numbers are prioritized
	@Override
	public byte getWorkPriority() {
		return 50;
	}

	@Override
	public void loadSimulationInfo(List<SimulationInfo> simulationInfo) {
		simulationInfo.add(new SimulationInfoBoolean(LocalizationAssembler.INFO_DRILL, "drill"));
	}

	private ModuleDrillIntelligence intelligence;
	private ModuleLiquidSensors liquidSensors;
	private ModuleOreTracker tracker;

	@Override
	public void init() {
		super.init();
		for (ModuleBase module : getVehicle().getModules()) {
			if (module instanceof ModuleDrillIntelligence) {
				intelligence = (ModuleDrillIntelligence) module;
			}
			if (module instanceof ModuleLiquidSensors) {
				liquidSensors = (ModuleLiquidSensors) module;
			}
			if (module instanceof ModuleOreTracker) {
				tracker = (ModuleOreTracker) module;
			}
		}
	}

	// return true when the work is done, false allow other modules to continue
	// the work
	@Override
	public boolean work() {
		if (!isDrillEnabled()) {
			stopDrill();
			stopWorking();
			return false;
		} else if (!doPreWork()) {
			stopDrill();
			stopWorking();
		}
		// get the next block so the cart knows where to mine
		BlockPos next = getNextBlock();
		// retrieve the height range of the hole
		int[] range = mineRange();
		// loop through the blocks in the "hole" in front of the cart
		for (int holeY = range[1]; holeY >= range[0]; holeY--) {
			for (int holeX = -blocksOnSide(); holeX <= blocksOnSide(); holeX++) {
				if (intelligence != null && !intelligence.isActive(holeX + blocksOnSide(), holeY, range[2], next.getX() > getVehicle().x() || next.getZ() < getVehicle().z())) {
					continue;
				}
				// calculate the coordinates of this "hole"
				if (mineBlockAndRevive(next.add((getVehicle().z() != next.getZ() ? holeX : 0), holeY, (getVehicle().x() != next.getX() ? holeX : 0)), next, holeX, holeY)) {
					return true;
				}
			}
		}
		if (countsAsAir(next.add(0, range[0], 0)) && !isValidForTrack(next.add(0, range[0], 0), true) && mineBlockAndRevive(next.add(0, (range[0] - 1), 0), next, 0, range[0] - 1)) {
			return true;
		}
		// if the code goes all the way to here, the cart is still ready for
		// action. Return false.
		stopWorking();
		stopDrill();
		return false;
	}

	/**
	 * Return the height range of the mined hole
	 **/
	protected int[] mineRange() {
		// the first element is the start index, the last is the end index
		// get the next block
		BlockPos next = getNextBlock();
		int yTarget = getModularCart().getYTarget();
		if (BlockRailBase.isRailBlock(getVehicle().getWorld(), next) || BlockRailBase.isRailBlock(getVehicle().getWorld(), next.down())) {
			return new int[] { 0, blocksOnTop() - 1, 1 };
		} else if (next.getY() > yTarget) {
			return new int[] { -1, blocksOnTop() - 1, 1 };
		} else if (next.getY() < yTarget) {
			return new int[] { 1, blocksOnTop() + 1, 0 };
		} else {
			return new int[] { 0, blocksOnTop() - 1, 1 };
		}
	}

	// returns how far the drill should drill above itself, i.e. how tall is the
	// hole
	protected abstract int blocksOnTop();

	// returns how far the drill should drill on each side
	protected abstract int blocksOnSide();

	public int getAreaWidth() {
		return blocksOnSide() * 2 + 1;
	}

	public int getAreaHeight() {
		return blocksOnTop();
	}

	private boolean mineBlockAndRevive(BlockPos target, BlockPos next, int holeX, int holeY) {
		if (mineBlock(target, next, holeX, holeY, false)) {
			return true;
		} else if (isDead()) {
			revive();
			return true;
		} else {
			return false;
		}
	}

	protected boolean mineBlock(BlockPos target, BlockPos next, int holeX, int holeY, boolean flag) {
		World world = getVehicle().getWorld();
		if (tracker != null) {
			BlockPos nextTarget = tracker.findBlockToMine(this, target);
			if (nextTarget != null) {
				target = nextTarget;
			}
		}
		// test whether this block is valid or not
		Object valid = isValidBlock(target, holeX, holeY, flag);
		TileEntity storage = null;
		if (valid instanceof TileEntity) {
			// if the test code found the block to be valid but it returned a
			// TileEntity it means we have to remove all items inside its
			// inventory too(therefore save the TileEntity)
			storage = (TileEntity) valid;
		} else if (valid == null) {
			// if this block wasn't valid, try the next one
			return false;
		}
		// retrieve some information about the block
		IBlockState state = world.getBlockState(target);
		Block block = state.getBlock();
		// if the cart hasn't been working, tell it to work and it's therefore
		// done for this tick
		// if (doPreWork())
		// {
		// calculate the working time
		float h = state.getBlockHardness(world, target);
		if (h < 0) {
			h = 0;
		}
		// CrumbleBlocksHook.startCrumblingBlock(this, targetX, targetY,
		// targetZ, workingTime);
		// return true;
		// }
		// if the cart is already working, the code below should be run
		ItemStack item;
		// if the block is a block with an inventory, empty it first
		if (storage != null) {
			// loop through the inventory
			for (int i = 0; i < ((IInventory) (storage)).getSizeInventory(); i++) {
				// pick out an item from the next slot
				item = ((IInventory) (storage)).getStackInSlot(i);
				// check if there was anything there
				if (item == null) {
					continue;
				}
				// let the cart do its thing with the item, on success remove it
				// from the inventory.
				if (minedItem(item, next)) {
					((IInventory) (storage)).setInventorySlotContents(i, null);
				} else {
					/*
					 * turnBack(); stopWorking(); return false;
					 */
					return false;
				}
			}
		}
		int fortune = enchanter != null ? enchanter.getFortuneLevel() : 0;
		List<ItemStack> drops = block.getDrops(getVehicle().getWorld(), target, state, fortune);
		if (shouldSilkTouch(state, target)) {
			ItemStack silkTouchedItem = getSilkTouchedItem(state);
			if (silkTouchedItem == null || minedItem(silkTouchedItem, next)) {
				getVehicle().getWorld().setBlockToAir(target);
			} else {
				return false;
			}
			// if the block drops anything, drop it
		} else if (!drops.isEmpty()) {
			// iStack = new ItemStack(b.idDropped(0, rand, 0),
			// b.quantityDropped(rand), b.damageDropped(m));
			List<ItemStack> stacks = block.getDrops(getVehicle().getWorld(), target, state, fortune);
			boolean shouldRemove = false;
			for (ItemStack stack : stacks) {
				if (minedItem(stack, next)) {
					shouldRemove = true;
				} else {
					/*
					 * turnBack(); stopWorking(); return false
					 */
					return false;
				}
			}
			if (shouldRemove) {
				getVehicle().getWorld().setBlockToAir(target);
			}
		} else {
			// mark this cart as idle and remove the block
			getVehicle().getWorld().setBlockToAir(target);
		}
		damageTool(1 + (int) h);
		startWorking(getTimeToMine(h));
		startDrill();
		return true;
	}

	/**
	 * Let the cart handle a mined item, return true upon success.
	 **/
	protected boolean minedItem(ItemStack item, BlockPos coordinate) {
		if (item == null || item.stackSize <= 0) {
			return true;
		}
		for (ModuleBase module : getVehicle().getModules()) {
			if (module instanceof ModuleIncinerator) {
				((ModuleIncinerator) module).incinerate(item);
				if (item.stackSize <= 0) {
					return true;
				}
			}
		}
		int size = item.stackSize;
		getVehicle().addItemToChest(item);
		if (item.stackSize == 0) {
			// everything worked fine
			return true;
		} else {
			boolean hasChest = false;
			for (ModuleBase module : getVehicle().getModules()) {
				if (module instanceof ModuleChest) {
					hasChest = true;
					break;
				}
			}
			if (hasChest) {
				if (item.stackSize != size) {
					// if only some items did fit in the chest we have no other
					// choice than spitting out the rest
					// but don't do it the normal way, that would only make the
					// Mining cart w/ chest get stuck and
					// the whole point with it is to avoid that, spit it out to
					// its side instead
					EntityItem entityitem = new EntityItem(getVehicle().getWorld(), getVehicle().getEntity().posX, getVehicle().getEntity().posY, getVehicle().getEntity().posZ, item);
					// observe that the motion for X uses the Z coordinate and
					// vice-versa
					entityitem.motionX = (float) (getVehicle().z() - coordinate.getZ()) / 10;
					entityitem.motionY = 0.15F;
					entityitem.motionZ = (float) (getVehicle().x() - coordinate.getX()) / 10;
					getVehicle().getWorld().spawnEntityInWorld(entityitem);
					return true;
				} else {
					// let's get out of here!
					return false;
				}
			} else {
				// pop out the item out of the cart's back
				EntityItem entityitem = new EntityItem(getVehicle().getWorld(), getVehicle().getEntity().posX, getVehicle().getEntity().posY, getVehicle().getEntity().posZ, item);
				entityitem.motionX = (float) (getVehicle().x() - coordinate.getX()) / 10;
				entityitem.motionY = 0.15F;
				entityitem.motionZ = (float) (getVehicle().z() - coordinate.getZ()) / 10;
				getVehicle().getWorld().spawnEntityInWorld(entityitem);
				return true;
			}
		}
	}

	private int getTimeToMine(float hardness) {
		int efficiency = enchanter != null ? enchanter.getEfficiencyLevel() : 0;
		return (int) ((getTimeMultiplier() * hardness) / Math.pow(1.3F, efficiency)) + (liquidSensors != null ? 2 : 0);
	}

	protected abstract float getTimeMultiplier();

	/**
	 * Method to check if a block is a valid block to remove by the miner
	 * 
	 * x, y and z is the coordinates of the block while i and j is offset
	 * location compared to the minecart
	 * 
	 * if this returns null the block is not valid, all other values are valid.
	 **/
	public Object isValidBlock(BlockPos pos, int i, int j, boolean flag) {
		// do not remove rail blocks or block which will cause rail blocks to be
		// removed
		if ((!flag && BlockRailBase.isRailBlock(getVehicle().getWorld(), pos)) || BlockRailBase.isRailBlock(getVehicle().getWorld(), pos.up())) {
			return null;
		} else {
			// retrieve the needed values, block id and the like
			IBlockState state = getVehicle().getWorld().getBlockState(pos);
			Block block = state.getBlock();
			// there need to be a block to remove
			if (block == null) {
				return null;
				// don't try to remove air
			} else if (getVehicle().getWorld().isAirBlock(pos)) {
				return null;
				// don't remove bedrock
			} else if (block == Blocks.BEDROCK) {
				return null;
				// don't remove fluids either
			} else if (block instanceof BlockLiquid) {
				return null;
				// nor things which can't be removed
			} else if (state.getBlockHardness(getVehicle().getWorld(), pos) < 0) {
				return null;
				// some special things are just allowed to be removed when in
				// font of the cart, like torches
			} else if ((i != 0 || j > 0) && (block == Blocks.TORCH || block == Blocks.REDSTONE_WIRE || block == Blocks.REDSTONE_TORCH || block == Blocks.UNLIT_REDSTONE_TORCH || block == Blocks.POWERED_REPEATER || block == Blocks.UNPOWERED_REPEATER
					|| block == Blocks.POWERED_COMPARATOR || block == Blocks.UNPOWERED_COMPARATOR || block == ModBlocks.MODULE_TOGGLER.getBlock())) {
				return null;
				// for containers like chest a special rule apply, therefore
				// test if this is a container
			} else if (block instanceof BlockContainer) {
				// if so load its tile entity to check if it has an inventory or
				// not
				TileEntity tileentity = getVehicle().getWorld().getTileEntity(pos);
				if (tileentity != null && IInventory.class.isInstance(tileentity)) {
					// depending on its position it's either invalid or we
					// should return the tile entity to be able to remove its
					// items
					if (i != 0 || j > 0) {
						return null;
					} else {
						return tileentity;
					}
				}
			}
			if (liquidSensors != null) {
				// check all five directions for danger(no need to check below
				// since liquids can't flow upwards ^^)
				if (liquidSensors.isDangerous(this, pos.up(), true) || liquidSensors.isDangerous(this, pos.east(), false) || liquidSensors.isDangerous(this, pos.west(), false) || liquidSensors.isDangerous(this, pos.south(), false) || liquidSensors.isDangerous(this, pos.north(), false)) {
					sensorLight = (byte) 3;
					return null;
				}
				sensorLight = (byte) 2;
			}
			// if the code goes all the way the block is valid to remove
			return false;
		}
	}

	@Override
	public void update() {
		super.update();
		if ((getVehicle().hasFuel() && isMining()) || miningCoolDown < 10) {
			drillRotation = (float) ((drillRotation + 0.03F * (10 - miningCoolDown)) % (Math.PI * 2));
			if (isMining()) {
				miningCoolDown = 0;
			} else {
				miningCoolDown++;
			}
		}
		if (!getVehicle().getWorld().isRemote && liquidSensors != null) {
			byte data = sensorLight;
			if (isDrillSpinning()) {
				data |= 4;
			}
			liquidSensors.getInfoFromDrill(data);
			sensorLight = (byte) 1;
		}
	}

	protected void startDrill() {
		updateDw(IS_MINING, true);
	}

	protected void stopDrill() {
		updateDw(IS_MINING, false);
	}

	protected boolean isMining() {
		if (isPlaceholder()) {
			return getBooleanSimulationInfo();
		} else {
			return getDw(IS_MINING);
		}
	}

	protected boolean isDrillSpinning() {
		return isMining() || miningCoolDown < 10;
	}

	@Override
	public void initDw() {
		IS_MINING = createDw(DataSerializers.BOOLEAN);
		IS_ENABLED = createDw(DataSerializers.BOOLEAN);
		registerDw(IS_MINING, false);
		registerDw(IS_ENABLED, true);
	}

	@Override
	public int numberOfDataWatchers() {
		return 2;
	}

	private byte sensorLight = (byte) 1;
	private float drillRotation;
	private int miningCoolDown;

	public float getDrillRotation() {
		return drillRotation;
	}

	private boolean isDrillEnabled() {
		return getDw(IS_ENABLED);
	}

	public void setDrillEnabled(boolean val) {
		updateDw(IS_ENABLED, val);
	}

	@Override
	public void mouseClicked(GuiVehicle gui, int x, int y, int button) {
		if (button == 0) {
			if (inRect(x, y, TOGGLE_BOX_RECT)) {
				sendPacketToServer(getDataWriter());
			}
		}
	}

	@Override
	protected void receivePacket(DataReader dr, EntityPlayer player) {
		setDrillEnabled(!isDrillEnabled());
	}

	@Override
	public boolean hasSlots() {
		return false;
	}

	@Override
	public boolean hasGui() {
		return true;
	}

	@Override
	public void drawForeground(GuiVehicle gui) {
		drawString(gui, LocalizationCartTool.DRILL.translate(), 8, 6, 0x404040);
	}

	@Override
	public int guiWidth() {
		return 45;
	}

	@Override
	public int guiHeight() {
		return 40;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void drawBackground(GuiVehicle gui, int x, int y) {
		drawToggleBox(gui, "drill", isDrillEnabled(), x, y);
	}

	@Override
	public void drawMouseOver(GuiVehicle gui, int x, int y) {
		drawStringOnMouseOver(gui, getStateName(), x, y, TOGGLE_IMAGE_RECT);
	}

	private String getStateName() {
		return LocalizationCartTool.DRILL_TOGGLE.translate(isDrillEnabled() ? "1" : "0");
	}

	@Override
	protected void save(NBTTagCompound tagCompound) {
		super.save(tagCompound);
		tagCompound.setBoolean("DrillEnabled", isDrillEnabled());
	}

	@Override
	protected void load(NBTTagCompound tagCompound) {
		super.load(tagCompound);
		setDrillEnabled(tagCompound.getBoolean("DrillEnabled"));
	}

	@Override
	public void doActivate(int id) {
		setDrillEnabled(true);
	}

	@Override
	public void doDeActivate(int id) {
		setDrillEnabled(false);
	}

	@Override
	public boolean isActive(int id) {
		return isDrillEnabled();
	}
}