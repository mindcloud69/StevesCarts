package vswe.stevescarts.models;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vswe.stevescarts.helpers.ResourceHelper;
import vswe.stevescarts.modules.ModuleBase;

@SideOnly(Side.CLIENT)
public class ModelPigTail extends ModelCartbase {
	private static ResourceLocation texture;

	@Override
	public ResourceLocation getResource(final ModuleBase module) {
		return ModelPigTail.texture;
	}

	@Override
	protected int getTextureHeight() {
		return 32;
	}

	public ModelPigTail() {
		final ModelRenderer tailanchor = new ModelRenderer(this);
		this.AddRenderer(tailanchor);
		tailanchor.setRotationPoint(10.0f, -4.0f, 0.0f);
		tailanchor.rotateAngleY = 1.5707964f;
		final ModelRenderer tail1 = new ModelRenderer(this, 0, 0);
		this.fixSize(tail1);
		tailanchor.addChild(tail1);
		tail1.addBox(-1.5f, -0.5f, -0.0f, 3, 1, 1, 0.0f);
		tail1.setRotationPoint(0.0f, 0.0f, 0.0f);
		final ModelRenderer tail2 = new ModelRenderer(this, 0, 0);
		this.fixSize(tail2);
		tailanchor.addChild(tail2);
		tail2.addBox(-0.5f, -1.5f, -0.0f, 1, 3, 1, 0.0f);
		tail2.setRotationPoint(2.0f, -2.0f, 0.0f);
		final ModelRenderer tail3 = new ModelRenderer(this, 0, 0);
		this.fixSize(tail3);
		tailanchor.addChild(tail3);
		tail3.addBox(-1.0f, -0.5f, -0.0f, 2, 1, 1, 0.0f);
		tail3.setRotationPoint(0.5f, -4.0f, 0.0f);
		final ModelRenderer tail4 = new ModelRenderer(this, 0, 0);
		this.fixSize(tail4);
		tailanchor.addChild(tail4);
		tail4.addBox(-0.5f, -0.5f, -0.0f, 1, 1, 1, 0.0f);
		tail4.setRotationPoint(-1.0f, -3.0f, 0.0f);
		final ModelRenderer tail5 = new ModelRenderer(this, 0, 0);
		this.fixSize(tail5);
		tailanchor.addChild(tail5);
		tail5.addBox(-0.5f, -0.5f, -0.0f, 1, 1, 1, 0.0f);
		tail5.setRotationPoint(0.0f, -2.0f, 0.0f);
	}

	@Override
	public void render(final Render render, final ModuleBase module, final float yaw, final float pitch, final float roll, final float mult, final float partialtime) {
		if (module != null) {
			final float[] color = module.getCart().getColor();
			GL11.glColor4f(color[0], color[1], color[2], 1.0f);
		}
		super.render(render, module, yaw, pitch, roll, mult, partialtime);
		GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
	}

	static {
		ModelPigTail.texture = ResourceHelper.getResource("/models/pigtailModel.png");
	}
}
