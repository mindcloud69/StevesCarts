package vswe.stevescarts.Models.Cart;

import net.minecraft.client.model.ModelRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vswe.stevescarts.Helpers.ResourceHelper;
import vswe.stevescarts.Modules.ModuleBase;
import vswe.stevescarts.Modules.Storages.Chests.ModuleChest;

@SideOnly(Side.CLIENT)
public class ModelGiftStorage extends ModelCartbase {
	private static ResourceLocation texture;
	ModelRenderer lid1;
	ModelRenderer lid2;

	@Override
	public ResourceLocation getResource(final ModuleBase module) {
		return ModelGiftStorage.texture;
	}

	@Override
	protected int getTextureHeight() {
		return 64;
	}

	public ModelGiftStorage() {
		this.lid1 = this.AddChest(false);
		this.lid2 = this.AddChest(true);
	}

	private ModelRenderer AddChest(final boolean opposite) {
		final ModelRenderer chestAnchor = new ModelRenderer(this);
		this.AddRenderer(chestAnchor);
		int offsetY = 0;
		if (opposite) {
			chestAnchor.rotateAngleY = 3.1415927f;
			offsetY = 21;
		}
		final ModelRenderer base = new ModelRenderer(this, 0, 7 + offsetY);
		this.fixSize(base);
		chestAnchor.addChild(base);
		base.addBox(8.0f, 3.0f, 2.0f, 16, 6, 4, 0.0f);
		base.setRotationPoint(-16.0f, -5.5f, -14.0f);
		final ModelRenderer lid = new ModelRenderer(this, 0, offsetY);
		this.fixSize(lid);
		chestAnchor.addChild(lid);
		lid.addBox(8.0f, -3.0f, -4.0f, 16, 3, 4, 0.0f);
		lid.setRotationPoint(-16.0f, -1.5f, -8.0f);
		final ModelRenderer lock = new ModelRenderer(this, 0, 17 + offsetY);
		this.fixSize(lock);
		lid.addChild(lock);
		lock.addBox(1.0f, 1.5f, 0.5f, 2, 3, 1, 0.0f);
		lock.setRotationPoint(14.0f, -3.0f, -5.5f);
		return lid;
	}

	@Override
	public void applyEffects(final ModuleBase module, final float yaw, final float pitch, final float roll) {
		this.lid1.rotateAngleX = ((module == null) ? 0.0f : (-((ModuleChest) module).getChestAngle()));
		this.lid2.rotateAngleX = ((module == null) ? 0.0f : (-((ModuleChest) module).getChestAngle()));
	}

	static {
		ModelGiftStorage.texture = ResourceHelper.getResource("/models/giftStorageModel.png");
	}
}
