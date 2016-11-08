package vswe.stevescarts.Models.Cart;

import net.minecraft.client.model.ModelRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vswe.stevescarts.Modules.ModuleBase;
import vswe.stevescarts.Modules.Workers.Tools.ModuleWoodcutter;

@SideOnly(Side.CLIENT)
public class ModelWoodCutter extends ModelCartbase {
	private ModelRenderer[] anchors;
	private ResourceLocation resource;

	@Override
	public ResourceLocation getResource(final ModuleBase module) {
		return this.resource;
	}

	@Override
	protected int getTextureWidth() {
		return 16;
	}

	@Override
	protected int getTextureHeight() {
		return 8;
	}

	public ModelWoodCutter(final ResourceLocation resource) {
		this.resource = resource;
		this.anchors = new ModelRenderer[5];
		for (int i = -2; i <= 2; ++i) {
			final ModelRenderer anchor = new ModelRenderer(this);
			this.AddRenderer(this.anchors[i + 2] = anchor);
			final ModelRenderer main = new ModelRenderer(this, 0, 0);
			anchor.addChild(main);
			this.fixSize(main);
			main.addBox(-3.5f, -1.5f, -0.5f, 7, 3, 1, 0.0f);
			main.setRotationPoint(-13.0f, 0.0f, i * 2);
			final ModelRenderer tip = new ModelRenderer(this, 0, 4);
			main.addChild(tip);
			this.fixSize(tip);
			tip.addBox(-0.5f, -0.5f, -0.5f, 1, 1, 1, 0.0f);
			tip.setRotationPoint(-4.0f, 0.0f, 0.0f);
		}
	}

	@Override
	public void applyEffects(final ModuleBase module, final float yaw, final float pitch, final float roll) {
		final float commonAngle = (module == null) ? 0.0f : ((ModuleWoodcutter) module).getCutterAngle();
		for (int i = 0; i < this.anchors.length; ++i) {
			float specificAngle;
			if (i % 2 == 0) {
				specificAngle = (float) Math.sin(commonAngle);
			} else {
				specificAngle = (float) Math.cos(commonAngle);
			}
			this.anchors[i].rotationPointX = specificAngle * 1.25f;
		}
	}
}
