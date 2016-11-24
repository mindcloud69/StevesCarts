package stevesvehicles.client.rendering.models.common;

import net.minecraft.client.model.ModelRenderer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import stevesvehicles.common.modules.ModuleBase;
import stevesvehicles.common.modules.common.attachment.ModuleShooterAdvanced;

@SideOnly(Side.CLIENT)
public class ModelSniperRifle extends ModelGun {
	private ModelRenderer anchor;
	private ModelRenderer gun;

	public ModelSniperRifle() {
		anchor = new ModelRenderer(this);
		addRenderer(anchor);
		gun = createGun(anchor);
	}

	@Override
	public void applyEffects(ModuleBase module, float yaw, float pitch, float roll) {
		gun.rotateAngleZ = module == null ? 0 : ((ModuleShooterAdvanced) module).getPipeRotation(0);
		anchor.rotateAngleY = module == null ? 0 : (float) Math.PI + ((ModuleShooterAdvanced) module).getRifleDirection() + yaw;
	}
}
