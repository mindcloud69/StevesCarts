package vswe.stevescarts.models;

import net.minecraft.client.model.ModelRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vswe.stevescarts.helpers.ResourceHelper;
import vswe.stevescarts.modules.ModuleBase;
import vswe.stevescarts.modules.storages.chests.ModuleExtractingChests;

@SideOnly(Side.CLIENT)
public class ModelExtractingChests extends ModelCartbase {
	private static ResourceLocation texture;
	ModelRenderer lid1;
	ModelRenderer lid2;
	ModelRenderer base1;
	ModelRenderer base2;

	@Override
	public ResourceLocation getResource(final ModuleBase module) {
		return ModelExtractingChests.texture;
	}

	public ModelExtractingChests() {
		ModelRenderer[] temp = this.AddChest(false);
		this.base1 = temp[0];
		this.lid1 = temp[1];
		temp = this.AddChest(true);
		this.base2 = temp[0];
		this.lid2 = temp[1];
	}

	private ModelRenderer[] AddChest(final boolean opposite) {
		final ModelRenderer chestAnchor = new ModelRenderer(this);
		this.AddRenderer(chestAnchor);
		if (opposite) {
			chestAnchor.rotateAngleY = 3.1415927f;
		}
		final ModelRenderer base = new ModelRenderer(this, 0, 17);
		this.fixSize(base);
		chestAnchor.addChild(base);
		base.addBox(8.0f, 3.0f, 2.0f, 16, 6, 14, 0.0f);
		base.setRotationPoint(-16.0f, -5.5f, -14.0f);
		final ModelRenderer lid = new ModelRenderer(this, 0, 0);
		this.fixSize(lid);
		chestAnchor.addChild(lid);
		lid.addBox(8.0f, -3.0f, -14.0f, 16, 3, 14, 0.0f);
		lid.setRotationPoint(-16.0f, -1.5f, 2.0f);
		final ModelRenderer lock = new ModelRenderer(this, 0, 37);
		this.fixSize(lock);
		lid.addChild(lock);
		lock.addBox(1.0f, 1.5f, 0.5f, 2, 3, 1, 0.0f);
		lock.setRotationPoint(14.0f, -3.0f, -15.5f);
		return new ModelRenderer[] { base, lid };
	}

	@Override
	public void applyEffects(final ModuleBase module, final float yaw, final float pitch, final float roll) {
		if (module == null) {
			this.lid1.rotateAngleX = 0.0f;
			this.lid2.rotateAngleX = 0.0f;
			this.lid1.rotationPointZ = 2.0f;
			this.lid2.rotationPointZ = 2.0f;
			this.base1.rotationPointZ = -14.0f;
			this.base2.rotationPointZ = -14.0f;
		} else {
			final ModuleExtractingChests chest = (ModuleExtractingChests) module;
			this.lid1.rotateAngleX = -chest.getChestAngle();
			this.lid2.rotateAngleX = -chest.getChestAngle();
			this.lid1.rotationPointZ = chest.getChestOffset() + 16.0f;
			this.lid2.rotationPointZ = chest.getChestOffset() + 16.0f;
			this.base1.rotationPointZ = chest.getChestOffset();
			this.base2.rotationPointZ = chest.getChestOffset();
		}
	}

	static {
		ModelExtractingChests.texture = ResourceHelper.getResource("/models/codeSideChestsModel.png");
	}
}
