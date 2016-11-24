package stevesvehicles.client.rendering.models.common;

import net.minecraft.client.model.ModelRenderer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import stevesvehicles.client.rendering.models.ModelVehicle;
import stevesvehicles.common.modules.ModuleBase;
import stevesvehicles.common.modules.common.engine.ModuleSolarTop;

@SideOnly(Side.CLIENT)
public abstract class ModelSolarPanel extends ModelVehicle {
	protected ModelRenderer createMovingHolder(int x, int y) {
		ModelRenderer moving = new ModelRenderer(this, x, y);
		this.moving = moving;
		addRenderer(moving);
		return moving;
	}

	private ModelRenderer moving;

	@Override
	public void applyEffects(ModuleBase module, float yaw, float pitch, float roll) {
		moving.rotationPointY = module == null ? -4 : ((ModuleSolarTop) module).getMovingLevel();
	}
}
