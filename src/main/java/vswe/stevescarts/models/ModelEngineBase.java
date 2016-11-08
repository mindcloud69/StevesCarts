package vswe.stevescarts.models;

import net.minecraft.client.model.ModelRenderer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public abstract class ModelEngineBase extends ModelCartbase {
	protected ModelRenderer anchor;

	public ModelEngineBase() {
		this.AddRenderer(this.anchor = new ModelRenderer(this, 0, 0));
		this.anchor.setRotationPoint(10.5f, 0.5f, -0.0f);
		this.anchor.rotateAngleY = -1.5707964f;
	}
}
