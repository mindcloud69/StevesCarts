package vswe.stevescarts.Models.Cart;

import net.minecraft.client.model.ModelRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vswe.stevescarts.Helpers.ResourceHelper;
import vswe.stevescarts.Modules.ModuleBase;
import vswe.stevescarts.Modules.Engines.ModuleSolarCompact;

@SideOnly(Side.CLIENT)
public class ModelCompactSolarPanel extends ModelCartbase {
	private static ResourceLocation texture;
	private static ResourceLocation texture2;
	ModelRenderer[][] models;

	@Override
	public ResourceLocation getResource(final ModuleBase module) {
		if (module != null && ((ModuleSolarCompact) module).getLight() == 15) {
			return ModelCompactSolarPanel.texture;
		}
		return ModelCompactSolarPanel.texture2;
	}

	@Override
	protected int getTextureWidth() {
		return 64;
	}

	@Override
	protected int getTextureHeight() {
		return 32;
	}

	public ModelCompactSolarPanel() {
		(this.models = new ModelRenderer[2][])[0] = this.createSide(false);
		this.models[1] = this.createSide(true);
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
		base.addBox(-7.0f, -6.0f, -1.5f, 14, 6, 3, 0.0f);
		base.setRotationPoint(0.0f, 2.0f, -9.0f);
		final ModelRenderer panelarminner = new ModelRenderer(this, 34, 0);
		anchor.addChild(panelarminner);
		this.fixSize(panelarminner);
		panelarminner.addBox(-1.0f, -1.0f, -2.0f, 2, 2, 4, 0.0f);
		panelarminner.setRotationPoint(0.0f, -1.0f, 0.0f);
		final ModelRenderer panelarmouter = new ModelRenderer(this, 34, 0);
		panelarminner.addChild(panelarmouter);
		this.fixSize(panelarmouter);
		panelarmouter.addBox(-1.0f, -1.0f, -3.0f, 2, 2, 4, 0.0f);
		panelarmouter.setRotationPoint(0.001f, 0.001f, 0.001f);
		final ModelRenderer panelBase = new ModelRenderer(this, 0, 9);
		panelarmouter.addChild(panelBase);
		this.fixSize(panelBase);
		panelBase.addBox(-5.5f, -2.0f, -1.0f, 11, 4, 2, 0.0f);
		panelBase.setRotationPoint(0.0f, 0.0f, -2.8f);
		final ModelRenderer panelTop = this.createPanel(panelBase, 10, 4, -0.497f, 0, 15);
		final ModelRenderer panelBot = this.createPanel(panelBase, 10, 4, -0.494f, 22, 15);
		final ModelRenderer panelLeft = this.createPanel(panelBase, 6, 4, -0.491f, 0, 20);
		final ModelRenderer panelRight = this.createPanel(panelBase, 6, 4, -0.488f, 14, 20);
		final ModelRenderer panelTopLeft = this.createPanel(panelLeft, 6, 4, 0.002f, 0, 25);
		final ModelRenderer panelBotLeft = this.createPanel(panelLeft, 6, 4, 0.001f, 28, 25);
		final ModelRenderer panelTopRight = this.createPanel(panelRight, 6, 4, 0.002f, 14, 25);
		final ModelRenderer panelBotRight = this.createPanel(panelRight, 6, 4, 0.001f, 42, 25);
		return new ModelRenderer[] { panelBase, panelTop, panelBot, panelLeft, panelRight, panelTopLeft, panelTopRight, panelBotLeft, panelBotRight, panelarmouter, panelarminner };
	}

	private ModelRenderer createPanel(final ModelRenderer parent, final int width, final int height, final float offset, final int textureOffsetX, final int textureOffsetY) {
		final ModelRenderer panel = new ModelRenderer(this, textureOffsetX, textureOffsetY);
		parent.addChild(panel);
		this.fixSize(panel);
		panel.addBox(-width / 2, -height / 2, -0.5f, width, height, 1, 0.0f);
		panel.setRotationPoint(0.0f, 0.0f, offset);
		return panel;
	}

	@Override
	public void applyEffects(final ModuleBase module, final float yaw, final float pitch, final float roll) {
		if (module == null) {
			for (int i = 0; i < 2; ++i) {
				final ModelRenderer[] models = this.models[i];
				models[9].rotationPointZ = 0.6f;
				models[10].rotationPointZ = -8.1f;
				models[1].rotationPointY = -0.1f;
				models[2].rotationPointY = 0.1f;
				models[3].rotationPointX = -2.01f;
				models[4].rotationPointX = 2.01f;
				final ModelRenderer modelRenderer = models[5];
				final ModelRenderer modelRenderer2 = models[6];
				final float n = -0.1f;
				modelRenderer2.rotationPointY = n;
				modelRenderer.rotationPointY = n;
				final ModelRenderer modelRenderer3 = models[7];
				final ModelRenderer modelRenderer4 = models[8];
				final float n2 = 0.1f;
				modelRenderer4.rotationPointY = n2;
				modelRenderer3.rotationPointY = n2;
				models[9].rotateAngleX = 0.0f;
			}
		} else {
			final ModuleSolarCompact solar = (ModuleSolarCompact) module;
			for (int j = 0; j < 2; ++j) {
				final ModelRenderer[] models2 = this.models[j];
				models2[9].rotationPointZ = 1.0f - solar.getExtractionDist();
				models2[10].rotationPointZ = -7.7f - solar.getInnerExtraction();
				models2[1].rotationPointY = -solar.getTopBotExtractionDist();
				models2[2].rotationPointY = solar.getTopBotExtractionDist();
				models2[3].rotationPointX = -2.0f - solar.getLeftRightExtractionDist();
				models2[4].rotationPointX = 2.0f + solar.getLeftRightExtractionDist();
				final ModelRenderer modelRenderer5 = models2[5];
				final ModelRenderer modelRenderer6 = models2[6];
				final float n3 = -solar.getCornerExtractionDist();
				modelRenderer6.rotationPointY = n3;
				modelRenderer5.rotationPointY = n3;
				final ModelRenderer modelRenderer7 = models2[7];
				final ModelRenderer modelRenderer8 = models2[8];
				final float cornerExtractionDist = solar.getCornerExtractionDist();
				modelRenderer8.rotationPointY = cornerExtractionDist;
				modelRenderer7.rotationPointY = cornerExtractionDist;
				models2[9].rotateAngleX = -solar.getPanelAngle();
			}
		}
	}

	static {
		ModelCompactSolarPanel.texture = ResourceHelper.getResource("/models/panelModelSideActive.png");
		ModelCompactSolarPanel.texture2 = ResourceHelper.getResource("/models/panelModelSideIdle.png");
	}
}
