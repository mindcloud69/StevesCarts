package vswe.stevescarts.models.engines;

import net.minecraft.client.model.ModelRenderer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vswe.stevescarts.models.ModelCartbase;
import vswe.stevescarts.modules.ModuleBase;
import vswe.stevescarts.modules.engines.ModuleSolarTop;

@SideOnly(Side.CLIENT)
public abstract class ModelSolarPanel extends ModelCartbase {
	ModelRenderer moving;

	protected ModelRenderer createMovingHolder(final int x, final int y) {
		final ModelRenderer moving = new ModelRenderer(this, x, y);
		this.AddRenderer(this.moving = moving);
		return moving;
	}

	@Override
	public void applyEffects(final ModuleBase module, final float yaw, final float pitch, final float roll) {
		this.moving.rotationPointY = ((module == null) ? -4.0f : ((ModuleSolarTop) module).getMovingLevel());
	}
}
