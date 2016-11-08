package vswe.stevescarts.models.engines;

import net.minecraft.client.model.ModelRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vswe.stevescarts.helpers.ResourceHelper;
import vswe.stevescarts.modules.ModuleBase;

@SideOnly(Side.CLIENT)
public class ModelEngineFrame extends ModelEngineBase {
	private static ResourceLocation texture;

	@Override
	public ResourceLocation getResource(final ModuleBase module) {
		return ModelEngineFrame.texture;
	}

	@Override
	protected int getTextureWidth() {
		return 8;
	}

	@Override
	protected int getTextureHeight() {
		return 8;
	}

	public ModelEngineFrame() {
		final ModelRenderer left = new ModelRenderer(this, 0, 0);
		this.anchor.addChild(left);
		this.fixSize(left);
		left.addBox(-0.5f, -2.5f, -0.5f, 1, 5, 1, 0.0f);
		left.setRotationPoint(-4.0f, 0.0f, 0.0f);
		final ModelRenderer right = new ModelRenderer(this, 0, 0);
		this.anchor.addChild(right);
		this.fixSize(right);
		right.addBox(-0.5f, -2.5f, -0.5f, 1, 5, 1, 0.0f);
		right.setRotationPoint(4.0f, 0.0f, 0.0f);
		final ModelRenderer top = new ModelRenderer(this, 4, 0);
		this.anchor.addChild(top);
		this.fixSize(top);
		top.addBox(-0.5f, -3.5f, -0.5f, 1, 7, 1, 0.0f);
		top.setRotationPoint(0.0f, -3.0f, 0.0f);
		top.rotateAngleZ = 1.5707964f;
		final ModelRenderer bot = new ModelRenderer(this, 4, 0);
		this.anchor.addChild(bot);
		this.fixSize(bot);
		bot.addBox(-0.5f, -3.5f, -0.5f, 1, 7, 1, 0.0f);
		bot.setRotationPoint(0.0f, 2.0f, 0.0f);
		bot.rotateAngleZ = 1.5707964f;
	}

	static {
		ModelEngineFrame.texture = ResourceHelper.getResource("/models/engineModelFrame.png");
	}
}
