package vswe.stevescarts.models;

import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vswe.stevescarts.helpers.ResourceHelper;
import vswe.stevescarts.modules.ModuleBase;
import vswe.stevescarts.modules.addons.ModuleShield;

@SideOnly(Side.CLIENT)
public class ModelShield extends ModelCartbase {
	private static ResourceLocation texture;
	private ModelRenderer[][] shieldAnchors;
	private ModelRenderer[][] shields;

	@Override
	public ResourceLocation getResource(final ModuleBase module) {
		return ModelShield.texture;
	}

	@Override
	protected int getTextureWidth() {
		return 8;
	}

	@Override
	protected int getTextureHeight() {
		return 4;
	}

	public ModelShield() {
		this.shields = new ModelRenderer[4][5];
		this.shieldAnchors = new ModelRenderer[this.shields.length][this.shields[0].length];
		for (int i = 0; i < this.shields.length; ++i) {
			for (int j = 0; j < this.shields[i].length; ++j) {
				this.AddRenderer(this.shieldAnchors[i][j] = new ModelRenderer(this));
				this.fixSize(this.shields[i][j] = new ModelRenderer(this, 0, 0));
				this.shieldAnchors[i][j].addChild(this.shields[i][j]);
				this.shields[i][j].addBox(-1.0f, -1.0f, -1.0f, 2, 2, 2, 0.0f);
				this.shields[i][j].setRotationPoint(0.0f, 0.0f, 0.0f);
			}
		}
	}

	@Override
	public void render(final Render render, final ModuleBase module, final float yaw, final float pitch, final float roll, final float mult, final float partialtime) {
		if (render == null || module == null || ((ModuleShield) module).hasShield()) {
			super.render(render, module, yaw, pitch, roll, mult, partialtime);
		}
	}

	@Override
	public void applyEffects(final ModuleBase module, final float yaw, final float pitch, final float roll) {
		final float shieldAngle = (module == null) ? 0.0f : ((ModuleShield) module).getShieldAngle();
		final float shieldDistance = (module == null) ? 18.0f : ((ModuleShield) module).getShieldDistance();
		for (int i = 0; i < this.shields.length; ++i) {
			for (int j = 0; j < this.shields[i].length; ++j) {
				float a = shieldAngle + 6.2831855f * (j / this.shields[i].length + i / this.shields.length);
				a %= 314.1592653589793;
				this.shieldAnchors[i][j].rotateAngleY = a;
				this.shields[i][j].rotationPointY = ((float) Math.sin(a / 5.0f) * 3.0f + (i - (this.shields.length - 1) / 2.0f) * 5.0f - 5.0f) * shieldDistance / 18.0f;
				this.shields[i][j].rotationPointZ = shieldDistance;
			}
		}
	}

	static {
		ModelShield.texture = ResourceHelper.getResource("/models/shieldModel.png");
	}
}
