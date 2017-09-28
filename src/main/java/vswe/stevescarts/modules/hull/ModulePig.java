package vswe.stevescarts.modules.hull;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.layers.LayerBipedArmor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vswe.stevescarts.entitys.EntityMinecartModular;

import javax.annotation.Nonnull;

public class ModulePig extends ModuleHull {
	private int oinkTimer;
	/**
	 * Only used for getting the texture of the armor.
	 */
	private LayerBipedArmor fakeArmorLayer = new LayerBipedArmor(null);

	public ModulePig(final EntityMinecartModular cart) {
		super(cart);
		oinkTimer = getRandomTimer();
	}

	private void oink() {
		//		this.getCart().world.playSoundAtEntity((Entity) this.getCart(), "mob.pig.say", 1.0f, (this.getCart().rand.nextFloat() - this.getCart().rand.nextFloat()) * 0.2f + 1.0f);
	}

	private int getRandomTimer() {
		return oinkTimer = getCart().rand.nextInt(900) + 300;
	}

	@Override
	public void update() {
		if (oinkTimer <= 0) {
			oink();
			oinkTimer = getRandomTimer();
		} else {
			--oinkTimer;
		}
	}

	@Nonnull
	private ItemStack getHelmet() {
		if (getCart().getPassengers().isEmpty()) {
			return ItemStack.EMPTY;
		}
		Entity rider = getCart().getPassengers().get(0);
		if (rider != null && rider instanceof EntityLivingBase) {
			return ((EntityLivingBase) rider).getItemStackFromSlot(EntityEquipmentSlot.HEAD);
		}
		return ItemStack.EMPTY;
	}

	public boolean hasHelment() {
		@Nonnull
		ItemStack item = getHelmet();
		return !item.isEmpty() && item.getItem() instanceof ItemArmor && ((ItemArmor) item.getItem()).armorType == EntityEquipmentSlot.HEAD;
	}

	@SideOnly(Side.CLIENT)
	public ResourceLocation getHelmetResource(final boolean isOverlay) {
		if (!hasHelment()) {
			return null;
		}
		@Nonnull
		ItemStack item = getHelmet();
		if (item.isEmpty()) {
			return null;
		}
		return fakeArmorLayer.getArmorResource((Entity) null, item, EntityEquipmentSlot.HEAD, isOverlay ? "overlay" : null);
	}

	@SideOnly(Side.CLIENT)
	public boolean hasHelmetColor(final boolean isOverlay) {
		return getHelmetColor(isOverlay) != -1;
	}

	@SideOnly(Side.CLIENT)
	public int getHelmetColor(final boolean isOverlay) {
		if (hasHelment()) {
			@Nonnull
			ItemStack item = getHelmet();
			return Minecraft.getMinecraft().getItemColors().colorMultiplier(item, isOverlay ? 1 : 0);
		}
		return -1;
	}

	@SideOnly(Side.CLIENT)
	public boolean getHelmetMultiRender() {
		if (hasHelment()) {
			@Nonnull
			ItemStack item = getHelmet();
			//TODO: Do we need this still
			//return ((ItemArmor) item.getItem()).requiresMultipleRenderPasses();
		}
		return false;
	}

	@Override
	public int getConsumption(final boolean isMoving) {
		if (!isMoving) {
			return super.getConsumption(isMoving);
		}
		return 1;
	}
}
