package vswe.stevescarts.modules.workers;

import net.minecraft.block.BlockFarmland;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidRegistry;
import vswe.stevescarts.entitys.EntityMinecartModular;
import vswe.stevescarts.modules.ModuleBase;
import vswe.stevescarts.modules.workers.tools.ModuleFarmer;

public class ModuleHydrater extends ModuleWorker {
	private int range;

	public ModuleHydrater(final EntityMinecartModular cart) {
		super(cart);
		this.range = 1;
	}

	@Override
	public byte getWorkPriority() {
		return 82;
	}

	@Override
	public void init() {
		super.init();
		for (final ModuleBase module : this.getCart().getModules()) {
			if (module instanceof ModuleFarmer) {
				this.range = ((ModuleFarmer) module).getExternalRange();
				break;
			}
		}
	}

	@Override
	public boolean work() {
		World world = getCart().worldObj;
		BlockPos next = this.getNextblock();
		for (int i = -this.range; i <= this.range; ++i) {
			for (int j = -this.range; j <= this.range; ++j) {
				if (this.hydrate(world, next.add(i, -1, j))) {
					return true;
				}
			}
		}
		return false;
	}

	private boolean hydrate(World world, BlockPos pos) {
		IBlockState state = world.getBlockState(pos);
		if (state.getBlock() == Blocks.FARMLAND){ 
			int moisture = state.getValue(BlockFarmland.MOISTURE);
			if(moisture != 7) {
				int waterCost = 7 - moisture;
				waterCost = this.getCart().drain(FluidRegistry.WATER, waterCost, false);
				if (waterCost > 0) {
					if (this.doPreWork()) {
						this.startWorking(2 + waterCost);
						return true;
					}
					this.stopWorking();
					this.getCart().drain(FluidRegistry.WATER, waterCost, true);
					world.setBlockState(pos, state.withProperty(BlockFarmland.MOISTURE, moisture + waterCost), 3);
				}
			}
		}
		return false;
	}
}
