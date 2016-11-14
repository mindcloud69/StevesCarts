package vswe.stevesvehicles.client.rendering.models.common;

import net.minecraft.client.model.ModelRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vswe.stevesvehicles.client.ResourceHelper;
import vswe.stevesvehicles.client.rendering.models.ModelVehicle;
import vswe.stevesvehicles.module.ModuleBase;

@SideOnly(Side.CLIENT)
public class ModelNote extends ModelVehicle {
	private static final ResourceLocation TEXTURE = ResourceHelper.getResource("/models/noteModel.png");

	@Override
	public ResourceLocation getResource(ModuleBase module) {
		return TEXTURE;
	}

	@Override
	protected int getTextureHeight() {
		return 32;
	}

	public ModelNote() {
		addSpeaker(false);
		addSpeaker(true);
	}

	private void addSpeaker(boolean opposite) {
		ModelRenderer noteAnchor = new ModelRenderer(this);
		addRenderer(noteAnchor);
		ModelRenderer base = new ModelRenderer(this, 0, 0);
		fixSize(base);
		noteAnchor.addChild(base);
		base.addBox(8, // X
				6, // Y
				6F, // Z
				16, // Size X
				12, // Size Y
				12, // Size Z
				0.0F);
		base.setRotationPoint(-16.0F, // X
				-13.5F, // Y
				-12 + 14.0F * (opposite ? 1 : -1) // Z
				);
	}
}
