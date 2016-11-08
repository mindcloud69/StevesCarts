package vswe.stevescarts.Models.Cart;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vswe.stevescarts.Helpers.ResourceHelper;
import vswe.stevescarts.Modules.ModuleBase;

@SideOnly(Side.CLIENT)
public class ModelPigHead extends ModelCartbase {
	private static ResourceLocation texture;

	@Override
	public ResourceLocation getResource(final ModuleBase module) {
		return ModelPigHead.texture;
	}

	@Override
	protected int getTextureHeight() {
		return 32;
	}

	public ModelPigHead() {
		final ModelRenderer head = new ModelRenderer(this, 0, 0);
		this.AddRenderer(head);
		head.addBox(-4.0f, -4.0f, -8.0f, 8, 8, 8, 0.0f);
		head.setRotationPoint(-9.0f, -5.0f, 0.0f);
		head.rotateAngleY = 1.5707964f;
		head.setTextureOffset(16, 16).addBox(-2.0f, 0.0f, -9.0f, 4, 3, 1, 0.0f);
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
		ModelPigHead.texture = ResourceHelper.getResourceFromPath("/entity/pig/pig.png");
	}
}
