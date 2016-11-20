package vswe.stevesvehicles.vehicle.entity;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityWaterMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.play.client.CPacketSteerBoat;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class EntityBoatBase extends EntityBoat { // The only reason
	// this extends
	// EntityBoat is for
	// vanilla and mods
	// to actually think
	// these are boats
	/** true if no player in boat */
	private static final DataParameter<Integer> TIME_SINCE_HIT = EntityDataManager.<Integer> createKey(EntityBoat.class, DataSerializers.VARINT);
	private static final DataParameter<Integer> FORWARD_DIRECTION = EntityDataManager.<Integer> createKey(EntityBoat.class, DataSerializers.VARINT);
	private static final DataParameter<Float> DAMAGE_TAKEN = EntityDataManager.<Float> createKey(EntityBoat.class, DataSerializers.FLOAT);
	private static final DataParameter<Integer> BOAT_TYPE = EntityDataManager.<Integer> createKey(EntityBoat.class, DataSerializers.VARINT);
	private static final DataParameter<Boolean>[] DATA_ID_PADDLE = new DataParameter[] { EntityDataManager.createKey(EntityBoat.class, DataSerializers.BOOLEAN), EntityDataManager.createKey(EntityBoat.class, DataSerializers.BOOLEAN) };
	private final float[] paddlePositions;
	/** How much of current speed to retain. Value zero to one. */
	private float momentum;
	private float outOfControlTicks;
	private float deltaRotation;
	private int lerpSteps;
	private double boatPitch;
	private double lerpY;
	private double lerpZ;
	private double boatYaw;
	private double lerpXRot;
	private boolean leftInputDown;
	private boolean rightInputDown;
	private boolean forwardInputDown;
	private boolean backInputDown;
	private double waterLevel;
	/**
	 * How much the boat should glide given the slippery blocks it's currently
	 * gliding over. Halved every tick.
	 */
	private float boatGlide;
	private EntityBoat.Status status;
	private EntityBoat.Status previousStatus;
	private double lastYd;

	public EntityBoatBase(World world) {
		super(world);
		this.paddlePositions = new float[2];
		this.preventEntitySpawning = true;
		this.setSize(1.375F, 0.5625F);
	}

	public EntityBoatBase(World world, double x, double y, double z) {
		this(world);
		this.setPosition(x, y, z);
		this.motionX = 0.0D;
		this.motionY = 0.0D;
		this.motionZ = 0.0D;
		this.prevPosX = x;
		this.prevPosY = y;
		this.prevPosZ = z;
	}

	@Override
	public double getYOffset() {
		return height / 2;
	}

	@Override
	protected boolean canTriggerWalking() {
		return false;
	}

	@Override
	protected void entityInit() {
		this.dataManager.register(TIME_SINCE_HIT, Integer.valueOf(0));
		this.dataManager.register(FORWARD_DIRECTION, Integer.valueOf(1));
		this.dataManager.register(DAMAGE_TAKEN, Float.valueOf(0.0F));
		this.dataManager.register(BOAT_TYPE, Integer.valueOf(EntityBoat.Type.OAK.ordinal()));
		for (DataParameter<Boolean> dataparameter : DATA_ID_PADDLE) {
			this.dataManager.register(dataparameter, Boolean.valueOf(false));
		}
	}

	@Override
	public AxisAlignedBB getCollisionBox(Entity entity) {
		return entity.getEntityBoundingBox();
	}

	@Override
	public boolean canBePushed() {
		return true;
	}

	@Override
	public double getMountedYOffset() {
		return /* 0.3 */ -0.1D;
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float amount) {
		if (this.isEntityInvulnerable(source)) {
			return false;
		} else if (!this.world.isRemote && !this.isDead) {
			if (source instanceof EntityDamageSourceIndirect && source.getEntity() != null && this.isPassenger(source.getEntity())) {
				return false;
			} else {
				this.setForwardDirection(-this.getForwardDirection());
				this.setTimeSinceHit(10);
				this.setDamageTaken(this.getDamageTaken() + amount * 10.0F);
				this.setBeenAttacked();
				boolean flag = source.getEntity() instanceof EntityPlayer && ((EntityPlayer) source.getEntity()).capabilities.isCreativeMode;
				if (flag || this.getDamageTaken() > 40.0F) {
					if (!flag && this.world.getGameRules().getBoolean("doEntityDrops")) {
						this.dropItemWithOffset(this.getItemBoat(), 1, 0.0F);
					}
					this.setDead();
				}
				return true;
			}
		} else {
			return true;
		}
	}
	/*
	 * @Override public boolean attackEntityFrom(DamageSource type, float dmg) {
	 * if (isEntityInvulnerable(type)) { return false; } Entity passenger =
	 * getPassengers().get(0); if (!worldObj.isRemote && !isDead) {
	 * setForwardDirection(-getForwardDirection()); setTimeSinceHit(10);
	 * setDamageTaken(getDamageTaken() + dmg * 10.0F); setBeenAttacked();
	 * boolean creative = type.getEntity() instanceof EntityPlayer &&
	 * ((EntityPlayer) type.getEntity()).capabilities.isCreativeMode; if
	 * (creative || this.getDamageTaken() > 40.0F) { if (passenger != null) {
	 * passenger.mountEntity(null); } setDead(); } } return true; }
	 */

	/*
	 * @Override public void applyEntityCollision(Entity other) { if (!(other
	 * instanceof EntityBoat)) { super.applyEntityCollision(other); } else if
	 * (other.riddenByEntity != this && other.ridingEntity != this) { double
	 * differenceX = other.posX - this.posX; double differenceZ = other.posZ -
	 * this.posZ; double difference = MathHelper.abs_max(differenceX,
	 * differenceZ); // System.out.println(difference + " " +
	 * worldObj.isRemote); if (difference >= 0.01) { difference =
	 * MathHelper.sqrt_double(difference); differenceX /= difference;
	 * differenceZ /= difference; double inverted = 1 / difference; if (inverted
	 * > 1) { inverted = 1; } differenceX *= inverted; differenceZ *= inverted;
	 * differenceX *= 0.05; differenceZ *= 0.05; differenceX *= 1 -
	 * this.entityCollisionReduction; differenceZ *= 1 -
	 * this.entityCollisionReduction; this.addVelocity(-differenceX, 0D,
	 * -differenceZ); other.addVelocity(differenceX, 0D, differenceZ); } } }
	 */
	@Override
	public void applyEntityCollision(Entity entityIn) {
		if (entityIn instanceof EntityBoat) {
			if (entityIn.getEntityBoundingBox().minY < this.getEntityBoundingBox().maxY) {
				super.applyEntityCollision(entityIn);
			}
		} else if (entityIn.getEntityBoundingBox().minY <= this.getEntityBoundingBox().minY) {
			super.applyEntityCollision(entityIn);
		}
	}

	protected abstract ItemStack getBoatItem();

	@Override
	public ItemStack getPickedResult(RayTraceResult target) {
		return getBoatItem();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void performHurtAnimation() {
		setForwardDirection(-getForwardDirection());
		setTimeSinceHit(10);
		setDamageTaken(getDamageTaken() * 11.0F);
	}

	@Override
	public boolean canBeCollidedWith() {
		return !isDead;
	}

	/*
	 * @SideOnly(Side.CLIENT)
	 * @Override public void setPositionAndRotation2(double x, double y, double
	 * z, float yaw, float pitch, int tick) { if (isBoatEmpty) {
	 * this.boatPosRotationIncrements = tick + 5; } else { double distanceX = x
	 * - this.posX; double distanceY = y - this.posY; double distanceZ = z -
	 * this.posZ; double distance = distanceX * distanceX + distanceY *
	 * distanceY + distanceZ * distanceZ; if (distance <= 1) { return; }
	 * boatPosRotationIncrements = 3; } this.boatX = x; this.boatY = y;
	 * this.boatZ = z; this.boatYaw = yaw; this.boatPitch = pitch; this.motionX
	 * = this.velocityX; this.motionY = this.velocityY; this.motionZ =
	 * this.velocityZ; }
	 */
	@Override
	@SideOnly(Side.CLIENT)
	public void setPositionAndRotationDirect(double x, double y, double z, float yaw, float pitch, int posRotationIncrements, boolean teleport) {
		this.boatPitch = x;
		this.lerpY = y;
		this.lerpZ = z;
		this.boatYaw = yaw;
		this.lerpXRot = pitch;
		this.lerpSteps = 10;
	}

	@Override
	public EnumFacing getAdjustedHorizontalFacing() {
		return this.getHorizontalFacing().rotateY();
	}

	/*
	 * @SideOnly(Side.CLIENT)
	 * @Override public void setVelocity(double velocityX, double velocityY,
	 * double velocityZ) { this.velocityX = this.motionX = velocityX;
	 * this.velocityY = this.motionY = velocityY; this.velocityZ = this.motionZ
	 * = velocityZ; }
	 */
	private static final int COLLISION_SLICES = 5;
	private static final double MAX_SPEED = 0.35; // depending on the throttle
	// one can give in the
	// handleSteering method
	// this speed might not
	// actually be achievable
	private static final double MAX_YAW_SPEED = 20;

	@Override
	public void onUpdate() {
		this.previousStatus = this.status;
		this.status = this.getBoatStatus();
		if (this.status != EntityBoat.Status.UNDER_WATER && this.status != EntityBoat.Status.UNDER_FLOWING_WATER) {
			this.outOfControlTicks = 0.0F;
		} else {
			++this.outOfControlTicks;
		}
		if (!this.world.isRemote && this.outOfControlTicks >= 60.0F) {
			this.removePassengers();
		}
		if (this.getTimeSinceHit() > 0) {
			this.setTimeSinceHit(this.getTimeSinceHit() - 1);
		}
		if (this.getDamageTaken() > 0.0F) {
			this.setDamageTaken(this.getDamageTaken() - 1.0F);
		}
		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;
		superSuperOnUpdate();
		// this.tickLerp();
		handleRotation();
		handleMovement();
		handlePaddlePosition();
		this.doBlockCollisions();
		handleEntityInteraction();
		handleSteering();
		// handleSpeedLimits();
		updateRiderBoat();
	}

	private EntityBoat.Status getBoatStatus() {
		EntityBoat.Status entityboat$status = this.getUnderwaterStatus();
		if (entityboat$status != null) {
			this.waterLevel = this.getEntityBoundingBox().maxY;
			return entityboat$status;
		} else if (this.checkInWater()) {
			return EntityBoat.Status.IN_WATER;
		} else {
			float f = this.getBoatGlide();
			if (f > 0.0F) {
				this.boatGlide = f;
				return EntityBoat.Status.ON_LAND;
			} else {
				return EntityBoat.Status.IN_AIR;
			}
		}
	}

	private boolean checkInWater() {
		AxisAlignedBB axisalignedbb = this.getEntityBoundingBox();
		int i = MathHelper.floor(axisalignedbb.minX);
		int j = MathHelper.ceil(axisalignedbb.maxX);
		int k = MathHelper.floor(axisalignedbb.minY);
		int l = MathHelper.ceil(axisalignedbb.minY + 0.001D);
		int i1 = MathHelper.floor(axisalignedbb.minZ);
		int j1 = MathHelper.ceil(axisalignedbb.maxZ);
		boolean flag = false;
		this.waterLevel = Double.MIN_VALUE;
		BlockPos.PooledMutableBlockPos blockpos$pooledmutableblockpos = BlockPos.PooledMutableBlockPos.retain();
		try {
			for (int k1 = i; k1 < j; ++k1) {
				for (int l1 = k; l1 < l; ++l1) {
					for (int i2 = i1; i2 < j1; ++i2) {
						blockpos$pooledmutableblockpos.setPos(k1, l1, i2);
						IBlockState iblockstate = this.world.getBlockState(blockpos$pooledmutableblockpos);
						if (iblockstate.getMaterial() == Material.WATER) {
							float f = getLiquidHeight(iblockstate, this.world, blockpos$pooledmutableblockpos);
							this.waterLevel = Math.max(f, this.waterLevel);
							flag |= axisalignedbb.minY < f;
						}
					}
				}
			}
		} finally {
			blockpos$pooledmutableblockpos.release();
		}
		return flag;
	}

	/**
	 * Decides whether the boat is currently underwater.
	 */
	@Nullable
	private EntityBoat.Status getUnderwaterStatus() {
		AxisAlignedBB axisalignedbb = this.getEntityBoundingBox();
		double d0 = axisalignedbb.maxY + 0.001D;
		int i = MathHelper.floor(axisalignedbb.minX);
		int j = MathHelper.ceil(axisalignedbb.maxX);
		int k = MathHelper.floor(axisalignedbb.maxY);
		int l = MathHelper.ceil(d0);
		int i1 = MathHelper.floor(axisalignedbb.minZ);
		int j1 = MathHelper.ceil(axisalignedbb.maxZ);
		boolean flag = false;
		BlockPos.PooledMutableBlockPos blockpos$pooledmutableblockpos = BlockPos.PooledMutableBlockPos.retain();
		try {
			for (int k1 = i; k1 < j; ++k1) {
				for (int l1 = k; l1 < l; ++l1) {
					for (int i2 = i1; i2 < j1; ++i2) {
						blockpos$pooledmutableblockpos.setPos(k1, l1, i2);
						IBlockState iblockstate = this.world.getBlockState(blockpos$pooledmutableblockpos);
						if (iblockstate.getMaterial() == Material.WATER && d0 < getLiquidHeight(iblockstate, this.world, blockpos$pooledmutableblockpos)) {
							if (iblockstate.getValue(BlockLiquid.LEVEL).intValue() != 0) {
								EntityBoat.Status entityboat$status = EntityBoat.Status.UNDER_FLOWING_WATER;
								return entityboat$status;
							}
							flag = true;
						}
					}
				}
			}
		} finally {
			blockpos$pooledmutableblockpos.release();
		}
		return flag ? EntityBoat.Status.UNDER_WATER : null;
	}

	public static float getBlockLiquidHeight(IBlockState p_184456_0_, IBlockAccess p_184456_1_, BlockPos p_184456_2_) {
		int i = p_184456_0_.getValue(BlockLiquid.LEVEL).intValue();
		return (i & 7) == 0 && p_184456_1_.getBlockState(p_184456_2_.up()).getMaterial() == Material.WATER ? 1.0F : 1.0F - BlockLiquid.getLiquidHeightPercent(i);
	}

	public static float getLiquidHeight(IBlockState p_184452_0_, IBlockAccess p_184452_1_, BlockPos p_184452_2_) {
		return p_184452_2_.getY() + getBlockLiquidHeight(p_184452_0_, p_184452_1_, p_184452_2_);
	}

	/*
	 * @Override public void onUpdate() { superSuperOnUpdate(); if
	 * (getTimeSinceHit() > 0) { setTimeSinceHit(getTimeSinceHit() - 1); } if
	 * (getDamageTaken() > 0.0F) { this.setDamageTaken(getDamageTaken() - 1.0F);
	 * } prevPosX = posX; prevPosY = posY; prevPosZ = posZ; double
	 * horizontalSpeed = Math.sqrt(this.motionX * this.motionX + this.motionZ *
	 * this.motionZ); spawnParticles(horizontalSpeed); if (useSimpleUpdate()) {
	 * updateClientSoloBoat(); } else { handleFloating(); handleSteering();
	 * handleSpeedLimits(); handleBlockRemoval();
	 * handleMovement(horizontalSpeed); handleRotation();
	 * handleEntityInteraction(); updateRiderBoat(); } } protected boolean
	 * useSimpleUpdate() { return worldObj.isRemote && isBoatEmpty; }
	 */
	private static final double BOUNDING_BOX_EXPANSION = 0.8;

	protected void handleEntityInteraction() {
		/*
		 * if (!worldObj.isRemote) { Entity riddenByEntity =
		 * getControllingPassenger(); List list =
		 * worldObj.getEntitiesWithinAABB(EntityBoat.class,
		 * getEntityBoundingBox().expand(BOUNDING_BOX_EXPANSION, 0,
		 * BOUNDING_BOX_EXPANSION)); if (list != null && !list.isEmpty()) { for
		 * (Object obj : list) { Entity entity = (Entity) obj; if (entity !=
		 * riddenByEntity && entity.canBePushed() && entity != this) {
		 * entity.applyEntityCollision(this); } } } if (riddenByEntity != null
		 * && riddenByEntity.isDead) { riddenByEntity = null; } }
		 */
		List<Entity> list = this.world.getEntitiesInAABBexcluding(this, this.getEntityBoundingBox().expand(0.20000000298023224D, -0.009999999776482582D, 0.20000000298023224D), EntitySelectors.<Entity> getTeamCollisionPredicate(this));
		if (!list.isEmpty()) {
			boolean flag = !this.world.isRemote && !(this.getControllingPassenger() instanceof EntityPlayer);
			for (int j = 0; j < list.size(); ++j) {
				Entity entity = list.get(j);
				if (!entity.isPassenger(this)) {
					if (flag && this.getPassengers().size() < 2 && !entity.isRiding() && entity.width < this.width && entity instanceof EntityLivingBase && !(entity instanceof EntityWaterMob) && !(entity instanceof EntityPlayer)) {
						entity.startRiding(this);
					} else {
						this.applyEntityCollision(entity);
					}
				}
			}
		}
	}

	protected void handlePaddlePosition() {
		for (int i = 0; i <= 1; ++i) {
			if (this.getPaddleState(i)) {
				this.paddlePositions[i] = (float) (this.paddlePositions[i] + 0.01D);
			} else {
				this.paddlePositions[i] = 0.0F;
			}
		}
	}

	@Override
	public void setPaddleState(boolean p_184445_1_, boolean p_184445_2_) {
		this.dataManager.set(DATA_ID_PADDLE[0], Boolean.valueOf(p_184445_1_));
		this.dataManager.set(DATA_ID_PADDLE[1], Boolean.valueOf(p_184445_2_));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public float getRowingTime(int p_184448_1_, float limbSwing) {
		return this.getPaddleState(p_184448_1_) ? (float) MathHelper.clampedLerp(this.paddlePositions[p_184448_1_] - 0.01D, this.paddlePositions[p_184448_1_], limbSwing) : 0.0F;
	}

	protected void handleRotation() {
		/*
		 * if (preventRotationUpdate) { preventRotationUpdate = false; } else {
		 * double yaw = this.rotationYaw; double differenceX = prevPosX - posX;
		 * double differenceZ = prevPosZ - posZ; double differenceSquared =
		 * differenceX * differenceX + differenceZ * differenceZ; if
		 * (differenceSquared > 0.001D) { yaw = Math.atan2(differenceZ,
		 * differenceX) * 180 / Math.PI; } double yawDifference =
		 * MathHelper.wrapAngleTo180_double(yaw - rotationYaw); if
		 * (yawDifference > MAX_YAW_SPEED) { yawDifference = MAX_YAW_SPEED; }
		 * else if (yawDifference < -MAX_YAW_SPEED) { yawDifference =
		 * -MAX_YAW_SPEED; } rotationYaw += yawDifference; } rotationPitch = 0;
		 * setRotation(rotationYaw, rotationPitch);
		 */
		if (this.lerpSteps > 0 && !this.canPassengerSteer()) {
			double d0 = this.posX + (this.boatPitch - this.posX) / this.lerpSteps;
			double d1 = this.posY + (this.lerpY - this.posY) / this.lerpSteps;
			double d2 = this.posZ + (this.lerpZ - this.posZ) / this.lerpSteps;
			double d3 = MathHelper.wrapDegrees(this.boatYaw - this.rotationYaw);
			this.rotationYaw = (float) (this.rotationYaw + d3 / this.lerpSteps);
			this.rotationPitch = (float) (this.rotationPitch + (this.lerpXRot - this.rotationPitch) / this.lerpSteps);
			--this.lerpSteps;
			this.setPosition(d0, d1, d2);
			this.setRotation(this.rotationYaw, this.rotationPitch);
		}
	}

	protected void handleMovement() {
		/*
		 * if (onGround) { motionX *= 0.5D; motionY *= 0.5D; motionZ *= 0.5D; }
		 * moveEntity(motionX, motionY, motionZ); if
		 * (hasCrashed(horizontalSpeed)) { if (!worldObj.isRemote && !isDead) {
		 * onCrash(false); } } else { motionX *= 0.95; motionY *= 0.95; motionZ
		 * *= 0.95; }
		 */
		if (this.canPassengerSteer()) {
			if (this.getPassengers().size() == 0 || !(this.getPassengers().get(0) instanceof EntityPlayer)) {
				this.setPaddleState(false, false);
			}
			this.updateMotion();
			if (this.world.isRemote) {
				this.controlBoat();
				this.world.sendPacketToServer(new CPacketSteerBoat(this.getPaddleState(0), this.getPaddleState(1)));
			}
			this.moveEntity(MoverType.SELF, this.motionX, this.motionY, this.motionZ);
		} else {
			this.motionX = 0.0D;
			this.motionY = 0.0D;
			this.motionZ = 0.0D;
		}
	}

	protected void updateMotion() {
		double d0 = -0.03999999910593033D;
		double d1 = this.hasNoGravity() ? 0.0D : -0.03999999910593033D;
		double d2 = 0.0D;
		this.momentum = 0.05F;
		if (this.previousStatus == EntityBoat.Status.IN_AIR && this.status != EntityBoat.Status.IN_AIR && this.status != EntityBoat.Status.ON_LAND) {
			this.waterLevel = this.getEntityBoundingBox().minY + this.height;
			this.setPosition(this.posX, this.getWaterLevelAbove() - this.height + 0.101D, this.posZ);
			this.motionY = 0.0D;
			this.lastYd = 0.0D;
			this.status = EntityBoat.Status.IN_WATER;
		} else {
			if (this.status == EntityBoat.Status.IN_WATER) {
				d2 = (this.waterLevel - this.getEntityBoundingBox().minY) / this.height;
				this.momentum = 0.9F;
			} else if (this.status == EntityBoat.Status.UNDER_FLOWING_WATER) {
				d1 = -7.0E-4D;
				this.momentum = 0.9F;
			} else if (this.status == EntityBoat.Status.UNDER_WATER) {
				d2 = 0.009999999776482582D;
				this.momentum = 0.45F;
			} else if (this.status == EntityBoat.Status.IN_AIR) {
				this.momentum = 0.9F;
			} else if (this.status == EntityBoat.Status.ON_LAND) {
				this.momentum = this.boatGlide;
				if (this.getControllingPassenger() instanceof EntityPlayer) {
					this.boatGlide /= 2.0F;
				}
			}
			this.motionX *= this.momentum;
			this.motionZ *= this.momentum;
			this.deltaRotation *= this.momentum;
			this.motionY += d1;
			if (d2 > 0.0D) {
				double d3 = 0.65D;
				this.motionY += d2 * 0.06153846016296973D;
				double d4 = 0.75D;
				this.motionY *= 0.75D;
			}
		}
	}

	protected void controlBoat() {
		if (this.isBeingRidden()) {
			float f = 0.0F;
			if (this.leftInputDown) {
				this.deltaRotation += -1.0F;
			}
			if (this.rightInputDown) {
				++this.deltaRotation;
			}
			if (this.rightInputDown != this.leftInputDown && !this.forwardInputDown && !this.backInputDown) {
				f += 0.005F;
			}
			this.rotationYaw += this.deltaRotation;
			if (this.forwardInputDown) {
				f += 0.04F;
			}
			if (this.backInputDown) {
				f -= 0.005F;
			}
			this.motionX += MathHelper.sin(-this.rotationYaw * 0.017453292F) * f;
			this.motionZ += MathHelper.cos(this.rotationYaw * 0.017453292F) * f;
			this.setPaddleState(this.rightInputDown || this.forwardInputDown, this.leftInputDown || this.forwardInputDown);
		}
	}

	@Override
	public void updatePassenger(Entity passenger) {
		if (this.isPassenger(passenger)) {
			float f = 0.0F;
			float f1 = (float) ((this.isDead ? 0.009999999776482582D : this.getMountedYOffset()) + passenger.getYOffset());
			if (this.getPassengers().size() > 1) {
				int i = this.getPassengers().indexOf(passenger);
				if (i == 0) {
					f = 0.2F;
				} else {
					f = -0.6F;
				}
				if (passenger instanceof EntityAnimal) {
					f = (float) (f + 0.2D);
				}
			}
			Vec3d vec3d = (new Vec3d(f, 0.0D, 0.0D)).rotateYaw(-this.rotationYaw * 0.017453292F - ((float) Math.PI / 2F));
			passenger.setPosition(this.posX + vec3d.xCoord, this.posY + f1, this.posZ + vec3d.zCoord);
			passenger.rotationYaw += this.deltaRotation;
			passenger.setRotationYawHead(passenger.getRotationYawHead() + this.deltaRotation);
			this.applyYawToEntity(passenger);
			if (passenger instanceof EntityAnimal && this.getPassengers().size() > 1) {
				int j = passenger.getEntityId() % 2 == 0 ? 90 : 270;
				passenger.setRenderYawOffset(((EntityAnimal) passenger).renderYawOffset + j);
				passenger.setRotationYawHead(passenger.getRotationYawHead() + j);
			}
		}
	}

	@Override
	protected void applyYawToEntity(Entity entityToUpdate) {
		entityToUpdate.setRenderYawOffset(this.rotationYaw);
		float f = MathHelper.wrapDegrees(entityToUpdate.rotationYaw - this.rotationYaw);
		float f1 = MathHelper.clamp(f, -105.0F, 105.0F);
		entityToUpdate.prevRotationYaw += f1 - f;
		entityToUpdate.rotationYaw += f1 - f;
		entityToUpdate.setRotationYawHead(entityToUpdate.rotationYaw);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void applyOrientationToEntity(Entity entityToUpdate) {
		this.applyYawToEntity(entityToUpdate);
	}

	@Override
	public boolean processInitialInteract(EntityPlayer player, EnumHand hand) {
		if (!this.world.isRemote && !player.isSneaking() && this.outOfControlTicks < 60.0F) {
			player.startRiding(this);
		}
		return true;
	}

	@Override
	protected void updateFallState(double y, boolean onGroundIn, IBlockState state, BlockPos pos) {
		this.lastYd = this.motionY;
		if (!this.isRiding()) {
			if (onGroundIn) {
				if (this.fallDistance > 3.0F) {
					if (this.status != EntityBoat.Status.ON_LAND) {
						this.fallDistance = 0.0F;
						return;
					}
					this.fall(this.fallDistance, 1.0F);
					if (!this.world.isRemote && !this.isDead) {
						this.setDead();
						if (this.world.getGameRules().getBoolean("doEntityDrops")) {
							for (int i = 0; i < 3; ++i) {
								this.entityDropItem(new ItemStack(Item.getItemFromBlock(Blocks.PLANKS), 1, this.getBoatType().getMetadata()), 0.0F);
							}
							for (int j = 0; j < 2; ++j) {
								this.dropItemWithOffset(Items.STICK, 1, 0.0F);
							}
						}
					}
				}
				this.fallDistance = 0.0F;
			} else if (this.world.getBlockState((new BlockPos(this)).down()).getMaterial() != Material.WATER && y < 0.0D) {
				this.fallDistance = (float) (this.fallDistance - y);
			}
		}
	}

	@Override
	public boolean getPaddleState(int p_184457_1_) {
		return this.dataManager.get(DATA_ID_PADDLE[p_184457_1_]).booleanValue() && this.getControllingPassenger() != null;
	}

	/**
	 * Sets the damage taken from the last hit.
	 */
	@Override
	public void setDamageTaken(float damageTaken) {
		this.dataManager.set(DAMAGE_TAKEN, Float.valueOf(damageTaken));
	}

	/**
	 * Gets the damage taken from the last hit.
	 */
	@Override
	public float getDamageTaken() {
		return this.dataManager.get(DAMAGE_TAKEN).floatValue();
	}

	/**
	 * Sets the time to count down from since the last time entity was hit.
	 */
	@Override
	public void setTimeSinceHit(int timeSinceHit) {
		this.dataManager.set(TIME_SINCE_HIT, Integer.valueOf(timeSinceHit));
	}

	/**
	 * Gets the time since the last hit.
	 */
	@Override
	public int getTimeSinceHit() {
		return this.dataManager.get(TIME_SINCE_HIT).intValue();
	}

	/**
	 * Sets the forward direction of the entity.
	 */
	@Override
	public void setForwardDirection(int forwardDirection) {
		this.dataManager.set(FORWARD_DIRECTION, Integer.valueOf(forwardDirection));
	}

	/**
	 * Gets the forward direction of the entity.
	 */
	@Override
	public int getForwardDirection() {
		return this.dataManager.get(FORWARD_DIRECTION).intValue();
	}

	@Override
	public void setBoatType(EntityBoat.Type boatType) {
		this.dataManager.set(BOAT_TYPE, Integer.valueOf(boatType.ordinal()));
	}

	@Override
	public EntityBoat.Type getBoatType() {
		return EntityBoat.Type.byId(this.dataManager.get(BOAT_TYPE).intValue());
	}

	@Override
	protected boolean canFitPassenger(Entity passenger) {
		return this.getPassengers().size() < 2;
	}

	/**
	 * For vehicles, the first passenger is generally considered the controller
	 * and "drives" the vehicle. For example, Pigs, Horses, and Boats are
	 * generally "steered" by the controlling passenger.
	 */
	@Override
	@Nullable
	public Entity getControllingPassenger() {
		List<Entity> list = this.getPassengers();
		return list.isEmpty() ? null : (Entity) list.get(0);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void updateInputs(boolean p_184442_1_, boolean p_184442_2_, boolean p_184442_3_, boolean p_184442_4_) {
		this.leftInputDown = p_184442_1_;
		this.rightInputDown = p_184442_2_;
		this.forwardInputDown = p_184442_3_;
		this.backInputDown = p_184442_4_;
	}

	protected void onCrash(boolean fall) {
		setDead();
	}

	protected boolean hasCrashed(double horizontalSpeed) {
		return isCollidedHorizontally && horizontalSpeed > 0.2D;
	}

	protected boolean hasFallen(float fallDistance) {
		return fallDistance > 3;
	}
	/*
	 * protected void handleBlockRemoval() { for (int x = -1; x <= 1; x += 2) {
	 * double differenceX = x * 0.4; int targetX = MathHelper.floor_double(posX
	 * + differenceX); for (int y = 0; y <= 2; y += 1) { int targetY =
	 * MathHelper.floor_double(posY) + y; for (int z = -1; z <= 1; z += 2) {
	 * double differenceZ = z * 0.4; int targetZ = MathHelper.floor_double(posZ
	 * + differenceZ); Block block = worldObj.getBlock(targetX, targetY,
	 * targetZ); if (handleBlockRemoval(block, targetX, targetY, targetZ)) {
	 * isCollidedHorizontally = false; } } } } } protected boolean
	 * handleBlockRemoval(Block block, BlockPos pos) { if (block ==
	 * Blocks.SNOW_LAYER) { worldObj.setBlockToAir(pos); return true; } else if
	 * (block == Blocks.WATERLILY) { worldObj.func_147480_a(pos, true); return
	 * true; } else { return false; } }
	 */

	/*
	 * private void handleSpeedLimits() { double horizontalSpeed =
	 * Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ); if
	 * (horizontalSpeed > MAX_SPEED) { double yaw = Math.atan2(motionZ,
	 * motionX); motionX = Math.cos(yaw) * MAX_SPEED; motionZ = Math.sin(yaw) *
	 * MAX_SPEED; } }
	 */
	protected void handleSteering() {
	}

	protected void updateRiderBoat() {
	}

	/**
	 * One can't call a super class' super class' method and since we extend
	 * EntityBoat simply to make this a boat we don't want to actually trigger
	 * its code. Fortunately the only thing the Entity's onUpdate does is to
	 * call onEntityUpdate, this method is a copy of Entity's onUpdate.
	 */
	private void superSuperOnUpdate() {
		onEntityUpdate();
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound par1NBTTagCompound) {
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound par1NBTTagCompound) {
	}

	@Override
	public EnumActionResult applyPlayerInteraction(EntityPlayer player, Vec3d vec, EnumHand hand) {
		Entity riddenByEntity = getControllingPassenger();
		if (riddenByEntity != null && riddenByEntity instanceof EntityPlayer && riddenByEntity != player) {
			return EnumActionResult.SUCCESS;
		} else {
			if (!world.isRemote) {
				player.startRiding(this);
			}
			return EnumActionResult.SUCCESS;
		}
	}
}
