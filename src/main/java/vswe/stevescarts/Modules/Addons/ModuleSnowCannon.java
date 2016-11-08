package vswe.stevescarts.Modules.Addons;

import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import vswe.stevescarts.Carts.MinecartModular;

public class ModuleSnowCannon extends ModuleAddon {
	private int tick;

	public ModuleSnowCannon(final MinecartModular cart) {
		super(cart);
	}

	@Override
	public void update() {
		super.update();
		if (this.getCart().worldObj.isRemote) {
			return;
		}
		if (this.getCart().hasFuel()) {
			if (this.tick >= this.getInterval()) {
				this.tick = 0;
				this.generateSnow();
			} else {
				++this.tick;
			}
		}
	}

	protected int getInterval() {
		return 70;
	}

	protected int getBlocksOnSide() {
		return 7;
	}

	protected int getBlocksFromLevel() {
		return 1;
	}

	private void generateSnow() {
		BlockPos cartPos = getCart().getPosition();
		for (int x = -this.getBlocksOnSide(); x <= this.getBlocksOnSide(); ++x) {
			for (int z = -this.getBlocksOnSide(); z <= this.getBlocksOnSide(); ++z) {
				for (int y = -this.getBlocksFromLevel(); y <= this.getBlocksFromLevel(); ++y) {
					BlockPos pos = cartPos.add(x, y, z);
					if (this.countsAsAir(pos) && this.getCart().worldObj.getBiomeForCoordsBody(pos).getTemperature() <= 1.0f && Blocks.SNOW.canPlaceBlockAt(this.getCart().worldObj, pos)) {
						this.getCart().worldObj.setBlockState(pos, Blocks.SNOW.getDefaultState());
					}
				}
			}
		}
	}
}
