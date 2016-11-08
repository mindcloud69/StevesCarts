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
public class ModelSideTanks extends ModelCartbase {
	private static ResourceLocation texture;

	@Override
	public ResourceLocation getResource(final ModuleBase module) {
		return ModelSideTanks.texture;
	}

	@Override
	protected int getTextureHeight() {
		return 16;
	}

	public ModelSideTanks() {
		for (int i = 0; i < 2; ++i) {
			for (int j = 0; j < 2; ++j) {
				final ModelRenderer tankside = new ModelRenderer(this, 0, 0);
				this.AddRenderer(tankside);
				tankside.addBox(-6.0f, -3.0f, -0.5f, 12, 6, 1, 0.0f);
				tankside.setRotationPoint(-2.0f, -0.5f, -10.5f + i * 22 - 3.0f + j * 5);
				final ModelRenderer tanktopbot = new ModelRenderer(this, 0, 7);
				this.AddRenderer(tanktopbot);
				tanktopbot.addBox(-6.0f, -2.0f, -0.5f, 12, 4, 1, 0.0f);
				tanktopbot.setRotationPoint(-2.0f, -3.0f + j * 5, -11.0f + i * 22);
				tanktopbot.rotateAngleX = 1.5707964f;
			}
			final ModelRenderer tankfront = new ModelRenderer(this, 26, 0);
			this.AddRenderer(tankfront);
			tankfront.addBox(-2.0f, -2.0f, -0.5f, 4, 4, 1, 0.0f);
			tankfront.setRotationPoint(-7.5f, -0.5f, -11.0f + i * 22);
			tankfront.rotateAngleY = 1.5707964f;
			final ModelRenderer tankback = new ModelRenderer(this, 36, 0);
			this.AddRenderer(tankback);
			tankback.addBox(-2.0f, -2.0f, -0.5f, 4, 4, 1, 0.0f);
			tankback.setRotationPoint(4.5f, -0.5f, -11.0f + i * 22);
			tankback.rotateAngleY = 1.5707964f;
			final ModelRenderer tube1 = new ModelRenderer(this, 26, 5);
			this.AddRenderer(tube1);
			tube1.addBox(-1.0f, -1.0f, -1.0f, 2, 2, 2, 0.0f);
			tube1.setRotationPoint(5.5f, -0.5f, -11.0f + i * 22);
			final ModelRenderer tube2 = new ModelRenderer(this, 26, 5);
			this.AddRenderer(tube2);
			tube2.addBox(-2.0f, -1.0f, -1.0f, 4, 2, 2, 0.0f);
			tube2.setRotationPoint(7.5f, -0.5f, -10.0f + i * 20);
			tube2.rotateAngleY = 1.5707964f;
			final ModelRenderer connection = new ModelRenderer(this, 36, 0);
			this.AddRenderer(connection);
			connection.addBox(-2.0f, -2.0f, -0.5f, 4, 4, 1, 0.0f);
			connection.setRotationPoint(7.5f, -0.5f, -8.5f + i * 17);
		}
	}

	@Override
	public void render(final Render render, final ModuleBase module, final float yaw, final float pitch, final float roll, final float mult, final float partialtime) {
		super.render(render, module, yaw, pitch, roll, mult, partialtime);
		if (render != null && module != null) {
			final FluidStack liquid = ((ModuleTank) module).getFluid();
			if (liquid != null) {
				((RendererMinecart) render).renderLiquidCuboid(liquid, ((ModuleTank) module).getCapacity(), -2.0f, -0.5f, -11.0f, 10.0f, 4.0f, 4.0f, mult);
				((RendererMinecart) render).renderLiquidCuboid(liquid, ((ModuleTank) module).getCapacity(), -2.0f, -0.5f, 11.0f, 10.0f, 4.0f, 4.0f, mult);
			}
		}
	}

	static {
		ModelSideTanks.texture = ResourceHelper.getResource("/models/tanksModel.png");
	}
}
