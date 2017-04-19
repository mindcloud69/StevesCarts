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
		if (getCart().world.isRemote) {
			return;
		}
		if (getCart().hasFuel()) {
			if (tick >= getInterval()) {
				tick = 0;
				generateSnow();
			} else {
				++tick;
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
		for (int x = -getBlocksOnSide(); x <= getBlocksOnSide(); ++x) {
			for (int z = -getBlocksOnSide(); z <= getBlocksOnSide(); ++z) {
				for (int y = -getBlocksFromLevel(); y <= getBlocksFromLevel(); ++y) {
					BlockPos pos = cartPos.add(x, y, z);
					if (countsAsAir(pos) && getCart().world.getBiomeForCoordsBody(pos).getTemperature() <= 1.0f && Blocks.SNOW.canPlaceBlockAt(getCart().world, pos)) {
						getCart().world.setBlockState(pos, Blocks.SNOW.getDefaultState());
					}
				}
			}
		}
	}
}
