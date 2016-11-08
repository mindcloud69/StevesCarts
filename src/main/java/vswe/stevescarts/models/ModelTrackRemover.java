package vswe.stevescarts.models;

import net.minecraft.client.model.ModelRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vswe.stevescarts.helpers.ResourceHelper;
import vswe.stevescarts.modules.ModuleBase;

@SideOnly(Side.CLIENT)
public class ModelTrackRemover extends ModelCartbase {
	private static ResourceLocation texture;

	@Override
	public ResourceLocation getResource(final ModuleBase module) {
		return ModelTrackRemover.texture;
	}

	public ModelTrackRemover() {
		final ModelRenderer base = new ModelRenderer(this, 0, 0);
		this.AddRenderer(base);
		base.addBox(-5.0f, -5.0f, -0.5f, 10, 10, 1, 0.0f);
		base.setRotationPoint(0.0f, -5.5f, -0.0f);
		base.rotateAngleX = 1.5707964f;
		final ModelRenderer pipe = new ModelRenderer(this, 0, 11);
		this.AddRenderer(pipe);
		pipe.addBox(-2.5f, -2.5f, -2.5f, 6, 5, 5, 0.0f);
		pipe.setRotationPoint(0.0f, -9.5f, -0.0f);
		pipe.rotateAngleZ = 1.5707964f;
		final ModelRenderer pipe2 = new ModelRenderer(this, 0, 21);
		pipe.addChild(pipe2);
		this.fixSize(pipe2);
		pipe2.addBox(-2.5f, -2.5f, -2.5f, 19, 5, 5, 0.0f);
		pipe2.setRotationPoint(0.005f, -0.005f, -0.005f);
		pipe2.rotateAngleZ = -1.5707964f;
		final ModelRenderer pipe3 = new ModelRenderer(this, 22, 0);
		pipe2.addChild(pipe3);
		this.fixSize(pipe3);
		pipe3.addBox(-2.5f, -2.5f, -2.5f, 14, 5, 5, 0.0f);
		pipe3.setRotationPoint(14.005f, -0.005f, 0.005f);
		pipe3.rotateAngleZ = 1.5707964f;
		final ModelRenderer end = new ModelRenderer(this, 0, 31);
		pipe3.addChild(end);
		this.fixSize(end);
		end.addBox(-7.0f, -11.0f, -0.5f, 14, 14, 1, 0.0f);
		end.setRotationPoint(12.0f, 0.0f, -0.0f);
		end.rotateAngleY = 1.5707964f;
	}

	static {
		ModelTrackRemover.texture = ResourceHelper.getResource("/models/removerModel.png");
	}
}
