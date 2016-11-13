package vswe.stevesvehicles.client.rendering.models.cart;
import net.minecraft.util.ResourceLocation;

import vswe.stevesvehicles.client.ResourceHelper;
import vswe.stevesvehicles.module.ModuleBase;

@SideOnly(Side.CLIENT)
public class ModelLiquidDrainer extends ModelCleaner {
	private static final ResourceLocation TEXTURE = ResourceHelper.getResource("/models/cleanerModelLiquid.png");

	@Override
	public ResourceLocation getResource(ModuleBase module) {
		return TEXTURE;
	}
}
