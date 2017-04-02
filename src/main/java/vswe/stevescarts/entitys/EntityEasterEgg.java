package vswe.stevescarts.entitys;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.projectile.EntityEgg;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import vswe.stevescarts.helpers.GiftItem;

import javax.annotation.Nonnull;
import java.util.ArrayList;

public class EntityEasterEgg extends EntityEgg {
	public EntityEasterEgg(final World world) {
		super(world);
	}

	public EntityEasterEgg(final World world, final EntityLivingBase thrower) {
		super(world, thrower);
	}

	public EntityEasterEgg(final World world, final double x, final double y, final double z) {
		super(world, x, y, z);
	}

	@Override
	protected void onImpact(RayTraceResult result) {
		if (result.entityHit != null) {
			result.entityHit.attackEntityFrom(DamageSource.causeThrownDamage(this, this.getThrower()), 0.0f);
		}
		if (!this.world.isRemote) {
			if (this.rand.nextInt(8) == 0) {
				if (this.rand.nextInt(32) == 0) {
					final EntityPig entitypig = new EntityPig(this.world);
					entitypig.setGrowingAge(-24000);
					entitypig.setLocationAndAngles(this.posX, this.posY, this.posZ, this.rotationYaw, 0.0f);
					this.world.spawnEntity(entitypig);
				} else {
					final EntityChicken entitychicken = new EntityChicken(this.world);
					entitychicken.setGrowingAge(-24000);
					entitychicken.setLocationAndAngles(this.posX, this.posY, this.posZ, this.rotationYaw, 0.0f);
					this.world.spawnEntity(entitychicken);
				}
			} else {
				final ArrayList<ItemStack> items = GiftItem.generateItems(this.rand, GiftItem.EasterList, 25 + this.rand.nextInt(300), 1);
				for (
					@Nonnull
						ItemStack item : items) {
					final EntityItem eItem = new EntityItem(this.world, this.posX, this.posY, this.posZ, item);
					eItem.motionX = this.rand.nextGaussian() * 0.05000000074505806;
					eItem.motionY = this.rand.nextGaussian() * 0.25;
					eItem.motionZ = this.rand.nextGaussian() * 0.05000000074505806;
					this.world.spawnEntity(eItem);
				}
			}
		}
		for (int j = 0; j < 8; ++j) {
			this.world.spawnParticle(EnumParticleTypes.SNOWBALL, this.posX, this.posY, this.posZ, 0.0, 0.0, 0.0);
		}
		if (!this.world.isRemote) {
			this.setDead();
		}
	}
}
