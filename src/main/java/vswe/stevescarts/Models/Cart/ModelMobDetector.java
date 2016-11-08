package vswe.stevescarts.Models.Cart;

import net.minecraft.client.model.ModelRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vswe.stevescarts.Helpers.ResourceHelper;
import vswe.stevescarts.Modules.ModuleBase;
import vswe.stevescarts.Modules.Realtimers.ModuleShooterAdv;

@SideOnly(Side.CLIENT)
public class ModelMobDetector extends ModelCartbase {
	private static ResourceLocation texture;
	ModelRenderer base;

	@Override
	public ResourceLocation getResource(final ModuleBase module) {
		return ModelMobDetector.texture;
	}

	@Override
	protected int getTextureHeight() {
		return 16;
	}

	public ModelMobDetector() {
		this.AddRenderer(this.base = new ModelRenderer(this, 0, 0));
		this.base.addBox(-1.0f, -2.0f, -1.0f, 2, 4, 2, 0.0f);
		this.base.setRotationPoint(0.0f, -14.0f, -0.0f);
		final ModelRenderer body = new ModelRenderer(this, 0, 8);
		this.base.addChild(body);
		this.fixSize(body);
		body.addBox(-2.5f, -1.5f, -0.5f, 5, 3, 1, 0.0f);
		body.setRotationPoint(0.0f, -1.5f, -1.5f);
		for (int i = 0; i < 2; ++i) {
			final ModelRenderer side = new ModelRenderer(this, 0, 13);
			body.addChild(side);
			this.fixSize(side);
			side.addBox(-2.5f, -0.5f, -0.5f, 5, 1, 1, 0.0f);
			side.setRotationPoint(0.0f, 2.0f * (i * 2 - 1), -1.0f);
		}
		for (int i = 0; i < 2; ++i) {
			final ModelRenderer side = new ModelRenderer(this, 12, 13);
			body.addChild(side);
			this.fixSize(side);
			side.addBox(-1.5f, -0.5f, -0.5f, 3, 1, 1, 0.0f);
			side.setRotationPoint(3.0f * (i * 2 - 1), 0.0f, -1.0f);
			side.rotateAngleZ = 1.5707964f;
		}
		final ModelRenderer receiver = new ModelRenderer(this, 8, 0);
		body.addChild(receiver);
		this.fixSize(receiver);
		receiver.addBox(-0.5f, -0.5f, -0.5f, 1, 1, 1, 0.0f);
		receiver.setRotationPoint(0.0f, 0.0f, -1.0f);
		receiver.rotateAngleY = 1.5707964f;
		final ModelRenderer dot = new ModelRenderer(this, 8, 2);
		body.addChild(dot);
		this.fixSize(dot);
		dot.addBox(-0.5f, -0.5f, -0.5f, 1, 1, 1, 0.0f);
		dot.setRotationPoint(0.0f, 0.0f, -2.0f);
	}

	@Override
	public void applyEffects(final ModuleBase module, final float yaw, final float pitch, final float roll) {
		this.base.rotateAngleY = ((module == null) ? 0.0f : (((ModuleShooterAdv) module).getDetectorAngle() + yaw));
	}

	static {
		ModelMobDetector.texture = ResourceHelper.getResource("/models/mobDetectorModel.png");
	}
}
