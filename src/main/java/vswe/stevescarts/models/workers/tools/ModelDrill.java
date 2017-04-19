package vswe.stevescarts.models.workers.tools;

import net.minecraft.client.model.ModelRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vswe.stevescarts.models.ModelCartbase;
import vswe.stevescarts.modules.ModuleBase;
import vswe.stevescarts.modules.workers.tools.ModuleDrill;

@SideOnly(Side.CLIENT)
public class ModelDrill extends ModelCartbase {
	private ModelRenderer drillAnchor;
	private ResourceLocation resource;

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

	public ModelDrill(final ResourceLocation resource) {
		this.resource = resource;
		AddRenderer(drillAnchor = new ModelRenderer(this));
		drillAnchor.rotateAngleY = 4.712389f;
		int srcY = 0;
		for (int i = 0; i < 6; ++i) {
			final ModelRenderer drill = fixSize(new ModelRenderer(this, 0, srcY));
			drillAnchor.addChild(drill);
			drill.addBox(-3.0f + i * 0.5f, -3.0f + i * 0.5f, i, 6 - i, 6 - i, 1, 0.0f);
			drill.setRotationPoint(0.0f, 0.0f, 11.0f);
			srcY += 7 - i;
		}
	}

	@Override
	public void applyEffects(final ModuleBase module, final float yaw, final float pitch, final float roll) {
		for (final Object drill : drillAnchor.childModels) {
			((ModelRenderer) drill).rotateAngleZ = ((module == null) ? 0.0f : ((ModuleDrill) module).getDrillRotation());
		}
	}
}
