package vswe.stevesvehicles.module.common.hull;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.layers.LayerBipedArmor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vswe.stevesvehicles.vehicle.VehicleBase;

public class HullPig extends ModuleHull {
	private int oinkTimer;
	/**
	 * Only used for getting the texture of the armor.
	 */
	private LayerBipedArmor fakeArmorLayer;

	public HullPig(VehicleBase vehicle) {
		super(vehicle);
		oinkTimer = getRandomTimer();
		fakeArmorLayer = new LayerBipedArmor(null);
	}

	@Override
	public int getConsumption(boolean isMoving) {
		if (!isMoving) {
			return super.getConsumption(isMoving);
		} else {
			return 1;
		}
	}

	@Override
	public void update() {
		if (oinkTimer <= 0) {
			oink();
			oinkTimer = getRandomTimer();
		} else {
			oinkTimer--;
		}
	}

	private void oink() {
		getVehicle().getEntity().playSound(SoundEvents.ENTITY_PIG_AMBIENT, 1.0F, (getVehicle().getRandom().nextFloat() - getVehicle().getRandom().nextFloat()) * 0.2F + 1.0F);
	}

	private int getRandomTimer() {
		return oinkTimer = getVehicle().getRandom().nextInt(900) + 300;
	}

	private ItemStack getHelmet() {
		if (getVehicle().getEntity().getPassengers().isEmpty()) {
			return null;
		}
		Entity rider = getVehicle().getEntity().getPassengers().get(0);
		if (rider != null && rider instanceof EntityLivingBase) {
			return ((EntityLivingBase) rider).getItemStackFromSlot(EntityEquipmentSlot.HEAD);
		}
		return null;
	}

	public boolean hasHelmet() {
		ItemStack item = getHelmet();
		if (item != null) {
			if (item.getItem() instanceof ItemArmor) {
				if (((ItemArmor) item.getItem()).armorType == EntityEquipmentSlot.HEAD) {
					return true;
				}
			}
		}
		return false;
	}

	@SideOnly(Side.CLIENT)
	public ResourceLocation getHelmetResource(boolean isOverlay) {
		if (hasHelmet()) {
			ItemStack item = getHelmet();
			if (item.getItem() == null) {
				return null;
			}
			return fakeArmorLayer.getArmorResource((Entity) null, item, EntityEquipmentSlot.HEAD, isOverlay ? "overlay" : null);
		}
		return null;
	}

	@SideOnly(Side.CLIENT)
	public boolean hasHelmetColor(boolean isOverlay) {
		return getHelmetColor(isOverlay) != -1;
	}

	@SideOnly(Side.CLIENT)
	public int getHelmetColor(boolean isOverlay) {
		if (hasHelmet()) {
			ItemStack item = getHelmet();
			return Minecraft.getMinecraft().getItemColors().getColorFromItemstack(item, isOverlay ? 1 : 0);
		}
		return -1;
	}
}