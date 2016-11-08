package vswe.stevescarts.Models.Cart;

import net.minecraft.client.model.ModelRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vswe.stevescarts.Helpers.ResourceHelper;
import vswe.stevescarts.Modules.ModuleBase;
import vswe.stevescarts.Modules.Storages.Chests.ModuleChest;

@SideOnly(Side.CLIENT)
public class ModelFrontChest extends ModelCartbase {
	private static ResourceLocation texture;
	ModelRenderer lid;

	@Override
	public ResourceLocation getResource(final ModuleBase module) {
		return ModelFrontChest.texture;
	}

	@Override
	protected int getTextureHeight() {
		return 32;
	}

	public ModelFrontChest() {
		this.lid = this.AddChest();
	}

	private ModelRenderer AddChest() {
		final ModelRenderer chestAnchor = new ModelRenderer(this);
		this.AddRenderer(chestAnchor);
		chestAnchor.rotateAngleY = 1.5707964f;
		chestAnchor.setRotationPoint(-3.5f, 0.0f, 0.0f);
		final ModelRenderer base = new ModelRenderer(this, 0, 11);
		this.fixSize(base);
		chestAnchor.addChild(base);
		base.addBox(7.0f, 3.0f, 4.0f, 14, 6, 8, 0.0f);
		base.setRotationPoint(-14.0f, -5.5f, -18.5f);
		final ModelRenderer lid = new ModelRenderer(this, 0, 0);
		this.fixSize(lid);
		chestAnchor.addChild(lid);
		lid.addBox(7.0f, -3.0f, -8.0f, 14, 3, 8, 0.0f);
		lid.setRotationPoint(-14.0f, -1.5f, -6.5f);
		final ModelRenderer lock = new ModelRenderer(this, 0, 25);
		this.fixSize(lock);
		lid.addChild(lock);
		lock.addBox(1.0f, 1.5f, 0.5f, 2, 3, 1, 0.0f);
		lock.setRotationPoint(12.0f, -3.0f, -9.5f);
		return lid;
	}

	@Override
	public void applyEffects(final ModuleBase module, final float yaw, final float pitch, final float roll) {
		this.lid.rotateAngleX = ((module == null) ? 0.0f : (-((ModuleChest) module).getChestAngle()));
	}

	static {
		ModelFrontChest.texture = ResourceHelper.getResource("/models/frontChestModel.png");
	}
}
