package vswe.stevescarts.Models.Cart;

import net.minecraft.client.model.ModelRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vswe.stevescarts.Helpers.ResourceHelper;
import vswe.stevescarts.Modules.ModuleBase;
import vswe.stevescarts.Modules.Realtimers.ModuleSeat;

@SideOnly(Side.CLIENT)
public class ModelSeat extends ModelCartbase {
	private static ResourceLocation texture;
	ModelRenderer sit;

	@Override
	public ResourceLocation getResource(final ModuleBase module) {
		return ModelSeat.texture;
	}

	@Override
	protected int getTextureWidth() {
		return 32;
	}

	@Override
	protected int getTextureHeight() {
		return 32;
	}

	public ModelSeat() {
		this.AddRenderer(this.sit = new ModelRenderer(this, 0, 0));
		this.sit.addBox(-4.0f, -2.0f, -2.0f, 8, 4, 4, 0.0f);
		this.sit.setRotationPoint(0.0f, 1.0f, 0.0f);
		final ModelRenderer back = new ModelRenderer(this, 0, 8);
		this.sit.addChild(back);
		this.fixSize(back);
		back.addBox(-4.0f, -2.0f, -1.0f, 8, 12, 2, 0.0f);
		back.setRotationPoint(0.0f, -8.0f, 3.0f);
	}

	@Override
	public void applyEffects(final ModuleBase module, final float yaw, final float pitch, final float roll) {
		this.sit.rotateAngleY = ((module == null) ? 1.5707964f : (((ModuleSeat) module).getChairAngle() + (((ModuleSeat) module).useRelativeRender() ? 0.0f : yaw)));
	}

	static {
		ModelSeat.texture = ResourceHelper.getResource("/models/chairModel.png");
	}
}
