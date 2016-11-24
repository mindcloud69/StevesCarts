package stevesvehicles.common.modules.cart.tool;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import stevesvehicles.client.gui.assembler.SimulationInfo;
import stevesvehicles.client.gui.assembler.SimulationInfoBoolean;
import stevesvehicles.client.gui.screen.GuiVehicle;
import stevesvehicles.client.localization.entry.block.LocalizationAssembler;
import stevesvehicles.client.localization.entry.module.cart.LocalizationCartTool;
import stevesvehicles.common.container.slots.SlotBase;
import stevesvehicles.common.container.slots.SlotFuel;
import stevesvehicles.common.container.slots.SlotSapling;
import stevesvehicles.common.modules.ISuppliesModule;
import stevesvehicles.common.modules.ModuleBase;
import stevesvehicles.common.modules.cart.ITreeModule;
import stevesvehicles.common.modules.cart.addon.cultivation.ModulePlantSize;
import stevesvehicles.common.utils.BlockPosUtils;
import stevesvehicles.common.vehicles.VehicleBase;

public abstract class ModuleWoodcutter extends ModuleTool implements ISuppliesModule, ITreeModule {
	private DataParameter<Boolean> IS_CUTTING;

	public ModuleWoodcutter(VehicleBase vehicleBase) {
		super(vehicleBase);
	}

	@Override
	public void loadSimulationInfo(List<SimulationInfo> simulationInfo) {
		simulationInfo.add(new SimulationInfoBoolean(LocalizationAssembler.INFO_CUTTING, "wood"));
	}

	// lower numbers are prioritized
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
		drawString(gui, LocalizationCartTool.CUTTER.translate(), 8, 6, 0x404040);
	}

	@Override
	protected SlotBase getSlot(int slotId, int x, int y) {
		return new SlotSapling(getVehicle().getVehicleEntity(), this, slotId, 8 + x * 18, 28 + y * 18);
	}

	@Override
	protected int getInventoryWidth() {
		return 3;
	}

	private ArrayList<ITreeModule> treeModules;
	private ModulePlantSize plantSize;

	@Override
	public void init() {
		super.init();
		treeModules = new ArrayList<>();
		for (ModuleBase module : getVehicle().getModules()) {
			if (module instanceof ITreeModule) {
				treeModules.add((ITreeModule) module);
			} else if (module instanceof ModulePlantSize) {
				plantSize = (ModulePlantSize) module;
			}
		}
	}

	public abstract int getPercentageDropChance();

	public ArrayList<ItemStack> getTierDrop(List<ItemStack> baseItems) {
		ArrayList<ItemStack> items = new ArrayList<>();
		for (ItemStack item : baseItems) {
			if (item != null) {
				dropItemByMultiplierChance(items, item, getPercentageDropChance());
			}
		}
		return items;
	}

	private void dropItemByMultiplierChance(ArrayList<ItemStack> items, ItemStack item, int percentage) {
		while (percentage > 0) {
			if (getVehicle().getRandom().nextInt(100) < percentage) {
				items.add(item.copy());
			}
			percentage -= 100;
		}
	}

	private boolean isPlanting;

	// return true when the work is done, false allow other modules to continue
	// the work
	@Override
	public boolean work() {
		World world = getVehicle().getWorld();
		// get the next block so the cart knows where to mine
		BlockPos next = getNextBlock();
		// loop through the blocks in the "hole" in front of the cart
		int size = getPlantSize();
		destroyLeaveBlockOnTrack(world, next);
		destroyLeaveBlockOnTrack(world, next.up());
		for (int i = -size; i <= size; i++) {
			if (i == 0) {
				continue;
			}
			// plant big trees in the correct order
			int j = i;
			if (j < 0) {
				j = -size - j - 1;
			}
			BlockPos plant = next.add(getVehicle().z() != next.getZ() ? j : 0, -1, getVehicle().x() != next.getX() ? j : 0);
			if (plant(size, world, plant, next.getX(), next.getZ())) {
				setCutting(false);
				return true;
			}
		}
		if (!isPlanting) {
			for (int i = -1; i <= 1; i++) {
				for (int j = -1; j <= 1; j++) {
					BlockPos farm = next.add(i, -1, j);
					if (farm(world, farm)) {
						setCutting(true);
						return true;
					}
				}
			}
		}
		isPlanting = false;
		setCutting(false);
		return false;
	}

	private boolean plant(int size, World world, BlockPos pos, int cx, int cz) {
		if ((pos.getX() == cx && ((pos.getX() / size) % 2 == 0)) || (pos.getZ() == cz && (pos.getZ() / size) % 2 == 0)) {
			return false;
		}
		int saplingSlotId = -1;
		ItemStack sapling = null;
		for (int i = 0; i < getInventorySize(); i++) {
			SlotBase slot = getSlots().get(i);
			if (slot.containsValidItem()) {
				saplingSlotId = i;
				sapling = getStack(i);
				break;
			}
		}
		if (sapling != null) {
			if (doPreWork()) {
				if (sapling.getItem().onItemUse(getFakePlayer(), world, pos, EnumHand.MAIN_HAND, EnumFacing.UP, 0, 0, 0) == EnumActionResult.SUCCESS) {
					if (sapling.getCount() == 0) {
						setStack(saplingSlotId, null);
					}
					startWorking(25);
					isPlanting = true;
					return true;
				}
			} else {
				stopWorking();
				isPlanting = false;
			}
		}
		return false;
	}

	private boolean farm(World world, BlockPos pos) {
		pos = pos.up();
		IBlockState state = world.getBlockState(pos);
		if (isWoodHandler(world, state, pos)) {
			ArrayList<BlockPos> checked = new ArrayList<>();
			if (removeAt(world, pos, checked)) {
				return true;
			} else {
				stopWorking();
			}
		}
		return false;
	}

	private boolean removeAt(World world, BlockPos here, ArrayList<BlockPos> checked) {
		checked.add(here);
		IBlockState state = world.getBlockState(here);
		if (checked.size() < 125 && BlockPosUtils.getHorizontalDistToVehicleSquared(here, getVehicle()) < 175) {
			for (int type = 0; type < 2; type++) {
				boolean hitWood = false;
				if (isLeavesHandler(world, state, here)) {
					type = 1;
				} else if (type == 1) {
					hitWood = true;
				}
				for (int offsetX = -1; offsetX <= 1; offsetX++) {
					for (int offsetY = 1; offsetY >= 0; offsetY--) {
						for (int offsetZ = -1; offsetZ <= 1; offsetZ++) {
							BlockPos target = here.add(offsetX, offsetY, offsetZ);
							IBlockState currentState = world.getBlockState(target);
							if (hitWood ? isWoodHandler(world, currentState, target) : isLeavesHandler(world, currentState, target)) {
								if (!checked.contains(target)) {
									return removeAt(world, target, checked);
								}
							}
						}
					}
				}
			}
		}
		List<ItemStack> stuff;
		if (shouldSilkTouch(state, here)) {
			stuff = new ArrayList<>();
			ItemStack stack = getSilkTouchedItem(state);
			if (stack != null) {
				stuff.add(stack);
			}
		} else {
			int fortune = enchanter != null ? enchanter.getFortuneLevel() : 0;
			stuff = state.getBlock().getDrops(world, here, state, fortune);
		}
		stuff = getTierDrop(stuff);
		boolean first = true;
		for (ItemStack item : stuff) {
			getVehicle().addItemToChest(item, Slot.class, SlotFuel.class);
			if (item.getCount() != 0) {
				if (first) {
					return false;
				}
				EntityItem entityitem = new EntityItem(world, getVehicle().getEntity().posX, getVehicle().getEntity().posY, getVehicle().getEntity().posZ, item);
				entityitem.motionX = (float) (here.getX() - getVehicle().x()) / 10;
				entityitem.motionY = 0.15F;
				entityitem.motionZ = (float) (here.getZ() - getVehicle().z()) / 10;
				getVehicle().getWorld().spawnEntity(entityitem);
			}
			first = false;
		}
		getVehicle().getWorld().setBlockToAir(here);
		int baseTime;
		if (isLeavesHandler(world, state, here)) {
			baseTime = 2;
			damageTool(1);
		} else {
			baseTime = 25;
			damageTool(5);
		}
		int efficiency = enchanter != null ? enchanter.getEfficiencyLevel() : 0;
		startWorking((int) (baseTime / Math.pow(1.3F, efficiency)));
		return true;
	}

	@Override
	public void initDw() {
		IS_CUTTING = createDw(DataSerializers.BOOLEAN);
		registerDw(IS_CUTTING, false);
	}

	@Override
	public int numberOfDataWatchers() {
		return 1;
	}

	private void setCutting(boolean val) {
		updateDw(IS_CUTTING, val);
	}

	protected boolean isCutting() {
		if (isPlaceholder()) {
			return getBooleanSimulationInfo();
		} else {
			return getDw(IS_CUTTING);
		}
	}

	private float cutterAngle = (float) (Math.PI / 4);

	public float getCutterAngle() {
		return cutterAngle;
	}

	/**
	 * Called every tick, here the necessary actions should be taken
	 **/
	@Override
	public void update() {
		// call the method from the super class, this will do all ordinary
		// things first
		super.update();
		boolean cuttingflag = isCutting();
		if (cuttingflag || cutterAngle != (float) (Math.PI / 4)) {
			boolean flag = false;
			if (!cuttingflag && cutterAngle < (float) (Math.PI / 4)) {
				flag = true;
			}
			cutterAngle = (float) ((cutterAngle + 0.9F) % (Math.PI * 2));
			if (!cuttingflag && cutterAngle > (float) (Math.PI / 4) && flag) {
				cutterAngle = (float) (Math.PI / 4);
			}
		}
	}

	@Override
	public boolean haveSupplies() {
		for (int i = 0; i < getInventorySize(); i++) {
			if (getSlots().get(i).containsValidItem()) {
				return true;
			}
		}
		return false;
	}

	public boolean isLeavesHandler(World world, IBlockState state, BlockPos pos) {
		for (ITreeModule module : treeModules) {
			if (module.isLeaves(world, state, pos)) {
				return true;
			}
		}
		return false;
	}

	public boolean isWoodHandler(World world, IBlockState state, BlockPos pos) {
		for (ITreeModule module : treeModules) {
			if (module.isWood(world, state, pos)) {
				return true;
			}
		}
		return false;
	}

	public boolean isSaplingHandler(ItemStack sapling) {
		for (ITreeModule module : treeModules) {
			if (module.isSapling(sapling)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean isLeaves(World world, IBlockState state, BlockPos pos) {
		return state.getBlock() == Blocks.LEAVES || state.getBlock() == Blocks.LEAVES2;
	}

	@Override
	public boolean isWood(World world, IBlockState state, BlockPos pos) {
		return state.getBlock() == Blocks.LOG || state.getBlock() == Blocks.LOG2;
	}

	@Override
	public boolean isSapling(ItemStack sapling) {
		return sapling != null && Block.getBlockFromItem(sapling.getItem()) == Blocks.SAPLING;
	}

	private int getPlantSize() {
		if (plantSize != null) {
			return plantSize.getSize();
		} else {
			return 1;
		}
	}

	private void destroyLeaveBlockOnTrack(World world, BlockPos pos) {
		IBlockState state = world.getBlockState(pos);
		if (state != null && isLeavesHandler(world, state, pos)) {
			world.setBlockToAir(pos);
		}
	}
}
