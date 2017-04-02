package vswe.stevescarts.models;

import net.minecraft.client.model.ModelRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vswe.stevescarts.helpers.ResourceHelper;
import vswe.stevescarts.modules.ModuleBase;
import vswe.stevescarts.modules.realtimers.ModuleFlowerRemover;

import java.util.ArrayList;

@SideOnly(Side.CLIENT)
public class ModelLawnMower extends ModelCartbase {
	private static ResourceLocation texture;
	private ArrayList<ModelRenderer> bladepins;

	@Override
	public ResourceLocation getResource(final ModuleBase module) {
		return ModelLawnMower.texture;
	}

	@Override
	protected int getTextureWidth() {
		return 64;
	}

	@Override
	protected int getTextureHeight() {
		return 32;
	}

	public ModelLawnMower() {
		this.bladepins = new ArrayList<>();
		this.createSide(false);
		this.createSide(true);
	}

	private void createSide(final boolean opposite) {
		final ModelRenderer anchor = new ModelRenderer(this, 0, 0);
		this.AddRenderer(anchor);
		if (opposite) {
			anchor.rotateAngleY = 3.1415927f;
		}
		final ModelRenderer base = new ModelRenderer(this, 0, 0);
		anchor.addChild(base);
		this.fixSize(base);
		base.addBox(-11.5f, -3.0f, -1.0f, 23, 6, 2, 0.0f);
		base.setRotationPoint(0.0f, -1.5f, -9.0f);
		for (int i = 0; i < 2; ++i) {
			final ModelRenderer arm = new ModelRenderer(this, 0, 8);
			base.addChild(arm);
			this.fixSize(arm);
			arm.addBox(-8.0f, -1.5f, -1.5f, 16, 3, 3, 0.0f);
			arm.setRotationPoint(-8.25f + i * 16.5f, 0.0f, -8.0f);
			arm.rotateAngleY = 1.5707964f;
			final ModelRenderer arm2 = new ModelRenderer(this, 0, 14);
			arm.addChild(arm2);
			this.fixSize(arm2);
			arm2.addBox(-1.5f, -1.5f, -1.5f, 3, 3, 3, 0.0f);
			arm2.setRotationPoint(6.5f, 3.0f, 0.0f);
			arm2.rotateAngleZ = 1.5707964f;
			final ModelRenderer bladepin = new ModelRenderer(this, 0, 20);
			arm2.addChild(bladepin);
			this.fixSize(bladepin);
			bladepin.addBox(-1.0f, -0.5f, -0.5f, 2, 1, 1, 0.0f);
			bladepin.setRotationPoint(2.5f, 0.0f, 0.0f);
			final ModelRenderer bladeanchor = new ModelRenderer(this, 0, 0);
			bladepin.addChild(bladeanchor);
			bladeanchor.rotateAngleY = 1.5707964f;
			for (int j = 0; j < 4; ++j) {
				final ModelRenderer blade = new ModelRenderer(this, 0, 22);
				bladeanchor.addChild(blade);
				this.fixSize(blade);
				blade.addBox(-1.5f, -1.5f, -0.5f, 8, 3, 1, 0.0f);
				blade.setRotationPoint(0.0f, 0.0f, j * 0.01f);
				blade.rotateAngleZ = 1.5707964f * (j + i * 0.5f);
				final ModelRenderer bladetip = new ModelRenderer(this, 0, 26);
				blade.addChild(bladetip);
				this.fixSize(bladetip);
				bladetip.addBox(6.5f, -1.0f, -0.5f, 6, 2, 1, 0.0f);
				bladetip.setRotationPoint(0.0f, 0.0f, 0.005f);
			}
			this.bladepins.add(bladepin);
		}
	}

	@Override
	public void applyEffects(final ModuleBase module, final float yaw, final float pitch, final float roll, final float partialtime) {
		final float angle = (module == null) ? 0.0f : (((ModuleFlowerRemover) module).getBladeAngle() + partialtime * ((ModuleFlowerRemover) module).getBladeSpindSpeed());
		for (int i = 0; i < this.bladepins.size(); ++i) {
			final ModelRenderer bladepin = this.bladepins.get(i);
			if (i % 2 == 0) {
				bladepin.rotateAngleX = angle;
			} else {
				bladepin.rotateAngleX = -angle;
			}
		}
	}

	static {
		ModelLawnMower.texture = ResourceHelper.getResource("/models/lawnmowerModel.png");
	}
}
