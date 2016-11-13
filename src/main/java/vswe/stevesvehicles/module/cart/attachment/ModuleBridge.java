package vswe.stevesvehicles.module.cart.attachment;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.BlockRailBase;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import vswe.stevesvehicles.client.gui.assembler.SimulationInfo;
import vswe.stevesvehicles.client.gui.assembler.SimulationInfoBoolean;
import vswe.stevesvehicles.client.gui.screen.GuiVehicle;
import vswe.stevesvehicles.container.slots.SlotBase;
import vswe.stevesvehicles.container.slots.SlotBridge;
import vswe.stevesvehicles.localization.entry.block.LocalizationAssembler;
import vswe.stevesvehicles.module.ISuppliesModule;
import vswe.stevesvehicles.module.cart.ModuleWorker;
import vswe.stevesvehicles.vehicle.VehicleBase;

public class ModuleBridge extends ModuleWorker implements ISuppliesModule {
	private DataParameter<Boolean> BRIDGE;
	public ModuleBridge(VehicleBase vehicleBase) {
		super(vehicleBase);
	}

	@Override
	public void loadSimulationInfo(List<SimulationInfo> simulationInfo) {
		simulationInfo.add(new SimulationInfoBoolean(LocalizationAssembler.INFO_BRIDGE, "bridge"));
	}

	@Override
	public boolean hasGui(){
		return true;
	}

	@Override
	public int guiWidth() {
		return 80;
	}

	@Override
	protected SlotBase getSlot(int slotId, int x, int y) {
		return new SlotBridge(getVehicle().getVehicleEntity(), slotId, 8 + x * 18, 23 + y * 18);
	}
	@Override
	public void drawForeground(GuiVehicle gui) {
		drawString(gui, getModuleName(), 8, 6, 0x404040);
	}

	//lower numbers are prioritized
	@Override
	public byte getWorkPriority() {
		return 98;
	}

	//return true when the work is done, false allow other modules to continue the work
	@Override
	public boolean work() {
		//get the next block
		BlockPos next = getNextBlock();
		BlockPos target;
		int yLocation;

		if (getModularCart().getYTarget() > next.getY()) {
			target = new BlockPos(next);
		}else if (getModularCart().getYTarget() < next.getY()) {
			target = next.down(2);
		}else {
			target = next.down();
		}

		if (!BlockRailBase.isRailBlock(getVehicle().getWorld(), next) && !BlockRailBase.isRailBlock(getVehicle().getWorld(), next.down())) {
			if (doPreWork()) {
				if (tryBuildBridge(target, false)) {
					startWorking(22);
					setBridge(true);
					return true;
				}
			}else {
				if (tryBuildBridge(target, true)) {
					stopWorking();
				}
			}
		}

		setBridge(false);
		return false;
	}

	private boolean tryBuildBridge(BlockPos target, boolean flag) {
		World world = getVehicle().getWorld();
		IBlockState blockState = world.getBlockState(target);
		
		if ((countsAsAir(target) || blockState.getBlock() instanceof BlockLiquid) && isValidForTrack(target.up(), false)) {

			for (int m = 0; m < getInventorySize(); m++) {
				if (getStack(m) != null) {

					if (SlotBridge.isBridgeMaterial(getStack(m))) {
						if (flag) {

							ItemStack stack = getStack(m);
							world.setBlockState(target, Block.getBlockFromItem(stack.getItem()).getStateFromMeta(stack.getItem().getMetadata(stack.getItemDamage())), 3);

							if (!getVehicle().hasCreativeSupplies()) {
								getStack(m).stackSize--;
								if (getStack(m).stackSize == 0)
								{
									setStack(m,null);
								}

								getVehicle().getVehicleEntity().markDirty();
							}
						}

						return true;
					}
				}
			}

			if (!isValidForTrack(target, true) && !isValidForTrack(target.up(), true) && !isValidForTrack(target.up(2), true)) {
				//turnback();
			}
		}

		return false;
	}

	@Override
	public void initDw() {
		BRIDGE = createDw(DataSerializers.BOOLEAN);
		registerDw(BRIDGE, false);
	}
	@Override
	public int numberOfDataWatchers() {
		return 1;
	}

	private void setBridge(boolean val) {
		updateDw(BRIDGE, val);
	}

	public boolean needBridge() {
		if (isPlaceholder()) {
			return getBooleanSimulationInfo();
		}else{
			return getDw(BRIDGE);
		}
	}

	@Override
	public boolean haveSupplies() {
		for (int i = 0; i < getInventorySize(); i++) {
			ItemStack item = getStack(i);
			if (item != null && SlotBridge.isBridgeMaterial(item)) {
				return true;
			}
		}
		return false;
	}	
}