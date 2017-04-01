package vswe.stevescarts.modules.realtimers;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import vswe.stevescarts.entitys.EntityMinecartModular;
import vswe.stevescarts.modules.ModuleBase;

public class ModuleCleaner extends ModuleBase {
	public ModuleCleaner(final EntityMinecartModular cart) {
		super(cart);
	}

	@Override
	public void update() {
		super.update();
		if (this.getCart().world.isRemote) {
			return;
		}
		if (this.getCart().hasFuel()) {
			this.suck();
		}
		this.clean();
	}

	private double calculatemotion(final double dif) {
		if (dif > -0.5 && dif < 0.5) {
			return 0.0;
		}
		return 1.0 / (dif * 15.0);
	}

	private void suck() {
		final List<Entity> list = this.getCart().world.getEntitiesWithinAABBExcludingEntity(this.getCart(), this.getCart().getEntityBoundingBox().expand(3.0, 1.0, 3.0));
		for (Entity e : list) {
			if (e instanceof EntityItem) {
				final EntityItem eItem = (EntityItem) e;
				final double difX = this.getCart().posX - eItem.posX;
				final double difY = this.getCart().posY - eItem.posY;
				final double difZ = this.getCart().posZ - eItem.posZ;
				final EntityItem entityItem = eItem;
				entityItem.motionX += this.calculatemotion(difX);
				final EntityItem entityItem2 = eItem;
				entityItem2.motionY += this.calculatemotion(difY);
				final EntityItem entityItem3 = eItem;
				entityItem3.motionZ += this.calculatemotion(difZ);
			}
		}
	}

	private void clean() {
		final List<Entity> list = this.getCart().world.getEntitiesWithinAABBExcludingEntity(this.getCart(), this.getCart().getEntityBoundingBox().expand(1.0, 0.5, 1.0));
		for (int e = 0; e < list.size(); ++e) {
			if (list.get(e) instanceof EntityItem) {
				final EntityItem eItem = (EntityItem) list.get(e);
				if (!eItem.isDead) {
					final int stackSize = eItem.getEntityItem().stackSize;
					this.getCart().addItemToChest(eItem.getEntityItem());
					if (stackSize != eItem.getEntityItem().stackSize) {
						//TODO
						//this.getCart().world.playSoundAtEntity((Entity) this.getCart(), "random.pop", 0.2f, ((this.getCart().rand.nextFloat() - this.getCart().rand.nextFloat()) * 0.7f + 1.0f) * 2.0f);
						if (eItem.getEntityItem().stackSize <= 0) {
							eItem.setDead();
						}
					} else if (this.failPickup(eItem.getEntityItem())) {
						eItem.setDead();
					}
				}
			} else if (list.get(e) instanceof EntityArrow) {
				final EntityArrow eItem2 = (EntityArrow) list.get(e);
				if (Math.pow(eItem2.motionX, 2.0) + Math.pow(eItem2.motionY, 2.0) + Math.pow(eItem2.motionZ, 2.0) < 0.2 && eItem2.arrowShake <= 0 && !eItem2.isDead) {
					eItem2.arrowShake = 3;
					@Nonnull ItemStack iItem = new ItemStack(Items.ARROW, 1);
					this.getCart().addItemToChest(iItem);
					if (iItem.stackSize <= 0) {
						//TODO
						//this.getCart().world.playSound((Entity) this.getCart(), "random.pop", 0.2f, ((this.getCart().rand.nextFloat() - this.getCart().rand.nextFloat()) * 0.7f + 1.0f) * 2.0f);
						eItem2.setDead();
					} else if (this.failPickup(iItem)) {
						eItem2.setDead();
					}
				}
			}
		}
	}

	private boolean failPickup(@Nonnull ItemStack item) {
		final int x = this.normalize(this.getCart().pushZ);
		final int z = this.normalize(this.getCart().pushX);
		if (x == 0 && z == 0) {
			return false;
		}
		if (this.getCart().world.isRemote) {}
		final EntityItem entityitem = new EntityItem(this.getCart().world, this.getCart().posX, this.getCart().posY, this.getCart().posZ, item.copy());
		entityitem.setPickupDelay(35);
		entityitem.motionX = x / 3.0f;
		entityitem.motionY = 0.15000000596046448;
		entityitem.motionZ = z / 3.0f;
		this.getCart().world.spawnEntity(entityitem);
		return true;
	}

	private int normalize(final double val) {
		if (val == 0.0) {
			return 0;
		}
		if (val > 0.0) {
			return 1;
		}
		return -1;
	}
}
