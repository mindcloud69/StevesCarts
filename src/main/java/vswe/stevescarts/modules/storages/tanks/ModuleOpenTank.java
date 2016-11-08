package vswe.stevescarts.modules.storages.tanks;

import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import vswe.stevescarts.entitys.EntityMinecartModular;

public class ModuleOpenTank extends ModuleTank {
	int cooldown;

	public ModuleOpenTank(final EntityMinecartModular cart) {
		super(cart);
		this.cooldown = 0;
	}

	@Override
	protected int getTankSize() {
		return 7000;
	}

	@Override
	public void update() {
		super.update();
		if (this.cooldown > 0) {
			--this.cooldown;
		} else {
			this.cooldown = 20;
			if (this.getCart().worldObj.isRaining() && this.getCart().worldObj.canSeeSky(new BlockPos(this.getCart().x(), this.getCart().y() + 1, this.getCart().z())) && this.getCart().worldObj.getPrecipitationHeight(new BlockPos(this.getCart().x(),0, this.getCart().z())).getY() < this.getCart().y() + 1) {
				this.fill(new FluidStack(FluidRegistry.WATER, this.getCart().worldObj.getBiome(new BlockPos(this.getCart().x(), 0, this.getCart().z())).getEnableSnow() ? 2 : 5), true);
			}
		}
	}
}
