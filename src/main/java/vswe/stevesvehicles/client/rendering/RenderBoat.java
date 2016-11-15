package vswe.stevesvehicles.client.rendering;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.math.MathHelper;
import vswe.stevesvehicles.vehicle.VehicleBase;
import vswe.stevesvehicles.vehicle.entity.EntityModularBoat;

public class RenderBoat extends RenderVehicle {
	public RenderBoat(RenderManager renderManager) {
		super(renderManager);
	}

	@Override
	protected void applyMatrixUpdates(VehicleBase vehicle, MatrixObject matrix, float partialTickTime) {
		EntityModularBoat boat = (EntityModularBoat) vehicle.getEntity();

		float damageTime = boat.getTimeSinceHit() - partialTickTime;
		float damage = boat.getDamageTaken() - partialTickTime;

		if (damage < 0.0F)
		{
			damage = 0.0F;
		}

		if (damageTime > 0.0F)
		{
			GlStateManager.rotate(MathHelper.sin(damageTime) * damageTime * damage / 10.0F * boat.getForwardDirection(), 1.0F, 0.0F, 0.0F);
		}

		GlStateManager.scale(-1.0F, -1.0F, 1.0F);
	}
}
