package stevesvehicles.common.upgrades.effects.fuel;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import stevesvehicles.common.blocks.tileentitys.TileEntityUpgrade;

public class Solar extends RechargerBase {
	public Solar(TileEntityUpgrade upgrade) {
		super(upgrade);
	}

	@Override
	protected int getAmount() {
		BlockPos masterPos = upgrade.getMaster().getPos();
		BlockPos pos = upgrade.getPos();
		if (pos.getY() > masterPos.getY()) {
			return 400;
		} else if (pos.getY() < masterPos.getY()) {
			return 0;
		} else {
			return 240;
		}
	}

	@Override
	protected boolean canGenerate() {
		World world = upgrade.getWorld();
		BlockPos pos = upgrade.getPos();
		return world.getLightFor(EnumSkyBlock.BLOCK, pos) == 15 && world.canBlockSeeSky(pos);
	}
}
