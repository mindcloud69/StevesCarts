package vswe.stevescarts.Models.Cart;

import net.minecraft.client.model.ModelRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vswe.stevescarts.Helpers.ResourceHelper;
import vswe.stevescarts.Modules.ModuleBase;

@SideOnly(Side.CLIENT)
public class ModelNote extends ModelCartbase {
	private static ResourceLocation texture;

	@Override
	public ResourceLocation getResource(final ModuleBase module) {
		return ModelNote.texture;
	}

	@Override
	protected int getTextureHeight() {
		return 32;
	}

	public ModelNote() {
		this.AddSpeaker(false);
		this.AddSpeaker(true);
	}

	private void AddSpeaker(final boolean opposite) {
		final ModelRenderer noteAnchor = new ModelRenderer(this);
		this.AddRenderer(noteAnchor);
		final ModelRenderer base = new ModelRenderer(this, 0, 0);
		this.fixSize(base);
		noteAnchor.addChild(base);
		base.addBox(8.0f, 6.0f, 6.0f, 16, 12, 12, 0.0f);
		base.setRotationPoint(-16.0f, -13.5f, -12.0f + 14.0f * (opposite ? 1 : -1));
	}

	static {
		ModelNote.texture = ResourceHelper.getResource("/models/noteModel.png");
	}
}
