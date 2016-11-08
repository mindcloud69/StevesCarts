package vswe.stevescarts.models;

import net.minecraft.client.model.ModelRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vswe.stevescarts.helpers.ResourceHelper;
import vswe.stevescarts.modules.ModuleBase;

@SideOnly(Side.CLIENT)
public class ModelCleaner extends ModelCartbase {
	private static ResourceLocation texture;

	@Override
	public ResourceLocation getResource(final ModuleBase module) {
		return ModelCleaner.texture;
	}

	@Override
	protected int getTextureWidth() {
		return 32;
	}

	@Override
	protected int getTextureHeight() {
		return 32;
	}

	public ModelCleaner() {
		final ModelRenderer box = new ModelRenderer(this, 0, 0);
		this.AddRenderer(box);
		box.addBox(-4.0f, -3.0f, -4.0f, 8, 6, 8, 0.0f);
		box.setRotationPoint(4.0f, -0.0f, -0.0f);
		for (int i = 0; i < 2; ++i) {
			final ModelRenderer sidetube = new ModelRenderer(this, 0, 14);
			this.AddRenderer(sidetube);
			sidetube.addBox(-2.0f, -2.0f, -1.0f, 4, 4, 2, 0.0f);
			sidetube.setRotationPoint(4.0f, -0.0f, -5.0f * (i * 2 - 1));
		}
		final ModelRenderer tube = new ModelRenderer(this, 0, 14);
		this.AddRenderer(tube);
		tube.addBox(-2.0f, -2.0f, -1.0f, 4, 4, 2, 0.0f);
		tube.setRotationPoint(-1.0f, 0.0f, 0.0f);
		tube.rotateAngleY = 1.5707964f;
		for (int j = 0; j < 2; ++j) {
			final ModelRenderer endtube = new ModelRenderer(this, 0, 14);
			this.AddRenderer(endtube);
			endtube.addBox(-2.0f, -2.0f, -1.0f, 4, 4, 2, 0.0f);
			endtube.setRotationPoint(-7.0f, -0.0f, -3.0f * (j * 2 - 1));
			endtube.rotateAngleY = 1.5707964f;
		}
		final ModelRenderer connectiontube = new ModelRenderer(this, 0, 20);
		this.AddRenderer(connectiontube);
		connectiontube.addBox(-5.0f, -5.0f, -1.0f, 10, 4, 4, 0.0f);
		connectiontube.setRotationPoint(-5.0f, 3.0f, 0.0f);
		connectiontube.rotateAngleY = 1.5707964f;
		for (int k = 0; k < 2; ++k) {
			final ModelRenderer externaltube = new ModelRenderer(this, 0, 14);
			this.AddRenderer(externaltube);
			externaltube.addBox(-2.0f, -2.0f, -1.0f, 4, 4, 2, 0.0f);
			externaltube.setRotationPoint(-10.95f, -0.0f, -3.05f * (k * 2 - 1));
			externaltube.rotateAngleY = 1.5707964f;
		}
	}

	static {
		ModelCleaner.texture = ResourceHelper.getResource("/models/cleanerModel.png");
	}
}
