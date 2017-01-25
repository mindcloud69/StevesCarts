package vswe.stevescarts.modules.addons;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fluids.IFluidBlock;
import vswe.stevescarts.entitys.EntityMinecartModular;
import vswe.stevescarts.modules.ModuleBase;
import vswe.stevescarts.modules.workers.ModuleLiquidDrainer;
import vswe.stevescarts.modules.workers.tools.ModuleDrill;

public class ModuleLiquidSensors extends ModuleAddon {
	private float sensorRotation;
	private int activetime;
	private int mult;
	private DataParameter<Byte> SENSOR_INFO;

	public ModuleLiquidSensors(final EntityMinecartModular cart) {
		super(cart);
		this.activetime = -1;
		this.mult = 1;
	}

	@Override
	public void update() {
		super.update();
		if (this.isDrillSpinning()) {
			this.sensorRotation += 0.05f * this.mult;
			if ((this.mult == 1 && this.sensorRotation > 0.7853981633974483) || (this.mult == -1 && this.sensorRotation < -0.7853981633974483)) {
				this.mult *= -1;
			}
		} else {
			if (this.sensorRotation != 0.0f) {
				if (this.sensorRotation > 0.0f) {
					this.sensorRotation -= 0.05f;
					if (this.sensorRotation < 0.0f) {
						this.sensorRotation = 0.0f;
					}
				} else {
					this.sensorRotation += 0.05f;
					if (this.sensorRotation > 0.0f) {
						this.sensorRotation = 0.0f;
					}
				}
			}
			if (this.activetime >= 0) {
				++this.activetime;
				if (this.activetime >= 10) {
					this.setLight(1);
					this.activetime = -1;
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
		SENSOR_INFO = createDw(DataSerializers.BYTE);
		registerDw(SENSOR_INFO, (byte)1);
	}

	private void activateLight(final int light) {
		if (this.getLight() == 3 && light == 2) {
			return;
		}
		this.setLight(light);
		this.activetime = 0;
	}

	public void getInfoFromDrill(byte data) {
		final byte light = (byte) (data & 0x3);
		if (light != 1) {
			this.activateLight(light);
		}
		data &= 0xFFFFFFFC;
		data |= (byte) this.getLight();
		this.setSensorInfo(data);
	}

	private void setLight(final int val) {
		if (this.isPlaceholder()) {
			return;
		}
		byte data = this.getDw(SENSOR_INFO);
		data &= 0xFFFFFFFC;
		data |= (byte) val;
		this.setSensorInfo(data);
	}

	private void setSensorInfo(byte val) {
		if (this.isPlaceholder()) {
			return;
		}
		registerDw(SENSOR_INFO, val);
	}

	public int getLight() {
		if (this.isPlaceholder()) {
			return this.getSimInfo().getLiquidLight();
		}
		return this.getDw(SENSOR_INFO) & 0x3;
	}

	protected boolean isDrillSpinning() {
		if (this.isPlaceholder()) {
			return this.getSimInfo().getDrillSpinning();
		}
		return (this.getDw(SENSOR_INFO) & 0x4) != 0x0;
	}

	public float getSensorRotation() {
		return this.sensorRotation;
	}

	public boolean isDangerous(final ModuleDrill drill, BlockPos pos, boolean isUp) {
		final Block block = this.getCart().world.getBlockState(pos).getBlock();
		if (block == Blocks.LAVA) {
			this.handleLiquid(drill, pos);
			return true;
		}
		if (block == Blocks.WATER) {
			this.handleLiquid(drill, pos);
			return true;
		}
		if (block != null && block instanceof IFluidBlock) {
			this.handleLiquid(drill, pos);
			return true;
		}
		final boolean isWater = block == Blocks.WATER || block == Blocks.FLOWING_WATER || block == Blocks.ICE;
		final boolean isLava = block == Blocks.LAVA || block == Blocks.FLOWING_LAVA;
		final boolean isOther = block != null && block instanceof IFluidBlock;
		final boolean isLiquid = isWater || isLava || isOther;
		if (!isLiquid) {
			if (isUp) {
				final boolean isFalling = block instanceof BlockFalling;
				if (isFalling) {
					return this.isDangerous(drill, pos.add(0, 1, 0), true) || this.isDangerous(drill, pos.add(1, 0, 0), false) || this.isDangerous(drill, pos.add(-1, 0, 0), false) || this.isDangerous(drill, pos.add(0, 0, 1), false) || this.isDangerous(drill, pos.add(0, 0, -1), false);
				}
			}
			return false;
		}
		if (isUp) {
			this.handleLiquid(drill, pos);
			return true;
		}
		IBlockState state = getCart().world.getBlockState(pos);
		int m = state.getBlock().getMetaFromState(state);
		if ((m & 0x8) == 0x8) {
			if (block.isBlockSolid(this.getCart().world, pos.down(), EnumFacing.UP)) {
				this.handleLiquid(drill, pos);
				return true;
			}
			return false;
		} else {
			if (isWater && (m & 0x7) == 0x7) {
				return false;
			}
			if (isLava && (m & 0x7) == 0x7 && !this.getCart().world.provider.isSkyColored()) {
				return false;
			}
			if (isLava && (m & 0x7) == 0x6) {
				return false;
			}
			this.handleLiquid(drill, pos);
			return true;
		}
	}

	private void handleLiquid(final ModuleDrill drill, BlockPos pos) {
		ModuleLiquidDrainer liquiddrainer = null;
		for (final ModuleBase module : this.getCart().getModules()) {
			if (module instanceof ModuleLiquidDrainer) {
				liquiddrainer = (ModuleLiquidDrainer) module;
				break;
			}
		}
		if (liquiddrainer != null) {
			liquiddrainer.handleLiquid(drill, pos);
		}
	}
}
