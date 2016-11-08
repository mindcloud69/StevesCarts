package vswe.stevescarts.models.workers.tools;

import net.minecraft.client.model.ModelRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vswe.stevescarts.models.ModelCartbase;
import vswe.stevescarts.modules.ModuleBase;
import vswe.stevescarts.modules.workers.tools.ModuleFarmer;

@SideOnly(Side.CLIENT)
public class ModelFarmer extends ModelCartbase {
	private ModelRenderer mainAnchor;
	private ModelRenderer anchor;
	private ModelRenderer[] outers;
	private ResourceLocation resource;

	@Override
	public ResourceLocation getResource(final ModuleBase module) {
		return this.resource;
	}

	@Override
	protected int getTextureWidth() {
		return 128;
	}

	@Override
	public float extraMult() {
		return 0.5f;
	}

	public ModelFarmer(final ResourceLocation resource) {
		this.resource = resource;
		this.AddRenderer(this.mainAnchor = new ModelRenderer(this));
		this.mainAnchor.setRotationPoint(-18.0f, 4.0f, 0.0f);
		for (int i = -1; i <= 1; i += 2) {
			final ModelRenderer smallarm = new ModelRenderer(this, 26, 23);
			this.mainAnchor.addChild(smallarm);
			this.fixSize(smallarm);
			smallarm.addBox(-1.0f, -1.0f, -1.0f, 8, 2, 2, 0.0f);
			smallarm.setRotationPoint(0.0f, 0.0f, i * 17);
		}
		final ModelRenderer mainarm = new ModelRenderer(this, 0, 37);
		this.mainAnchor.addChild(mainarm);
		this.fixSize(mainarm);
		mainarm.addBox(-30.0f, -2.0f, -2.0f, 60, 4, 4, 0.0f);
		mainarm.setRotationPoint(8.0f, 0.0f, 0.0f);
		mainarm.rotateAngleY = 1.5707964f;
		for (int j = -1; j <= 1; j += 2) {
			final ModelRenderer extra = new ModelRenderer(this, 26, 27);
			this.mainAnchor.addChild(extra);
			this.fixSize(extra);
			extra.addBox(-2.5f, -2.5f, -1.0f, 5, 5, 2, 0.0f);
			extra.setRotationPoint(8.0f, 0.0f, j * 30);
			final ModelRenderer bigarm = new ModelRenderer(this, 26, 17);
			this.mainAnchor.addChild(bigarm);
			this.fixSize(bigarm);
			bigarm.addBox(-1.0f, -2.0f, -1.0f, 16, 4, 2, 0.0f);
			bigarm.setRotationPoint(8.0f, 0.0f, j * 32);
		}
		this.anchor = new ModelRenderer(this);
		this.mainAnchor.addChild(this.anchor);
		this.anchor.setRotationPoint(22.0f, 0.0f, 0.0f);
		final float start = -1.5f;
		final float end = 1.5f;
		for (float k = -1.5f; k <= 1.5f; ++k) {
			for (int l = 0; l < 6; ++l) {
				final ModelRenderer side = new ModelRenderer(this, 0, 0);
				this.anchor.addChild(side);
				this.fixSize(side);
				side.addBox(-5.0f, -8.8f, -1.0f, 10, 4, 2, 0.0f);
				side.setRotationPoint(0.0f, 0.0f, k * 20.0f + l % 2 * 0.005f);
				side.rotateAngleZ = l * 6.2831855f / 6.0f;
			}
			if (k == start || k == end) {
				final ModelRenderer sidecenter = new ModelRenderer(this, 0, 12);
				this.anchor.addChild(sidecenter);
				this.fixSize(sidecenter);
				sidecenter.addBox(-6.0f, -6.0f, -0.5f, 12, 12, 1, 0.0f);
				sidecenter.setRotationPoint(0.0f, 0.0f, k * 20.0f);
			} else {
				for (int l = 0; l < 3; ++l) {
					final ModelRenderer sidecenter2 = new ModelRenderer(this, 26, 12);
					this.anchor.addChild(sidecenter2);
					this.fixSize(sidecenter2);
					sidecenter2.addBox(-1.0f, -2.0f, -0.5f, 8, 4, 1, 0.0f);
					sidecenter2.setRotationPoint(0.0f, 0.0f, k * 20.0f);
					sidecenter2.rotateAngleZ = (l + 0.25f) * 6.2831855f / 3.0f;
				}
			}
		}
		for (int m = 0; m < 6; ++m) {
			final ModelRenderer middle = new ModelRenderer(this, 0, 6);
			this.anchor.addChild(middle);
			this.fixSize(middle);
			middle.addBox(-30.0f, -1.7f, -1.0f, 60, 2, 2, 0.0f);
			middle.setRotationPoint(0.0f, 0.0f, m % 2 * 0.005f);
			middle.rotateAngleX = m * 6.2831855f / 6.0f;
			middle.rotateAngleY = 1.5707964f;
		}
		this.outers = new ModelRenderer[6];
		for (int m = 0; m < 6; ++m) {
			final ModelRenderer nailAnchor = new ModelRenderer(this);
			this.anchor.addChild(nailAnchor);
			nailAnchor.rotateAngleX = this.nailRot(m);
			nailAnchor.rotateAngleY = 1.5707964f;
			final ModelRenderer outer = new ModelRenderer(this, 0, 10);
			nailAnchor.addChild(outer);
			this.fixSize(outer);
			outer.addBox(-30.0f, -0.5f, -0.5f, 60, 1, 1, 0.0f);
			outer.setRotationPoint(0.0f, -8.8f, 0.0f);
			outer.rotateAngleX = 3.1415927f;
			this.outers[m] = outer;
			for (int j2 = -13; j2 <= 13; ++j2) {
				if (Math.abs(j2) > 6 || Math.abs(j2) < 4) {
					final ModelRenderer nail = new ModelRenderer(this, 44, 13);
					outer.addChild(nail);
					this.fixSize(nail);
					nail.addBox(-0.5f, -1.5f, -0.5f, 1, 3, 1, 0.0f);
					nail.setRotationPoint(j2 * 2, -2.0f, 0.0f);
				}
			}
		}
	}

	@Override
	public void applyEffects(final ModuleBase module, final float yaw, final float pitch, final float roll) {
		this.mainAnchor.rotateAngleZ = ((module == null) ? 3.926991f : (-((ModuleFarmer) module).getRigAngle()));
		final float farmAngle = (module == null) ? 0.0f : ((ModuleFarmer) module).getFarmAngle();
		this.anchor.rotateAngleZ = -farmAngle;
		for (int i = 0; i < 6; ++i) {
			this.outers[i].rotateAngleX = farmAngle + this.nailRot(i);
		}
	}

	private float nailRot(final int i) {
		return (i + 0.5f) * 6.2831855f / 6.0f;
	}
}
