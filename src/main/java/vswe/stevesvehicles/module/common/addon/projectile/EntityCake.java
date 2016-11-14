package vswe.stevesvehicles.module.common.addon.projectile;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityEgg;
import net.minecraft.init.Blocks;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class EntityCake extends EntityEgg {
	public EntityCake(World world) {
		super(world);
	}

	public EntityCake(World world, EntityLiving thrower) {
		super(world, thrower);
	}

	public EntityCake(World world, double x, double y, double z) {
		super(world, x, y, z);
	}

	@Override
	protected void onImpact(RayTraceResult data) {
		if (data.entityHit != null) {
			data.entityHit.attackEntityFrom(DamageSource.causeThrownDamage(this, getThrower()), 0);
			if (data.entityHit instanceof EntityPlayer) {
				EntityPlayer player = (EntityPlayer) data.entityHit;
				player.getFoodStats().addStats(14, 0.7F);
			}
		} else {
			BlockPos pos = getPosition();
			if (worldObj.isAirBlock(pos) && worldObj.isSideSolid(pos.down(), EnumFacing.UP)) {
				worldObj.setBlockState(pos, Blocks.CAKE.getDefaultState());
			}
		}
		for (int i = 0; i < 8; i++) {
			// noinspection SpellCheckingInspection
			worldObj.spawnParticle(EnumParticleTypes.SNOWBALL, posX, posY, posZ, 0.0D, 0.0D, 0.0D);
		}
		if (!worldObj.isRemote) {
			setDead();
		}
	}
}
