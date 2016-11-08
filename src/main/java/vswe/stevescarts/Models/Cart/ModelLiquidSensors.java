package vswe.stevescarts.Models.Cart;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vswe.stevescarts.Helpers.ResourceHelper;
import vswe.stevescarts.Modules.Addons.ModuleLiquidSensors;
import vswe.stevescarts.Modules.ModuleBase;

@SideOnly(Side.CLIENT)
public class ModelLiquidSensors extends ModelCartbase {
	private static ResourceLocation texture;
	ModelRenderer[] sensor1;
	ModelRenderer[] sensor2;

	@Override
	public ResourceLocation getResource(final ModuleBase module) {
		return ModelLiquidSensors.texture;
	}

	@Override
	protected int getTextureWidth() {
		return 32;
	}

	@Override
	protected int getTextureHeight() {
		return 16;
	}

	public ModelLiquidSensors() {
		this.sensor1 = this.createSensor(false);
		this.sensor2 = this.createSensor(true);
	}

	private ModelRenderer[] createSensor(final boolean right) {
		final ModelRenderer base = new ModelRenderer(this, 0, 0);
		this.AddRenderer(base);
		base.addBox(0.5f, 2.0f, 0.5f, 1, 4, 1, 0.0f);
		if (right) {
			base.setRotationPoint(-10.0f, -11.0f, 6.0f);
		} else {
			base.setRotationPoint(-10.0f, -11.0f, -8.0f);
		}
		final ModelRenderer head = new ModelRenderer(this, 4, 0);
		this.fixSize(head);
		base.addChild(head);
		head.addBox(-2.0f, -2.0f, -2.0f, 4, 4, 4, 0.0f);
		head.setRotationPoint(1.0f, 0.0f, 1.0f);
		final ModelRenderer face = new ModelRenderer(this, 20, 0);
		this.fixSize(face);
		head.addChild(face);
		face.addBox(-0.5f, -1.0f, -1.0f, 1, 2, 2, 0.0f);
		face.setRotationPoint(-2.5f, 0.0f, 0.0f);
		final ModelRenderer[] dynamic = new ModelRenderer[4];
		dynamic[0] = head;
		for (int i = 1; i < 4; ++i) {
			final ModelRenderer light = new ModelRenderer(this, 20, 1 + i * 3);
			this.fixSize(light);
			head.addChild(light);
			light.addBox(-1.0f, -0.5f, -1.0f, 2, 1, 2, 0.0f);
			light.setRotationPoint(0.0f, -2.5f, 0.0f);
			dynamic[i] = light;
		}
		return dynamic;
	}

	@Override
	public void applyEffects(final ModuleBase module, final float yaw, final float pitch, final float roll) {
		this.sensor1[0].rotateAngleY = ((module == null) ? 0.0f : (-((ModuleLiquidSensors) module).getSensorRotation()));
		this.sensor2[0].rotateAngleY = ((module == null) ? 0.0f : ((ModuleLiquidSensors) module).getSensorRotation());
		final int active = (module == null) ? 2 : ((ModuleLiquidSensors) module).getLight();
		for (int i = 1; i < 4; ++i) {
			this.sensor1[i].isHidden = (i != active);
			this.sensor2[i].isHidden = (i != active);
		}
	}

	static {
		ModelLiquidSensors.texture = ResourceHelper.getResource("/models/sensorModel.png");
	}
}
