package vswe.stevescarts.Models.Cart;

import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vswe.stevescarts.Helpers.ResourceHelper;
import vswe.stevescarts.Modules.ModuleBase;
import vswe.stevescarts.Modules.Storages.Tanks.ModuleTank;
import vswe.stevescarts.Renders.RendererMinecart;

@SideOnly(Side.CLIENT)
public class ModelAdvancedTank extends ModelCartbase {
	private static ResourceLocation texture;

	@Override
	public ResourceLocation getResource(final ModuleBase module) {
		return ModelAdvancedTank.texture;
	}

	public ModelAdvancedTank() {
		for (int i = 0; i < 2; ++i) {
			final ModelRenderer tankside = new ModelRenderer(this, 0, 13);
			this.AddRenderer(tankside);
			tankside.addBox(-8.0f, -6.5f, -0.5f, 16, 13, 1, 0.0f);
			tankside.setRotationPoint(0.0f, -4.5f, -5.5f + i * 11);
			final ModelRenderer tanktopbot = new ModelRenderer(this, 0, 0);
			this.AddRenderer(tanktopbot);
			tanktopbot.addBox(-8.0f, -6.0f, -0.5f, 16, 12, 1, 0.0f);
			tanktopbot.setRotationPoint(0.0f, 2.5f - i * 14, 0.0f);
			tanktopbot.rotateAngleX = 1.5707964f;
			final ModelRenderer tankfrontback = new ModelRenderer(this, 0, 27);
			this.AddRenderer(tankfrontback);
			tankfrontback.addBox(-5.0f, -6.5f, -0.5f, 10, 13, 1, 0.0f);
			tankfrontback.setRotationPoint(-7.5f + i * 15, -4.5f, 0.0f);
			tankfrontback.rotateAngleY = 1.5707964f;
		}
	}

	@Override
	public void render(final Render render, final ModuleBase module, final float yaw, final float pitch, final float roll, final float mult, final float partialtime) {
		super.render(render, module, yaw, pitch, roll, mult, partialtime);
		if (render != null && module != null) {
			final FluidStack liquid = ((ModuleTank) module).getFluid();
			if (liquid != null) {
				((RendererMinecart) render).renderLiquidCuboid(liquid, ((ModuleTank) module).getCapacity(), 0.0f, -4.5f, 0.0f, 14.0f, 13.0f, 10.0f, mult);
			}
		}
	}

	static {
		ModelAdvancedTank.texture = ResourceHelper.getResource("/models/tankModelLarge.png");
	}
}
