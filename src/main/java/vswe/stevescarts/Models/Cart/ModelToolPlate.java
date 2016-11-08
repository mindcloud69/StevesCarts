package vswe.stevescarts.Models.Cart;

import net.minecraft.client.model.ModelRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vswe.stevescarts.Helpers.ResourceHelper;
import vswe.stevescarts.Modules.ModuleBase;

@SideOnly(Side.CLIENT)
public class ModelToolPlate extends ModelCartbase {
	private static ResourceLocation texture;

	@Override
	public ResourceLocation getResource(final ModuleBase module) {
		return ModelToolPlate.texture;
	}

	@Override
	protected int getTextureWidth() {
		return 32;
	}

	@Override
	protected int getTextureHeight() {
		return 8;
	}

	public ModelToolPlate() {
		final ModelRenderer drillBase = new ModelRenderer(this, 0, 0);
		this.AddRenderer(drillBase);
		drillBase.addBox(-5.0f, -7.0f, -2.0f, 10, 6, 1, 0.0f);
		drillBase.setRotationPoint(-9.0f, 4.0f, 0.0f);
		drillBase.rotateAngleY = 1.5707964f;
	}

	static {
		ModelToolPlate.texture = ResourceHelper.getResource("/models/toolPlateModel.png");
	}
}
