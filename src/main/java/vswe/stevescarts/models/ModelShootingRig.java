package vswe.stevescarts.models;

import net.minecraft.client.model.ModelRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vswe.stevescarts.helpers.ResourceHelper;
import vswe.stevescarts.modules.ModuleBase;

@SideOnly(Side.CLIENT)
public class ModelShootingRig extends ModelCartbase {
	private static ResourceLocation texture;

	@Override
	public ResourceLocation getResource(final ModuleBase module) {
		return ModelShootingRig.texture;
	}

	@Override
	protected int getTextureHeight() {
		return 16;
	}

	public ModelShootingRig() {
		final ModelRenderer base = new ModelRenderer(this, 0, 0);
		this.AddRenderer(base);
		base.addBox(-7.0f, -0.5f, -3.0f, 14, 1, 6, 0.0f);
		base.setRotationPoint(0.0f, -5.5f, -0.0f);
		base.rotateAngleY = 1.5707964f;
		final ModelRenderer pillar = new ModelRenderer(this, 0, 7);
		this.AddRenderer(pillar);
		pillar.addBox(-2.0f, -2.5f, -2.0f, 4, 5, 4, 0.0f);
		pillar.setRotationPoint(0.0f, -8.0f, -0.0f);
		final ModelRenderer top = new ModelRenderer(this, 16, 7);
		this.AddRenderer(top);
		top.addBox(-3.0f, -1.0f, -3.0f, 6, 2, 6, 0.0f);
		top.setRotationPoint(0.0f, -11.0f, -0.0f);
	}

	static {
		ModelShootingRig.texture = ResourceHelper.getResource("/models/rigModel.png");
	}
}
