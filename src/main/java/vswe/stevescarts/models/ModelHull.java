package vswe.stevescarts.models;

import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import vswe.stevescarts.modules.ModuleBase;

@SideOnly(Side.CLIENT)
public class ModelHull extends ModelCartbase {
	private ResourceLocation resource;

	@Override
	public ResourceLocation getResource(final ModuleBase module) {
		return this.resource;
	}

	@Override
	protected int getTextureHeight() {
		return 32;
	}

	public ModelHull(final ResourceLocation resource) {
		this.resource = resource;
		final ModelRenderer bot = new ModelRenderer(this, 0, 0);
		final ModelRenderer front = new ModelRenderer(this, 0, 18);
		final ModelRenderer left = new ModelRenderer(this, 0, 18);
		final ModelRenderer right = new ModelRenderer(this, 0, 18);
		final ModelRenderer back = new ModelRenderer(this, 0, 18);
		this.AddRenderer(bot);
		this.AddRenderer(front);
		this.AddRenderer(left);
		this.AddRenderer(right);
		this.AddRenderer(back);
		bot.addBox(-10.0f, -8.0f, -1.0f, 20, 16, 2, 0.0f);
		bot.setRotationPoint(0.0f, 4.0f, 0.0f);
		front.addBox(-8.0f, -9.0f, -1.0f, 16, 8, 2, 0.0f);
		front.setRotationPoint(-9.0f, 4.0f, 0.0f);
		left.addBox(-8.0f, -9.0f, -1.0f, 16, 8, 2, 0.0f);
		left.setRotationPoint(0.0f, 4.0f, -7.0f);
		right.addBox(-8.0f, -9.0f, -1.0f, 16, 8, 2, 0.0f);
		right.setRotationPoint(0.0f, 4.0f, 7.0f);
		back.addBox(-8.0f, -9.0f, -1.0f, 16, 8, 2, 0.0f);
		back.setRotationPoint(9.0f, 4.0f, 0.0f);
		bot.rotateAngleX = 1.5707964f;
		front.rotateAngleY = 4.712389f;
		left.rotateAngleY = 3.1415927f;
		back.rotateAngleY = 1.5707964f;
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
}
