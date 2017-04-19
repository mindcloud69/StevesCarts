package vswe.stevescarts.models;

import net.minecraft.client.model.ModelRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vswe.stevescarts.modules.ILeverModule;
import vswe.stevescarts.modules.ModuleBase;

@SideOnly(Side.CLIENT)
public class ModelLever extends ModelCartbase {
	ModelRenderer lever;
	ResourceLocation resource;

	@Override
	public ResourceLocation getResource(final ModuleBase module) {
		return resource;
	}

	@Override
	protected int getTextureWidth() {
		return 32;
	}

	@Override
	protected int getTextureHeight() {
		return 32;
	}

	public ModelLever(final ResourceLocation resource) {
		this.resource = resource;
		final ModelRenderer base = new ModelRenderer(this, 0, 0);
		AddRenderer(base);
		base.addBox(-2.5f, -1.5f, -0.5f, 5, 3, 1, 0.0f);
		base.setRotationPoint(0.0f, 2.0f, 8.5f);
		base.addChild(lever = new ModelRenderer(this, 0, 4));
		fixSize(lever);
		lever.addBox(-0.5f, -12.0f, -0.5f, 1, 11, 1, 0.0f);
		lever.setRotationPoint(0.0f, 0.0f, 0.0f);
		final ModelRenderer handle = new ModelRenderer(this, 4, 4);
		lever.addChild(handle);
		fixSize(handle);
		handle.addBox(-1.0f, -13.0f, -1.0f, 2, 2, 2, 0.0f);
		handle.setRotationPoint(0.0f, 0.0f, 0.0f);
	}

	@Override
	public void applyEffects(final ModuleBase module, final float yaw, final float pitch, final float roll) {
		lever.rotateAngleZ = ((module == null) ? 0.0f : (0.3926991f - ((ILeverModule) module).getLeverState() * 3.1415927f / 4.0f));
	}
}
