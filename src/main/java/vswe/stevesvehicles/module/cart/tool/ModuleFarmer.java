package vswe.stevesvehicles.module.cart.tool;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;
import vswe.stevesvehicles.client.gui.assembler.SimulationInfo;
import vswe.stevesvehicles.client.gui.assembler.SimulationInfoBoolean;
import vswe.stevesvehicles.client.gui.screen.GuiVehicle;
import vswe.stevesvehicles.container.slots.SlotBase;
import vswe.stevesvehicles.container.slots.SlotSeed;
import vswe.stevesvehicles.localization.entry.block.LocalizationAssembler;
import vswe.stevesvehicles.localization.entry.module.cart.LocalizationCartTool;
import vswe.stevesvehicles.module.ISuppliesModule;
import vswe.stevesvehicles.module.ModuleBase;
import vswe.stevesvehicles.module.cart.ICropModule;
import vswe.stevesvehicles.vehicle.VehicleBase;

public abstract class ModuleFarmer extends ModuleTool implements ISuppliesModule, ICropModule {
	private DataParameter<Boolean> IS_FARMING;
	public ModuleFarmer(VehicleBase vehicleBase) {
		super(vehicleBase);
	}

	@Override
	public void loadSimulationInfo(List<SimulationInfo> simulationInfo) {
		simulationInfo.add(new SimulationInfoBoolean(LocalizationAssembler.INFO_FARM, "farming"));
	}

	protected abstract int getRange();

	public int getExternalRange() {
		return getRange();
	}

	private ArrayList<ICropModule> plantModules;

	@Override
	public void init() {
		super.init();
		plantModules = new ArrayList<>();
		for (ModuleBase module : getVehicle().getModules()) {
			if (module instanceof ICropModule) {
				plantModules.add((ICropModule) module);
			}
		}
	}

	// lower numbers are prioritised
	@Override
	public byte getWorkPriority() {
		return 80;
	}

	@Override
	public boolean hasGui() {
		return true;
	}

	@Override
	public void drawForeground(GuiVehicle gui) {
		drawString(gui, LocalizationCartTool.FARMER.translate(), 8, 6, 0x404040);
	}

	@Override
	protected int getInventoryWidth() {
		return 3;
	}

	@Override
	protected SlotBase getSlot(int slotId, int x, int y) {
		return new SlotSeed(getVehicle().getVehicleEntity(), this, slotId, 8 + x * 18, 28 + y * 18);
	}

	// return true when the work is done, false allow other modules to continue
	// the work
	@Override
	public boolean work() {
		// get the next block so the cart knows where to mine
		World world = getVehicle().getWorld();
		BlockPos next = getNextBlock();
		// save thee coordinates for easy access
		// loop through the blocks in the "hole" in front of the cart
		for (int i = -getRange(); i <= getRange(); i++) {
			for (int j = -getRange(); j <= getRange(); j++) {
				// calculate the coordinates of this "hole"
				BlockPos target = next.add(i, -1, j);
				if (farm(world, target) || till(world, target) || plant(world, target)) {
					return true;
				}
			}
		}
		return false;
	}

	protected boolean till(World world, BlockPos pos) {
		IBlockState soilState = world.getBlockState(pos);
		if (world.isAirBlock(pos.up()) && (soilState.getBlock() == Blocks.GRASS || soilState.getBlock() == Blocks.DIRT)) {
			if (doPreWork()) {
				startWorking(10);
				return true;
			} else {
				stopWorking();
				world.setBlockState(pos, Blocks.FARMLAND.getDefaultState());
			}
		}
		return false;
	}

	protected boolean plant(World world, BlockPos pos) {
		int seedSlot = -1;
		IBlockState soil = world.getBlockState(pos);
		if (soil != null && soil != Blocks.AIR) {
			// check if there's any seeds to place
			for (int id = 0; id < getInventorySize(); id++) {
				// check if the slot contains seeds
				if (getStack(id) != null) {
					if (isSeedValidHandler(getStack(id))) {
						IBlockState crop = getCropFromSeedHandler(getStack(id));
						if (crop != null && crop instanceof IPlantable && world.isAirBlock(pos.up()) && soil.getBlock().canSustainPlant(soil, world, pos, EnumFacing.UP, ((IPlantable) crop))) {
							seedSlot = id;
							break;
						}
					}
				}
			}
			if (seedSlot != -1) {
				if (doPreWork()) {
					startWorking(25);
					return true;
				} else {
					stopWorking();
					IBlockState crop = getCropFromSeedHandler(getStack(seedSlot));
					world.setBlockState(pos.up(), crop);
					if (!getVehicle().hasCreativeSupplies()) {
						getStack(seedSlot).stackSize--;
						if (getStack(seedSlot).stackSize <= 0) {
							setStack(seedSlot, null);
						}
					}
				}
			}
		}
		return false;
	}

	protected boolean farm(World world, BlockPos pos) {
		pos = pos.up();
		IBlockState state = world.getBlockState(pos);
		if (isReadyToHarvestHandler(world, state, pos)) {
			if (doPreWork()) {
				int efficiency = enchanter != null ? enchanter.getEfficiencyLevel() : 0;
				int workingTime = (int) (getBaseFarmingTime() / Math.pow(1.3F, efficiency));
				setFarming(workingTime * 4);
				startWorking(workingTime);
				return true;
			} else {
				stopWorking();
				List<ItemStack> stuff;
				if (shouldSilkTouch(state, pos)) {
					stuff = new ArrayList<>();
					ItemStack stack = getSilkTouchedItem(state);
					if (stack != null) {
						stuff.add(stack);
					}
				} else {
					int fortune = enchanter != null ? enchanter.getFortuneLevel() : 0;
					stuff = state.getBlock().getDrops(getVehicle().getWorld(), pos, state, fortune);
				}
				for (ItemStack item : stuff) {
					getVehicle().addItemToChest(item);
					if (item.stackSize != 0) {
						EntityItem entityitem = new EntityItem(getVehicle().getWorld(), getVehicle().getEntity().posX, getVehicle().getEntity().posY, getVehicle().getEntity().posZ, item);
						entityitem.motionX = (float) (pos.getX() - getVehicle().x()) / 10;
						entityitem.motionY = 0.15F;
						entityitem.motionZ = (float) (pos.getZ() - getVehicle().z()) / 10;
						getVehicle().getWorld().spawnEntityInWorld(entityitem);
					}
				}
				getVehicle().getWorld().setBlockToAir(pos);
				damageTool(3);
			}
		}
		return false;
	}

	protected int getBaseFarmingTime() {
		return 25;
	}

	public boolean isSeedValidHandler(ItemStack seed) {
		for (ICropModule module : plantModules) {
			if (module.isSeedValid(seed)) {
				return true;
			}
		}
		return false;
	}

	protected IBlockState getCropFromSeedHandler(ItemStack seed) {
		for (ICropModule module : plantModules) {
			if (module.isSeedValid(seed)) {
				return module.getCropFromSeed(seed);
			}
		}
		return null;
	}

	protected boolean isReadyToHarvestHandler(World world, IBlockState state, BlockPos pos) {
		for (ICropModule module : plantModules) {
			if (module.isReadyToHarvest(world, state, pos)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean isSeedValid(ItemStack seed) {
		return seed.getItem() == Items.WHEAT_SEEDS || seed.getItem() == Items.POTATO || seed.getItem() == Items.CARROT;
	}

	@Override
	public IBlockState getCropFromSeed(ItemStack seed) {
		if (seed.getItem() == Items.CARROT) {
			return Blocks.CARROTS.getDefaultState();
		} else if (seed.getItem() == Items.POTATO) {
			return Blocks.POTATOES.getDefaultState();
		} else if (seed.getItem() == Items.WHEAT_SEEDS) {
			return Blocks.WHEAT.getDefaultState();
		}
		return null;
	}

	@Override
	public boolean isReadyToHarvest(World world, IBlockState state, BlockPos pos) {
		return state.getBlock() instanceof BlockCrops && state.getValue(BlockCrops.AGE) == 7;
	}

	private int farming;
	private float farmAngle;
	private float rigAngle = -(float) Math.PI * 5 / 4;

	public float getFarmAngle() {
		return farmAngle;
	}

	public float getRigAngle() {
		return rigAngle;
	}

	@Override
	public void initDw() {
		IS_FARMING = createDw(DataSerializers.BOOLEAN);
		registerDw(IS_FARMING, false);
	}

	@Override
	public int numberOfDataWatchers() {
		return 1;
	}

	private void setFarming(int val) {
		farming = val;
		updateDw(IS_FARMING, val > 0);
	}

	protected boolean isFarming() {
		if (isPlaceholder()) {
			return getBooleanSimulationInfo();
		} else {
			return getVehicle().isEngineBurning() && getDw(IS_FARMING);
		}
	}

	/**
	 * Called every tick, here the necessary actions should be taken
	 **/
	@Override
	public void update() {
		// call the method from the super class, this will do all ordinary
		// things first
		super.update();
		if (!getVehicle().getWorld().isRemote) {
			setFarming(farming - 1);
		} else {
			float up = -(float) Math.PI * 5 / 4;
			float down = -(float) Math.PI;
			boolean flag = isFarming();
			if (flag) {
				if (rigAngle < down) {
					rigAngle += 0.1F;
					if (rigAngle > down) {
						rigAngle = down;
					}
				} else {
					farmAngle = (float) ((farmAngle + 0.15F) % (Math.PI * 2));
				}
			} else {
				if (rigAngle > up) {
					rigAngle -= 0.075F;
					if (rigAngle < up) {
						rigAngle = up;
					}
				}
			}
		}
	}

	@Override
	public boolean haveSupplies() {
		for (int i = 0; i < getInventorySize(); i++) {
			ItemStack item = getStack(i);
			if (item != null && isSeedValidHandler(item)) {
				return true;
			}
		}
		return false;
	}
}