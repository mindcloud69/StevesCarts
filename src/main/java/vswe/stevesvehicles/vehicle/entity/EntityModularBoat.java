package vswe.stevesvehicles.vehicle.entity;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import vswe.stevesvehicles.buoy.EntityBuoy;
import vswe.stevesvehicles.network.DataReader;
import vswe.stevesvehicles.network.DataWriter;
import vswe.stevesvehicles.network.PacketHandler;
import vswe.stevesvehicles.network.PacketType;
import vswe.stevesvehicles.vehicle.VehicleBase;
import vswe.stevesvehicles.vehicle.VehicleBoat;

public class EntityModularBoat extends EntityBoatBase implements IVehicleEntity {
	private VehicleBase vehicleBase;
	private static final AxisAlignedBB ZERO_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D);

	public EntityModularBoat(World world) {
		super(world);
		this.vehicleBase = new VehicleBoat(this);
	}

	public EntityModularBoat(World world, double x, double y, double z, NBTTagCompound info, String name) {
		super(world, x, y, z);
		this.vehicleBase = new VehicleBoat(this, info, name);
	}

	/**
	 * The normal data watcher is overridden by a special one on the client
	 * side. This is to be able to wait to process data in the beginning. Fixing
	 * the syncing at start up.
	 */
	@SideOnly(Side.CLIENT)
	private void overrideDatawatcher() {
		this.dataManager = new LockableEntityDataManager(this);
	}

	@Override
	protected void entityInit() {
		if (this.world.isRemote && !(dataManager instanceof LockableEntityDataManager)) {
			this.overrideDatawatcher();
		}
		dataManager.register(VehicleBoat.IS_WORKING, false);
		dataManager.register(VehicleBoat.IS_DISANABLED, false);
		super.entityInit();
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tagCompound) {
		super.writeToNBT(tagCompound);
		vehicleBase.writeToNBT(tagCompound);
		return tagCompound;
	}

	@Override
	public void readFromNBT(NBTTagCompound tagCompound) {
		super.readFromNBT(tagCompound);
		vehicleBase.readFromNBT(tagCompound);
	}

	@Override
	public EnumActionResult applyPlayerInteraction(EntityPlayer player, Vec3d vec, EnumHand hand) {
		if (player.isSneaking()) {
			List<EntityBuoy> list = world.getEntitiesWithinAABB(EntityBuoy.class, getEntityBoundingBox().expand(10, 3, 10));
			EntityBuoy closest = null;
			double closestDistance = 0;
			for (EntityBuoy buoy : list) {
				double distance = getDistanceSqToEntity(buoy);
				if (closest == null || distance < closestDistance) {
					closest = buoy;
					closestDistance = distance;
				}
			}
			if (closest != null) {
				moveTo(closest);
			}
			return EnumActionResult.SUCCESS;
		}
		if (!vehicleBase.canInteractWithEntity(player)) {
			return EnumActionResult.PASS;
		} else {
			vehicleBase.onInteractWith(player);
			return EnumActionResult.SUCCESS;
		}
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		vehicleBase.onUpdate();
	}

	@Override
	public void setDead() {
		vehicleBase.preDeath();
		super.setDead();
		vehicleBase.postDeath();
	}

	@Override
	public boolean attackEntityFrom(DamageSource type, float dmg) {
		return vehicleBase.canBeAttacked(type, dmg) && super.attackEntityFrom(type, dmg);
	}

	@Override
	public double getMountedYOffset() {
		float offset = vehicleBase.getMountedYOffset();
		if (offset != 0) {
			return offset;
		}
		return super.getMountedYOffset();
	}

	@Override
	protected ItemStack getBoatItem() {
		return vehicleBase.getVehicleItem();
	}

	private static final double MINIMUM_STEERING_SPEED = 0.01;
	private static final double THROTTLE = 0.1;
	private double targetX;
	private double targetZ;
	private boolean hasTarget;
	private EntityBuoy targetBuoy;
	private int directionMultiplier;
	private boolean hasRider;

	@Override
	protected void handleSteering() {
		Entity riddenByEntity = getControllingPassenger();
		double speed = Math.sqrt(motionX * motionX + motionZ * motionZ);
		if (riddenByEntity != null && riddenByEntity instanceof EntityLivingBase) {
			EntityLivingBase rider = (EntityLivingBase) riddenByEntity;
			double speedDifference = rider.moveForward * THROTTLE;
			if (!hasRider || speed <= MINIMUM_STEERING_SPEED) {
				hasRider = true;
				directionMultiplier = rider.moveForward >= 0 ? 1 : -1;
			}
			speed += speedDifference * directionMultiplier;
			double rotation = -rider.moveStrafing * 0.1;
			if (speed <= MINIMUM_STEERING_SPEED) {
				motionX = 0;
				motionZ = 0;
				rotationYaw += 180 * rotation / Math.PI;
			} else {
				speed *= directionMultiplier;
				rotation *= directionMultiplier;
				double yaw = (rotationYaw - 180) * Math.PI / 180;
				yaw += rotation;
				motionX = Math.cos(yaw) * speed;
				motionZ = Math.sin(yaw) * speed;
				rotationYaw = (float) yaw * 180 / (float) Math.PI - 180;
			}
			// preventRotationUpdate = true;
		} else {
			hasRider = false;
			if (hasTarget) {
				speed += THROTTLE;
				double differenceX = targetX - posX;
				double differenceZ = targetZ - posZ;
				double differenceSquared = differenceX * differenceX + differenceZ * differenceZ;
				if (differenceSquared < 0.5) {
					hasTarget = false;
					if (targetBuoy != null) {
						EntityBuoy next = targetBuoy.getNextBuoy();
						if (next != null) {
							moveTo(next);
						}
					}
				} else {
					double yaw = Math.atan2(differenceZ, differenceX);
					motionX = Math.cos(yaw) * speed;
					motionZ = Math.sin(yaw) * speed;
				}
			}
		}
	}

	private void moveTo(EntityBuoy buoy) {
		double[] target = getTarget(buoy);
		targetX = target[0];
		targetZ = target[1];
		hasTarget = true;
		targetBuoy = buoy;
	}

	private static final double BUOY_DISTANCE = 3;

	public static double[] getTarget(EntityBuoy buoy) {
		EntityBuoy prev = buoy.getPrevBuoy();
		EntityBuoy next = buoy.getNextBuoy();
		if (prev != null && next != null) {
			// TODO test that this algorithm works in both concave and convex
			// setups
			double differenceX = next.posX - prev.posX;
			double differenceZ = next.posZ - prev.posZ;
			double angle = Math.atan2(differenceZ, differenceX);
			angle += Math.PI / 2;
			return new double[] { buoy.posX + BUOY_DISTANCE * Math.cos(angle), buoy.posZ + BUOY_DISTANCE * Math.sin(angle) };
		} else {
			// TODO what do we do when we don't have a previous and a next buoy?
			return new double[] { buoy.posX + 2, buoy.posZ + 2 };
		}
	}

	/*
	 * Both the server and the client does the steering calculations, just like
	 * with the vanilla boat. The algorithm used for this boat is however much
	 * more dependant on its input values than the vanilla one and will
	 * therefore cause plenty of irregularities if the input values differ
	 * slightly. This will actually be the case which causing differences
	 * between the client and the server. These differences will make the boat
	 * bump into boats when it's not even close and run through boats all
	 * because the server boat is a few blocks over in another direction. The
	 * more you drive the bigger this different might become. If it's big enough
	 * you'll be too far away from your boat when you're actually in it that the
	 * server won't even let you interact with the boat (you can open the
	 * interface but not do anything in it). For the vanilla boat this doesn't
	 * turn into an issue (the steering is more basic and doesn't become a
	 * problem). This is why, when you're riding a vanilla boat, the client
	 * doesn't care about where the server says the boat is. If one would start
	 * listening to this (when the client and server calculates the steering
	 * differently) the client boat would jump into place from time to time
	 * (when it receives server info) which would make the steering very hacky
	 * and therefore the experience very bad since one can't go where one wants
	 * to. One could implement smooth transitions between the values which does
	 * help a bit, but the issues are still there even if they are graphically
	 * smoother. Making them graphically smoother also makes them slower to
	 * update to real values and therefore making any future values more wrong.
	 * That means that the next update has an even bigger problem to try to
	 * smoothly fix. One could tell the boats to only be controlled on the
	 * server side, which removes this issue. However, this makes the steering
	 * constantly hacky due to the fact that the server isn't constantly sending
	 * data to the client. Making it constantly doing so wouldn't be perfect
	 * either, the received data will always be delayed and if it is delayed a
	 * few ticks it becomes impossible to control since what you do might happen
	 * a second later. Also, if lag, or any other reason, causes the packets to
	 * not be received frequent enough the boat will just be stuck in on place
	 * waiting for the next packet. If the boats update client side too they can
	 * fill in for the missing information while waiting for a new packet (this
	 * is how it's done in most cases). Due to the problems described above the
	 * steering has been solved in a slightly ugly way. However, this gives the
	 * player a smooth steering experience and keeps the server and client
	 * synced. The client with the player controlling the boat has almost full
	 * control of the boat. That client periodically sends information about
	 * where the boat is. The server then obeys and moves the boat to that
	 * location. Both the client and the server runs the steering algorithm to
	 * keep them in about the same location between syncs. When the server
	 * receives information it makes sure that the information is not plain
	 * stupid. This is to prevent the client to send invalid data and cause the
	 * boat to run through objects.
	 */
	private static final double MAX_MOVEMENT = 1.5;
	private static final double MAX_SPEED = 0.5;
	private static final int SYNC_DELAY = 30;
	private static final int ALLOWED_SYNC_DELAY_JITTER = 5;
	private int delay = 0;

	@Override
	public void updatePassenger(Entity passenger) {
		super.updatePassenger(passenger);
		if (isPassenger(passenger)) {
			if (world.isRemote) {
				if (delay < SYNC_DELAY) {
					delay++;
				} else {
					delay = 0;
					DataWriter dw = PacketHandler.getDataWriter(PacketType.BOAT_MOVEMENT);
					dw.writeInteger(Float.floatToIntBits((float) posX));
					dw.writeInteger(Float.floatToIntBits((float) posY));
					dw.writeInteger(Float.floatToIntBits((float) posZ));
					dw.writeInteger(Float.floatToIntBits(rotationYaw));
					dw.writeInteger(Float.floatToIntBits((float) motionX));
					dw.writeInteger(Float.floatToIntBits((float) motionY));
					dw.writeInteger(Float.floatToIntBits((float) motionZ));
					PacketHandler.sendPacketToServer(dw);
				}
			} else {
				delay++;
			}
		}
	}

	public void onMovementPacket(DataReader dr) {
		if (delay >= SYNC_DELAY - ALLOWED_SYNC_DELAY_JITTER) {
			delay = 0;
			double tempX = Float.intBitsToFloat(dr.readSignedInteger());
			double tempY = Float.intBitsToFloat(dr.readSignedInteger());
			double tempZ = Float.intBitsToFloat(dr.readSignedInteger());
			tempX = restrictMovement(tempX, posX, MAX_MOVEMENT);
			tempY = restrictMovement(tempY, posY, MAX_MOVEMENT);
			tempZ = restrictMovement(tempZ, posZ, MAX_MOVEMENT);
			move(MoverType.SELF, tempX - posX, tempY - posY, tempZ - posZ);
			rotationYaw = Float.intBitsToFloat(dr.readSignedInteger());
			motionX = restrictMovement(Float.intBitsToFloat(dr.readSignedInteger()), motionX, MAX_SPEED);
			motionY = restrictMovement(Float.intBitsToFloat(dr.readSignedInteger()), motionY, MAX_SPEED);
			motionZ = restrictMovement(Float.intBitsToFloat(dr.readSignedInteger()), motionZ, MAX_SPEED);
		}
	}

	private double restrictMovement(double target, double source, double maxDifference) {
		if (target > source + maxDifference) {
			return source + maxDifference;
		} else if (target < source - maxDifference) {
			return source - maxDifference;
		} else {
			return target;
		}
	}

	@Override
	protected boolean hasCrashed(double horizontalSpeed) {
		return false;
	}

	// Inventory, Tank and a few other methods. These methods are required due
	// to the implementing interfaces, but all logic is handled in the
	// VehicleBase.
	@Override
	public void markDirty() {
		vehicleBase.onInventoryUpdate();
	}

	@Override
	public boolean isUsableByPlayer(EntityPlayer player) {
		return player.getDistanceSq(posX, posY, posZ) <= 64D;
	}

	@Override
	public int getSizeInventory() {
		return vehicleBase.getInventorySize();
	}

	@Override
	public ItemStack getStackInSlot(int id) {
		return vehicleBase.getStack(id);
	}

	@Override
	public void setInventorySlotContents(int id, ItemStack item) {
		vehicleBase.setStack(id, item);
	}

	@Override
	public boolean isEmpty() {
		return vehicleBase.isEmpty();
	}

	@Override
	public ItemStack decrStackSize(int id, int count) {
		return vehicleBase.decreaseStack(id, count);
	}

	@Override
	public ItemStack removeStackFromSlot(int index) {
		return vehicleBase.removeStackFromSlot(index);
	}

	@Override
	public void openInventory(EntityPlayer player) {
		vehicleBase.openInventory(player);
	}

	@Override
	public void closeInventory(EntityPlayer player) {
		vehicleBase.closeInventory(player);
	}

	@Override
	public boolean isItemValidForSlot(int id, ItemStack item) {
		return vehicleBase.isItemValid(id, item);
	}

	@Override
	public int getInventoryStackLimit() {
		return vehicleBase.getInventoryStackLimit();
	}

	@Override
	public String getName() {
		return vehicleBase.getName();
	}

	@Override
	public boolean hasCustomName() {
		return false;
	}

	@Override
	public int fill(FluidStack resource, boolean doFill) {
		return vehicleBase.fill(resource, doFill);
	}

	@Override
	public FluidStack drain(int maxDrain, boolean doDrain) {
		return vehicleBase.drain(maxDrain, doDrain);
	}

	@Override
	public FluidStack drain(FluidStack resource, boolean doDrain) {
		return vehicleBase.drain(resource, doDrain);
	}

	@Override
	public IFluidTankProperties[] getTankProperties() {
		return vehicleBase.getTankProperties();
	}

	@Override
	public void writeSpawnData(ByteBuf data) {
		vehicleBase.writeSpawnData(data);
	}

	@Override
	public void readSpawnData(ByteBuf data) {
		vehicleBase.readSpawnData(data);
	}

	@Override
	public AxisAlignedBB getEntityBoundingBox() {
		return vehicleBase == null || vehicleBase.isPlaceholder ? ZERO_AABB : super.getEntityBoundingBox();
	}

	@Override
	public boolean canBeCollidedWith() {
		return !vehicleBase.isPlaceholder && super.canBeCollidedWith();
	}

	@Override
	public boolean canBePushed() {
		return !vehicleBase.isPlaceholder && super.canBePushed();
	}

	@Override
	public boolean canRiderInteract() {
		return true;
	}

	@Override
	public VehicleBase getVehicle() {
		return vehicleBase;
	}

	@Override
	public int getField(int id) {
		return 0;
	}

	@Override
	public void setField(int id, int value) {
	}

	@Override
	public int getFieldCount() {
		return 0;
	}

	@Override
	public void clear() {
	}
}
