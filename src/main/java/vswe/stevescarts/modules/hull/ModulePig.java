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
		this.oinkTimer = this.getRandomTimer();
	}

	private void oink() {
		//		this.getCart().world.playSoundAtEntity((Entity) this.getCart(), "mob.pig.say", 1.0f, (this.getCart().rand.nextFloat() - this.getCart().rand.nextFloat()) * 0.2f + 1.0f);
	}

	private int getRandomTimer() {
		return this.oinkTimer = this.getCart().rand.nextInt(900) + 300;
	}

	@Override
	public void update() {
		if (this.oinkTimer <= 0) {
			this.oink();
			this.oinkTimer = this.getRandomTimer();
		} else {
			--this.oinkTimer;
		}
	}

	@Nonnull
	@Nonnull
	private ItemStack getHelmet() {
		if (this.getCart().getPassengers().isEmpty()) {
			return ItemStack.EMPTY;
		}
		Entity rider = this.getCart().getPassengers().get(0);
		if (rider != null && rider instanceof EntityLivingBase) {
			return ((EntityLivingBase) rider).getItemStackFromSlot(EntityEquipmentSlot.HEAD);
		}
		return ItemStack.EMPTY;
	}

	public boolean hasHelment() {
		@Nonnull
		ItemStack item = this.getHelmet();
		return !item.isEmpty() && item.getItem() instanceof ItemArmor && ((ItemArmor) item.getItem()).armorType == EntityEquipmentSlot.HEAD;
	}

	@SideOnly(Side.CLIENT)
	public ResourceLocation getHelmetResource(final boolean isOverlay) {
		if (!this.hasHelment()) {
			return null;
		}
		@Nonnull
		ItemStack item = this.getHelmet();
		if (item.isEmpty()) {
			return null;
		}
		return fakeArmorLayer.getArmorResource((Entity) null, item, EntityEquipmentSlot.HEAD, isOverlay ? "overlay" : null);
	}

	@SideOnly(Side.CLIENT)
	public boolean hasHelmetColor(final boolean isOverlay) {
		return this.getHelmetColor(isOverlay) != -1;
	}

	@SideOnly(Side.CLIENT)
	public int getHelmetColor(final boolean isOverlay) {
		if (this.hasHelment()) {
			@Nonnull
			ItemStack item = this.getHelmet();
			return Minecraft.getMinecraft().getItemColors().getColorFromItemstack(item, isOverlay ? 1 : 0);
		}
		return -1;
	}

	@SideOnly(Side.CLIENT)
	public boolean getHelmetMultiRender() {
		if (this.hasHelment()) {
			@Nonnull
			ItemStack item = this.getHelmet();
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
