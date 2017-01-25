package vswe.stevescarts.models;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vswe.stevescarts.modules.ModuleBase;

@SideOnly(Side.CLIENT)
public class ModelPumpkinHull extends ModelHull {
	private ResourceLocation resourceactive;
	private ResourceLocation resourceidle;

	@Override
	public ResourceLocation getResource(final ModuleBase module) {
		return (module == null || this.isActive(module)) ? this.resourceactive : this.resourceidle;
	}

	public ModelPumpkinHull(final ResourceLocation resourceactive, final ResourceLocation resourceidle) {
		super(resourceactive);
		this.resourceactive = resourceactive;
		this.resourceidle = resourceidle;
	}

	private boolean isActive(final ModuleBase module) {
		final long time = module.getCart().world.getWorldInfo().getWorldTime() % 24000L;
		return time >= 12000L && time <= 18000L;
	}
}
