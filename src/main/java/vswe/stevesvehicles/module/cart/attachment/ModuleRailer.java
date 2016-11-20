package vswe.stevesvehicles.module.cart.attachment;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRailBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import vswe.stevesvehicles.client.gui.assembler.SimulationInfo;
import vswe.stevesvehicles.client.gui.assembler.SimulationInfoInteger;
import vswe.stevesvehicles.client.gui.screen.GuiVehicle;
import vswe.stevesvehicles.container.slots.SlotBase;
import vswe.stevesvehicles.container.slots.SlotBuilder;
import vswe.stevesvehicles.localization.entry.block.LocalizationAssembler;
import vswe.stevesvehicles.localization.entry.module.cart.LocalizationCartRails;
import vswe.stevesvehicles.module.ISuppliesModule;
import vswe.stevesvehicles.module.cart.ModuleWorker;
import vswe.stevesvehicles.vehicle.VehicleBase;

public class ModuleRailer extends ModuleWorker implements ISuppliesModule {
	private DataParameter<Byte> RAILS;

	public ModuleRailer(VehicleBase vehicleBase) {
		super(vehicleBase);
	}

	@Override
	public void loadSimulationInfo(List<SimulationInfo> simulationInfo) {
		simulationInfo.add(new SimulationInfoInteger(LocalizationAssembler.INFO_RAILS, "rail", 0, getInventorySize(), 1));
	}

	@Override
	public boolean hasGui() {
		return true;
	}

	@Override
	protected SlotBase getSlot(int slotId, int x, int y) {
		return new SlotBuilder(getVehicle().getVehicleEntity(), slotId, 8 + x * 18, 23 + y * 18);
	}

	@Override
	public void drawForeground(GuiVehicle gui) {
		drawString(gui, LocalizationCartRails.TITLE.translate(), 8, 6, 0x404040);
	}

	// lower numbers are prioritized
	@Override
	public byte getWorkPriority() {
		return 100;
	}

	// return true when the work is done, false allow other modules to continue
	// the work
	@Override
	public boolean work() {
		World world = getVehicle().getWorld();
		// get the next block
		BlockPos next = getNextBlock();
		ArrayList<BlockPos> positions = getValidRailPositions(next);
		// if this cart hasn't started working
		if (doPreWork()) {
			// check if it's possible to place a rail, if so start the working
			// delay
			boolean valid = false;
			for (BlockPos position : positions) {
				if (tryPlaceTrack(world, position, false)) {
					valid = true;
					break;
				}
			}
			if (valid) {
				startWorking(12);
			} else {
				boolean front = false;
				for (BlockPos position : positions) {
					if (BlockRailBase.isRailBlock(world, position)) {
						front = true;
						break;
					}
				}
				if (!front) {
					turnback();
				}
			}
			return true;
		} else {
			// if the cart is working it's now time for it to place its rail,
			// try to find a spot for it
			stopWorking();
			for (BlockPos position : positions) {
				if (tryPlaceTrack(world, position, true)) {
					break;
				}
			}
			return false;
		}
	}

	protected ArrayList<BlockPos> getValidRailPositions(BlockPos pos) {
		ArrayList<BlockPos> lst = new ArrayList<>();
		if (pos.getY() >= getVehicle().y()) {
			lst.add(pos.up());
		}
		lst.add(pos);
		lst.add(pos.down());
		return lst;
	}

	protected boolean validRail(Item item) {
		return Block.getBlockFromItem(item) == Blocks.RAIL;
	}

	/**
	 * Code for placing a rail block, this method is used by placeTrack()
	 * 
	 * the flag parameter is telling if it should build anything, if false it
	 * will only test the posibillities.
	 **/
	private boolean tryPlaceTrack(World world, BlockPos pos, boolean flag) {
		// test if this block is free to use
		if (isValidForTrack(world, pos, true)) {
			// loop through the slots to search for rails
			for (int id = 0; id < getInventorySize(); id++) {
				// check if it has found a standard rail block
				if (getStack(id) != null) {
					if (validRail(getStack(id).getItem())) {
						// if so this is a valid action to do, if the flag
						// parameter is true this action should also be done
						if (flag) {
							// place the rail
							getVehicle().getWorld().setBlockState(pos, Block.getBlockFromItem(getStack(id).getItem()).getDefaultState());
							if (!getVehicle().hasCreativeSupplies()) {
								// remove the placed rail from the cart's
								// inventory
								getStack(id).shrink(1);
								// remove the stack if it's empty
								if (getStack(id).getCount() == 0) {
									setStack(id, null);
								}
								getVehicle().getVehicleEntity().markDirty();
							}
						}
						// return true to tell this went all right and that no
						// other blocks should be checked
						return true;
					}
				}
			}
			// if there's no rails left, return true so it won't test other
			// blocks(there won't be any rails no matter which block it tries to
			// build on). Also tell the cart to turn back.
			turnback();
			return true;
		}
		// if the block wasn't free to use return false so BuildTrack() will try
		// the next block(if any)
		return false;
	}

	@Override
	public void initDw() {
		RAILS = createDw(DataSerializers.BYTE);
		registerDw(RAILS, (byte) 0);
	}

	@Override
	public int numberOfDataWatchers() {
		return 1;
	}

	@Override
	public void onInventoryChanged() {
		super.onInventoryChanged();
		calculateRails();
	}

	private void calculateRails() {
		if (getVehicle().getWorld().isRemote) {
			return;
		}
		byte valid = 0;
		// loop through the slots to search for rails
		for (int i = 0; i < getInventorySize(); i++) {
			// check if it has found a standard rail block
			if (getStack(i) != null) {
				if (validRail(getStack(i).getItem())) {
					valid++;
				}
			}
		}
		updateDw(RAILS, valid);
	}

	public int getRails() {
		if (isPlaceholder()) {
			return getIntegerSimulationInfo();
		} else {
			return getDw(RAILS);
		}
	}

	private boolean hasGeneratedAngles = false;
	private float[] railAngles;

	public float getRailAngle(int i) {
		if (!hasGeneratedAngles) {
			railAngles = new float[getInventorySize()];
			for (int j = 0; j < getInventorySize(); j++) {
				railAngles[j] = getVehicle().getRandom().nextFloat() / 2 - 0.25F;
			}
			hasGeneratedAngles = true;
		}
		return railAngles[i];
	}

	@Override
	protected void load(NBTTagCompound tagCompound) {
		calculateRails();
	}

	@Override
	public boolean haveSupplies() {
		for (int i = 0; i < getInventorySize(); i++) {
			ItemStack item = getStack(i);
			if (item != null && validRail(item.getItem())) {
				return true;
			}
		}
		return false;
	}
}
