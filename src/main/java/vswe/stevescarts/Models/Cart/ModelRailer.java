package vswe.stevescarts.Models.Cart;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vswe.stevescarts.Helpers.ResourceHelper;
import vswe.stevescarts.Modules.ModuleBase;
import vswe.stevescarts.Modules.Workers.ModuleRailer;

@SideOnly(Side.CLIENT)
public class ModelRailer extends ModelCartbase {
	private static ResourceLocation texture;
	private ModelRenderer[] rails;

	@Override
	public ResourceLocation getResource(final ModuleBase module) {
		return ModelRailer.texture;
	}

	@Override
	protected int getTextureWidth() {
		return 32;
	}

	@Override
	protected int getTextureHeight() {
		return 32;
	}

	public ModelRailer(final int railCount) {
		this.rails = new ModelRenderer[railCount];
		for (int r = 0; r < this.rails.length; ++r) {
			final ModelRenderer railAnchor = new ModelRenderer(this);
			this.AddRenderer(railAnchor);
			(this.rails[r] = railAnchor).setRotationPoint(0.0f, (float) (-r), 0.0f);
			final ModelRenderer rail1 = new ModelRenderer(this, 18, 0);
			this.fixSize(rail1);
			railAnchor.addChild(rail1);
			rail1.addBox(1.0f, 8.0f, 0.5f, 2, 16, 1, 0.0f);
			rail1.setRotationPoint(-16.0f, -6.5f, -7.0f);
			rail1.rotateAngleZ = 4.712389f;
			rail1.rotateAngleY = 4.712389f;
			final ModelRenderer rail2 = new ModelRenderer(this, 24, 0);
			this.fixSize(rail2);
			railAnchor.addChild(rail2);
			rail2.addBox(1.0f, 8.0f, 0.5f, 2, 16, 1, 0.0f);
			rail2.setRotationPoint(-16.0f, -6.5f, 3.0f);
			rail2.rotateAngleZ = 4.712389f;
			rail2.rotateAngleY = 4.712389f;
			for (int i = 0; i < 4; ++i) {
				final ModelRenderer railbedMiddle = new ModelRenderer(this, 0, 0);
				this.fixSize(railbedMiddle);
				railAnchor.addChild(railbedMiddle);
				railbedMiddle.addBox(4.0f, 1.0f, 0.5f, 8, 2, 1, 0.0f);
				railbedMiddle.setRotationPoint(-8.0f + i * 4, -6.5f, -8.0f);
				railbedMiddle.rotateAngleZ = 4.712389f;
				railbedMiddle.rotateAngleY = 4.712389f;
				final ModelRenderer railbedSide1 = new ModelRenderer(this, 0, 3);
				this.fixSize(railbedSide1);
				railAnchor.addChild(railbedSide1);
				railbedSide1.addBox(0.5f, 1.0f, 0.5f, 1, 2, 1, 0.0f);
				railbedSide1.setRotationPoint(-8.0f + i * 4, -6.5f, -7.5f);
				railbedSide1.rotateAngleZ = 4.712389f;
				railbedSide1.rotateAngleY = 4.712389f;
				final ModelRenderer railbedSide2 = new ModelRenderer(this, 0, 3);
				this.fixSize(railbedSide2);
				railAnchor.addChild(railbedSide2);
				railbedSide2.addBox(0.5f, 1.0f, 0.5f, 1, 2, 1, 0.0f);
				railbedSide2.setRotationPoint(-8.0f + i * 4, -6.5f, 5.5f);
				railbedSide2.rotateAngleZ = 4.712389f;
				railbedSide2.rotateAngleY = 4.712389f;
			}
		}
	}

	@Override
	public void applyEffects(final ModuleBase module, final float yaw, final float pitch, final float roll) {
		final int valid = (module == null) ? 1 : ((ModuleRailer) module).getRails();
		for (int i = 0; i < this.rails.length; ++i) {
			this.rails[i].rotateAngleY = ((module == null) ? 0.0f : ((ModuleRailer) module).getRailAngle(i));
			this.rails[i].isHidden = (i >= valid);
		}
	}

	static {
		ModelRailer.texture = ResourceHelper.getResource("/models/builderModel.png");
	}
}
