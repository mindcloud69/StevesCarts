package vswe.stevescarts.Modules.Workers;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFarmland;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fluids.FluidRegistry;
import vswe.stevescarts.Carts.MinecartModular;
import vswe.stevescarts.Modules.ModuleBase;
import vswe.stevescarts.Modules.Workers.Tools.ModuleFarmer;

public class ModuleHydrater extends ModuleWorker {
	private int range;

	public ModuleHydrater(final MinecartModular cart) {
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
		BlockPos next = this.getNextblock();
		for (int i = -this.range; i <= this.range; ++i) {
			for (int j = -this.range; j <= this.range; ++j) {
				if (this.hydrate(next.add(i, -1, j))) {
					return true;
				}
			}
		}
		return false;
	}

	private boolean hydrate(BlockPos pos) {
		IBlockState state = getCart().worldObj.getBlockState(pos);
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
					getCart().worldObj.setBlockState(pos, state.getBlock().getStateFromMeta(moisture + waterCost), 3);
				}
			}
		}
		return false;
	}
}
