package vswe.stevescarts.Models.Cart;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vswe.stevescarts.Modules.ModuleBase;

@SideOnly(Side.CLIENT)
public class ModelHullTop extends ModelCartbase {
	private ResourceLocation resource;
	private boolean useColors;

	@Override
	public ResourceLocation getResource(final ModuleBase module) {
		return this.resource;
	}

	@Override
	protected int getTextureHeight() {
		return 16;
	}

	public ModelHullTop(final ResourceLocation resource) {
		this(resource, true);
	}

	public ModelHullTop(final ResourceLocation resource, final boolean useColors) {
		this.resource = resource;
		this.useColors = useColors;
		final ModelRenderer top = new ModelRenderer(this, 0, 0);
		this.AddRenderer(top);
		top.addBox(-8.0f, -6.0f, -1.0f, 16, 12, 2, 0.0f);
		top.setRotationPoint(0.0f, -4.0f, 0.0f);
		top.rotateAngleX = -1.5707964f;
	}

	@Override
	public void render(final Render render, final ModuleBase module, final float yaw, final float pitch, final float roll, final float mult, final float partialtime) {
		if (this.useColors && module != null) {
			final float[] color = module.getCart().getColor();
			GL11.glColor4f(color[0], color[1], color[2], 1.0f);
		}
		super.render(render, module, yaw, pitch, roll, mult, partialtime);
		GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
	}
}
