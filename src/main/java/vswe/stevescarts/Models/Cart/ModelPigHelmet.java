package vswe.stevescarts.Models.Cart;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import vswe.stevescarts.Modules.Hull.ModulePig;
import vswe.stevescarts.Modules.ModuleBase;

@SideOnly(Side.CLIENT)
public class ModelPigHelmet extends ModelCartbase {
	private boolean isOverlay;

	@Override
	public ResourceLocation getResource(final ModuleBase module) {
		final ModulePig pig = (ModulePig) module;
		return pig.getHelmetResource(this.isOverlay);
	}

	@Override
	protected int getTextureHeight() {
		return 32;
	}

	public ModelPigHelmet(final boolean isOverlay) {
		this.isOverlay = isOverlay;
		final ModelRenderer Headwear = new ModelRenderer(this, 0, 0);
		this.AddRenderer(Headwear);
		Headwear.addBox(-4.0f, -4.0f, -4.0f, 8, 8, 8, 0.0f);
		Headwear.setRotationPoint(-12.2f + (isOverlay ? 0.2f : 0.0f), -5.4f, 0.0f);
		Headwear.rotateAngleY = 1.5707964f;
	}

	@Override
	public void render(final Render render, final ModuleBase module, final float yaw, final float pitch, final float roll, final float mult, final float partialtime) {
		if (render == null || module == null) {
			return;
		}
		final ModulePig pig = (ModulePig) module;
		if (!pig.hasHelment() || (this.isOverlay && !pig.getHelmetMultiRender())) {
			return;
		}
		final float sizemult = 1.09375f + (this.isOverlay ? 0.020833334f : 0.0f);
		GL11.glScalef(sizemult, sizemult, sizemult);
		if (pig.hasHelmetColor(this.isOverlay)) {
			final int color = pig.getHelmetColor(this.isOverlay);
			final float red = (color >> 16 & 0xFF) / 255.0f;
			final float green = (color >> 8 & 0xFF) / 255.0f;
			final float blue = (color & 0xFF) / 255.0f;
			GL11.glColor3f(red, green, blue);
		}
		super.render(render, module, yaw, pitch, roll, mult, partialtime);
		GL11.glColor3f(1.0f, 1.0f, 1.0f);
		GL11.glScalef(1.0f / sizemult, 1.0f / sizemult, 1.0f / sizemult);
	}
}
