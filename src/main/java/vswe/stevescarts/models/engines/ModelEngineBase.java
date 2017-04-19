package vswe.stevescarts.models.engines;

import net.minecraft.client.model.ModelRenderer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vswe.stevescarts.models.ModelCartbase;

@SideOnly(Side.CLIENT)
public abstract class ModelEngineBase extends ModelCartbase {
	protected ModelRenderer anchor;

	public ModelEngineBase() {
		AddRenderer(anchor = new ModelRenderer(this, 0, 0));
		anchor.setRotationPoint(10.5f, 0.5f, -0.0f);
		anchor.rotateAngleY = -1.5707964f;
	}
}
