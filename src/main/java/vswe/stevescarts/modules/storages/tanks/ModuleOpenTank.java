package vswe.stevescarts.modules.storages.tanks;

import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import vswe.stevescarts.entitys.EntityMinecartModular;

public class ModuleOpenTank extends ModuleTank {
	int cooldown;

	public ModuleOpenTank(final EntityMinecartModular cart) {
		super(cart);
		cooldown = 0;
	}

	@Override
	protected int getTankSize() {
		return 7000;
	}

	@Override
	public void update() {
		super.update();
		if (cooldown > 0) {
			--cooldown;
		} else {
			cooldown = 20;
			if (getCart().world.isRaining() && getCart().world.canSeeSky(new BlockPos(getCart().x(), getCart().y() + 1, getCart().z())) && getCart().world.getPrecipitationHeight(new BlockPos(getCart().x(), 0, getCart().z())).getY() < getCart().y() + 1) {
				fill(new FluidStack(FluidRegistry.WATER, getCart().world.getBiome(new BlockPos(getCart().x(), 0, getCart().z())).getEnableSnow() ? 2 : 5), true);
			}
		}
	}
}
