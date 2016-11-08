package vswe.stevescarts.Models.Cart;

import java.util.ArrayList;

import net.minecraft.client.model.ModelRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vswe.stevescarts.Helpers.ResourceHelper;
import vswe.stevescarts.Modules.ModuleBase;
import vswe.stevescarts.Modules.Engines.ModuleSolarTop;

@SideOnly(Side.CLIENT)
public class ModelSolarPanelHeads extends ModelSolarPanel {
	private static ResourceLocation texture;
	private static ResourceLocation texture2;
	ArrayList<ModelRenderer> panels;

	@Override
	public ResourceLocation getResource(final ModuleBase module) {
		if (module != null && ((ModuleSolarTop) module).getLight() == 15) {
			return ModelSolarPanelHeads.texture;
		}
		return ModelSolarPanelHeads.texture2;
	}

	@Override
	protected int getTextureWidth() {
		return 32;
	}

	@Override
	protected int getTextureHeight() {
		return 16;
	}

	public ModelSolarPanelHeads(final int panelCount) {
		this.panels = new ArrayList<ModelRenderer>();
		final ModelRenderer moving = this.createMovingHolder(0, 0);
		for (int i = 0; i < panelCount; ++i) {
			this.createPanel(moving, i);
		}
	}

	private void createPanel(final ModelRenderer base, final int index) {
		float rotation = 0.0f;
		float f = 0.0f;
		switch (index) {
			case 0: {
				rotation = 0.0f;
				f = -1.5f;
				break;
			}
			case 1: {
				rotation = 3.1415927f;
				f = -1.5f;
				break;
			}
			case 2: {
				rotation = 4.712389f;
				f = -6.0f;
				break;
			}
			case 3: {
				rotation = 1.5707964f;
				f = -6.0f;
				break;
			}
			default: {
				return;
			}
		}
		this.createPanel(base, rotation, f);
	}

	private void createPanel(final ModelRenderer base, final float rotation, final float f) {
		final ModelRenderer panel = new ModelRenderer(this, 0, 0);
		this.fixSize(panel);
		base.addChild(panel);
		panel.addBox(-6.0f, 0.0f, -2.0f, 12, 13, 2, 0.0f);
		panel.setRotationPoint((float) Math.sin(rotation) * f, -5.0f, (float) Math.cos(rotation) * f);
		panel.rotateAngleY = rotation;
		this.panels.add(panel);
	}

	@Override
	public void applyEffects(final ModuleBase module, final float yaw, final float pitch, final float roll) {
		super.applyEffects(module, yaw, pitch, roll);
		for (final ModelRenderer panel : this.panels) {
			panel.rotateAngleX = ((module == null) ? 0.0f : (-((ModuleSolarTop) module).getInnerRotation()));
		}
	}

	static {
		ModelSolarPanelHeads.texture = ResourceHelper.getResource("/models/panelModelActive.png");
		ModelSolarPanelHeads.texture2 = ResourceHelper.getResource("/models/panelModelIdle.png");
	}
}
