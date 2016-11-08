package vswe.stevescarts.models;

import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vswe.stevescarts.helpers.ResourceHelper;
import vswe.stevescarts.modules.ModuleBase;
import vswe.stevescarts.modules.storages.tanks.ModuleTank;
import vswe.stevescarts.renders.RendererMinecart;

@SideOnly(Side.CLIENT)
public class ModelFrontTank extends ModelCartbase {
	private static ResourceLocation texture;

	@Override
	public ResourceLocation getResource(final ModuleBase module) {
		return ModelFrontTank.texture;
	}

	@Override
	protected int getTextureWidth() {
		return 32;
	}

	@Override
	protected int getTextureHeight() {
		return 32;
	}

	public ModelFrontTank() {
		for (int i = 0; i < 2; ++i) {
			final ModelRenderer tankside = new ModelRenderer(this, 0, 15);
			this.AddRenderer(tankside);
			tankside.addBox(-4.0f, -3.0f, -0.5f, 8, 6, 1, 0.0f);
			tankside.setRotationPoint(-14.0f, 0.0f, -6.5f + i * 13);
			final ModelRenderer tanktopbot = new ModelRenderer(this, 0, 0);
			this.AddRenderer(tanktopbot);
			tanktopbot.addBox(-4.0f, -7.0f, -0.5f, 8, 14, 1, 0.0f);
			tanktopbot.setRotationPoint(-14.0f, 3.5f - i * 7, 0.0f);
			tanktopbot.rotateAngleX = 1.5707964f;
			final ModelRenderer tankfrontback = new ModelRenderer(this, 0, 22);
			this.AddRenderer(tankfrontback);
			tankfrontback.addBox(-6.0f, -3.0f, -0.5f, 12, 6, 1, 0.0f);
			tankfrontback.setRotationPoint(-17.5f + i * 7, 0.0f, 0.0f);
			tankfrontback.rotateAngleY = 1.5707964f;
		}
	}

	@Override
	public void render(final Render render, final ModuleBase module, final float yaw, final float pitch, final float roll, final float mult, final float partialtime) {
		super.render(render, module, yaw, pitch, roll, mult, partialtime);
		if (render != null && module != null) {
			final FluidStack liquid = ((ModuleTank) module).getFluid();
			if (liquid != null) {
				((RendererMinecart) render).renderLiquidCuboid(liquid, ((ModuleTank) module).getCapacity(), -14.0f, 0.0f, 0.0f, 6.0f, 6.0f, 12.0f, mult);
			}
		}
	}

	static {
		ModelFrontTank.texture = ResourceHelper.getResource("/models/tankModelFront.png");
	}
}
