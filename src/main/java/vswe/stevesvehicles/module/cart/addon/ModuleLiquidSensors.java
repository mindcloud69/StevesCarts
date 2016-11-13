package vswe.stevesvehicles.module.cart.addon;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.IFluidBlock;

import vswe.stevesvehicles.client.gui.assembler.SimulationInfo;
import vswe.stevesvehicles.client.gui.assembler.SimulationInfoInteger;
import vswe.stevesvehicles.localization.entry.block.LocalizationAssembler;
import vswe.stevesvehicles.module.ModuleBase;
import vswe.stevesvehicles.module.cart.attachment.ModuleLiquidDrainer;
import vswe.stevesvehicles.module.cart.tool.ModuleDrill;
import vswe.stevesvehicles.module.common.addon.ModuleAddon;
import vswe.stevesvehicles.vehicle.VehicleBase;

public class ModuleLiquidSensors extends ModuleAddon {
	private DataParameter<Byte> SENSOR_INFO;

	public ModuleLiquidSensors(VehicleBase vehicleBase) {
		super(vehicleBase);
	}

	@Override
	public void loadSimulationInfo(List<SimulationInfo> simulationInfo) {
		simulationInfo.add(new SimulationInfoInteger(LocalizationAssembler.INFO_LIQUID, "sensors", 1, 3, 1));
	}

	@Override
	public void update() {
		super.update();
		if (isDrillSpinning()) {
			sensorRotation += 0.05F * multiplier;
			if ((multiplier == 1 && sensorRotation > Math.PI / 4) || (multiplier == -1 && sensorRotation < -Math.PI / 4)) {
				multiplier *= -1;
			}
		} else {
			if (sensorRotation != 0) {
				if (sensorRotation > 0) {
					sensorRotation -= 0.05F;
					if (sensorRotation < 0) {
						sensorRotation = 0;
					}
				} else {
					sensorRotation += 0.05F;
					if (sensorRotation > 0) {
						sensorRotation = 0;
					}
				}
			}
			if (activateTime >= 0) {
				activateTime++;
				if (activateTime >= 10) {
					setLight(1);
					activateTime = -1;
				}
			}
		}
	}

	@Override
	public int numberOfDataWatchers() {
		return 1;
	}

	@Override
	public void initDw() {
		registerDw(SENSOR_INFO, (byte) 1);
	}

	private void activateLight(int light) {
		if (getLight() == 3 && light == 2) {
			return;
		}
		setLight(light);
		activateTime = 0;
	}

	// called from any drill, byte data will contain which light number and if
	// the drill is spinning or not
	public void getInfoFromDrill(byte data) {
		byte light = (byte) (data & 3);
		if (light != 1) {
			activateLight(light);
		}
		data &= ~3;
		data |= getLight();
		setSensorInfo(data);
	}

	private void setLight(int val) {
		if (isPlaceholder()) {
			return;
		}
		byte data = getDw(SENSOR_INFO);
		data &= ~3;
		data |= val;
		setSensorInfo(data);
	}

	private void setSensorInfo(byte val) {
		if (isPlaceholder()) {
			return;
		}
		updateDw(SENSOR_INFO, val);
	}

	private float sensorRotation;
	private int activateTime = -1;
	private int multiplier = 1;

	public int getLight() {
		if (isPlaceholder()) {
			return getIntegerSimulationInfo();
		} else {
			return getDw(SENSOR_INFO) & 3;
		}
	}

	protected boolean isDrillSpinning() {
		if (isPlaceholder()) {
			return getIntegerSimulationInfo() != 1;
		} else {
			return (getDw(SENSOR_INFO) & 4) != 0;
		}
	}

	public float getSensorRotation() {
		return sensorRotation;
	}

	// check if it's dangerous to remove a certain block(only used if a addon
	// allows the cart to use it)
	public boolean isDangerous(final ModuleDrill drill, BlockPos target, boolean isUp) {
		World world = getVehicle().getWorld();
		IBlockState state = world.getBlockState(target);
		Block block = state.getBlock();
		if (block == Blocks.LAVA) { // static lava
			handleLiquid(drill, target);
			return true;
		} else if (block == Blocks.WATER) { // static water
			handleLiquid(drill, target);
			return true;
		} else if (block != null && block instanceof IFluidBlock) { // static
			// other
			// //TODO is
			// this
			// really a
			// static
			// fluid
			handleLiquid(drill, target);
			return true;
		}
		// for moving there's different cases:
		// 1. the liquid is above, -> it will fall down -> not good
		// 2. the liquid is at the side -> might be alright(see below)
		// 2.1. the liquid is at the side but has flown so far that it can't
		// spread further -> nothing will happen -> it's alright
		// 2.2. the liquid is already falling and will therefore not spread ->
		// nothing will happen -> it's alright
		// 2.2.E exception: if there's a block below it will spread anyways ->
		// not good
		// Ignore the 2.3 ones, no liquid in the tunnel, it's easier to code and
		// more convenient for the user
		// 2.3. the liquid is at the side but has flown so far that it can only
		// spread ONE block more -> might be alright(see below)
		// 2.3.1. the liquid is at the bottom of the tunnel -> it will spread
		// one block -> the cart is too far away -> it's alright
		// 2.3.2. the liquid is not at the bottom -> it will spread one block
		// and then fall -> it will flow to the bottom and start to spread ->
		// the cart will be in the way -> not good
		// 2.4. none of the above -> the liquid will flow and destroy the cart
		// -> not good
		// 3. when the block is removed sand or gravel will fall down -> liquid
		// on top of this will fall down -> the liquid will hit the cart -> not
		// good (this is very difficult to detect(maybe not :P))
		boolean isWater = block == Blocks.WATER || block == Blocks.FLOWING_WATER || block == Blocks.ICE /* ice */;
		boolean isLava = block == Blocks.LAVA || block == Blocks.FLOWING_LAVA;
		// boolean isOther = block != null && block instanceof IFluidBlock;
		boolean isLiquid = isWater || isLava; // || isOther;
		if (isLiquid && block != null) {
			// check for cases 1. and 2.
			if (isUp) {
				handleLiquid(drill, target);
				return true; // case 1.
			} else {
				int level = state.getValue(BlockLiquid.LEVEL);
				if ((level & 8) == 8) {
					if (block.isBlockSolid(getVehicle().getWorld(), target.down(), EnumFacing.UP)) {
						handleLiquid(drill, target);
						return true; // case 2.2.E.
					} else {
						return false; // case 2.2.
					}
				} else if (isWater && ((level & 7) == 7)) {
					return false; // case 2.1.
				} else if (isLava && ((level & 7) == 7) && !getVehicle().getWorld().provider.isSurfaceWorld()) {
					return false; // case 2.1.
				} else if (isLava && ((level & 7) == 6)) {
					return false; // case 2.1.
				}
				// TODO make a more advanced version of this, fluids are so more
				// advanced than liquids
				/*
				 * else if (isOther && ((m & 7) ==
				 * ((IFluidBlock)block).getFlowDistance())) { return false;
				 * //case 2.1. }
				 */
				else {
					handleLiquid(drill, target);
					return true; // case 2.4
				}
			}
		} else {
			// check for case 3
			if (isUp) {
				// sand or gravel
				boolean isFalling = block instanceof BlockFalling;
				if (isFalling) {
					return isDangerous(drill, target.up(), true) || isDangerous(drill, target.east(), false) || isDangerous(drill, target.west(), false) || isDangerous(drill, target.south(), false) || isDangerous(drill, target.north(), false);
				}
			}
		}
		return false;
	}

	private void handleLiquid(ModuleDrill drill, BlockPos traget) {
		ModuleLiquidDrainer liquidDrainer = null;
		for (ModuleBase module : getVehicle().getModules()) {
			if (module instanceof ModuleLiquidDrainer) {
				liquidDrainer = (ModuleLiquidDrainer) module;
				break;
			}
		}
		if (liquidDrainer != null) {
			liquidDrainer.handleLiquid(drill, traget);
		}
	}
}