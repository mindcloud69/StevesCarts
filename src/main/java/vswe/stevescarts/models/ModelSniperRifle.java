package vswe.stevescarts.models;

import net.minecraft.client.model.ModelRenderer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vswe.stevescarts.models.realtimers.ModelGun;
import vswe.stevescarts.modules.ModuleBase;
import vswe.stevescarts.modules.realtimers.ModuleShooterAdv;

@SideOnly(Side.CLIENT)
public class ModelSniperRifle extends ModelGun {
	ModelRenderer anchor;
	ModelRenderer gun;

	public ModelSniperRifle() {
		AddRenderer(anchor = new ModelRenderer(this));
		gun = createGun(anchor);
	}

	@Override
	public void applyEffects(final ModuleBase module, final float yaw, final float pitch, final float roll) {
		gun.rotateAngleZ = ((module == null) ? 0.0f : ((ModuleShooterAdv) module).getPipeRotation(0));
		anchor.rotateAngleY = ((module == null) ? 0.0f : (3.1415927f + ((ModuleShooterAdv) module).getRifleDirection() + yaw));
	}
}
