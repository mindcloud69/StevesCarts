package vswe.stevescarts.modules.addons;

import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import vswe.stevescarts.entitys.EntityMinecartModular;

public class ModuleSnowCannon extends ModuleAddon {
	private int tick;

	public ModuleSnowCannon(final EntityMinecartModular cart) {
		super(cart);
	}

	@Override
	public void update() {
		super.update();
		if (this.getCart().world.isRemote) {
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
					if (this.countsAsAir(pos) && this.getCart().world.getBiomeForCoordsBody(pos).getTemperature() <= 1.0f && Blocks.SNOW.canPlaceBlockAt(this.getCart().world, pos)) {
						this.getCart().world.setBlockState(pos, Blocks.SNOW.getDefaultState());
					}
				}
			}
		}
	}
}
