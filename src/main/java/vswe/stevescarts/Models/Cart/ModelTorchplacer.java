package vswe.stevescarts.Models.Cart;

import net.minecraft.client.model.ModelRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vswe.stevescarts.Helpers.ResourceHelper;
import vswe.stevescarts.Modules.ModuleBase;
import vswe.stevescarts.Modules.Workers.ModuleTorch;

@SideOnly(Side.CLIENT)
public class ModelTorchplacer extends ModelCartbase {
	private static ResourceLocation texture;
	ModelRenderer[] torches1;
	ModelRenderer[] torches2;

	@Override
	public ResourceLocation getResource(final ModuleBase module) {
		return ModelTorchplacer.texture;
	}

	@Override
	protected int getTextureWidth() {
		return 32;
	}

	@Override
	protected int getTextureHeight() {
		return 32;
	}

	public ModelTorchplacer() {
		this.torches1 = this.createSide(false);
		this.torches2 = this.createSide(true);
	}

	private ModelRenderer[] createSide(final boolean opposite) {
		final ModelRenderer anchor = new ModelRenderer(this, 0, 0);
		this.AddRenderer(anchor);
		if (opposite) {
			anchor.rotateAngleY = 3.1415927f;
		}
		final ModelRenderer base = new ModelRenderer(this, 0, 0);
		anchor.addChild(base);
		this.fixSize(base);
		base.addBox(-7.0f, -2.0f, -1.0f, 14, 4, 2, 0.0f);
		base.setRotationPoint(0.0f, -2.0f, -9.0f);
		final ModelRenderer[] torches = new ModelRenderer[3];
		for (int i = -1; i <= 1; ++i) {
			final ModelRenderer torchHolder = new ModelRenderer(this, 0, 6);
			base.addChild(torchHolder);
			this.fixSize(torchHolder);
			torchHolder.addBox(-1.0f, -1.0f, -0.5f, 2, 2, 1, 0.0f);
			torchHolder.setRotationPoint(i * 4, 0.0f, -1.5f);
			final ModelRenderer torch = new ModelRenderer(this, 0, 9);
			torchHolder.addChild(torches[i + 1] = torch);
			this.fixSize(torch);
			torch.addBox(-1.0f, -5.0f, -1.0f, 2, 10, 2, 0.0f);
			torch.setRotationPoint(0.0f, 0.0f, -1.5f);
		}
		return torches;
	}

	@Override
	public void applyEffects(final ModuleBase module, final float yaw, final float pitch, final float roll) {
		final int torches = (module == null) ? 7 : ((ModuleTorch) module).getTorches();
		for (int i = 0; i < 3; ++i) {
			final boolean isTorch = (torches & 1 << i) != 0x0;
			this.torches1[i].isHidden = !isTorch;
			this.torches2[2 - i].isHidden = !isTorch;
		}
	}

	static {
		ModelTorchplacer.texture = ResourceHelper.getResource("/models/torchModel.png");
	}
}
