package vswe.stevescarts.models;

import net.minecraft.client.model.ModelRenderer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vswe.stevescarts.modules.ModuleBase;
import vswe.stevescarts.modules.realtimers.ModuleShooterAdv;

@SideOnly(Side.CLIENT)
public class ModelSniperRifle extends ModelGun {
	ModelRenderer anchor;
	ModelRenderer gun;

	public ModelSniperRifle() {
		this.AddRenderer(this.anchor = new ModelRenderer(this));
		this.gun = this.createGun(this.anchor);
	}

	@Override
	public void applyEffects(final ModuleBase module, final float yaw, final float pitch, final float roll) {
		this.gun.rotateAngleZ = ((module == null) ? 0.0f : ((ModuleShooterAdv) module).getPipeRotation(0));
		this.anchor.rotateAngleY = ((module == null) ? 0.0f : (3.1415927f + ((ModuleShooterAdv) module).getRifleDirection() + yaw));
	}
}
