package vswe.stevesvehicles.client.rendering.models.cart;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vswe.stevesvehicles.client.ResourceHelper;
import vswe.stevesvehicles.module.ModuleBase;

@SideOnly(Side.CLIENT)
public class ModelLiquidDrainer extends ModelCleaner {
	private static final ResourceLocation TEXTURE = ResourceHelper.getResource("/models/cleaner_liquid.png");

	@Override
	public ResourceLocation getResource(ModuleBase module) {
		return TEXTURE;
	}
}
