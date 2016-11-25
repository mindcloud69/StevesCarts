package stevesvehicles.common.vehicles.entitys;

import net.minecraft.block.BlockRailBase;
import net.minecraft.block.BlockRailBase.EnumRailDirection;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.MovingSound;
import net.minecraft.client.audio.MovingSoundMinecart;
import net.minecraft.client.audio.MovingSoundMinecartRiding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.minecart.MinecartInteractEvent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import stevesvehicles.common.blocks.ModBlocks;
import stevesvehicles.common.blocks.tileentitys.detector.DetectorType;
import stevesvehicles.common.modules.ModuleBase;
import stevesvehicles.common.vehicles.VehicleBase;
import stevesvehicles.common.vehicles.VehicleCart;
import io.netty.buffer.ByteBuf;

/**
 * The modular minecart class, this is the cart. This is what controls all
 * modules whereas the modules({@link ModuleBase} handles specific tasks. The
 * cart the player can see in-game is a combination of the cart and its modules.
 * 
 * @author Vswe
 */
public class EntityModularCart extends EntityMinecart implements IVehicleEntity {
	private VehicleBase vehicleBase;
	public BlockPos disabledPos;
	protected boolean wasDisabled;
	public double pushX;
	public double pushZ;
	public double temppushX;
	public double temppushZ;
	public boolean cornerFlip;
	private EnumRailDirection fixedRailDirection;
	private BlockPos fixedRailPos;
	private int wrongRender;
	private boolean oldRender;
	private float lastRenderYaw;
	private double lastMotionX;
	private double lastMotionZ;
	private static final AxisAlignedBB ZERO_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D);
	/**
	 * Information about how trails turn
	 */
	public static final int[][][] railDirectionCoordinates = new int[][][] { { { 0, 0, -1 }, { 0, 0, 1 } }, { { -1, 0, 0 }, { 1, 0, 0 } }, { { -1, -1, 0 }, { 1, 0, 0 } }, { { -1, 0, 0 }, { 1, -1, 0 } }, { { 0, 0, -1 }, { 0, -1, 1 } },
		{ { 0, -1, -1 }, { 0, 0, 1 } }, { { 0, 0, 1 }, { 1, 0, 0 } }, { { 0, 0, 1 }, { -1, 0, 0 } }, { { 0, 0, -1 }, { -1, 0, 0 } }, { { 0, 0, -1 }, { 1, 0, 0 } } };

		@Override
		public VehicleBase getVehicle() {
			return vehicleBase;
		}

		/**
		 * Creates a cart in the world, this is used when the cart is spawned by a
		 * player(or something else) on the server side.
		 * 
		 * @param world
		 *            The world the cart is placed in
		 * @param x
		 *            The X coordinate the cart is placed at
		 * @param y
		 *            The Y coordinate the cart is placed at
		 * @param z
		 *            The Z coordinate the cart is placed at
		 * @param info
		 *            The tag compound with mostly the cart's modules.
		 * @param name
		 *            The name of this cart
		 */
		public EntityModularCart(World world, double x, double y, double z, NBTTagCompound info, String name) {
			super(world, x, y, z);
			this.vehicleBase = new VehicleCart(this, info, name);
		}

		/**
		 * Creates a cart in the world. This is used when a cart is loaded on the
		 * server, or when a cart is created on the client.
		 * 
		 * @param world
		 *            The world the cart is created in
		 */
		public EntityModularCart(World world) {
			super(world);
			this.vehicleBase = new VehicleCart(this);
		}

		/**
		 * The normal datawatcher is overridden by a special one on the client side.
		 * This is to be able to wait to process data in the beginning. Fixing the
		 * syncing at start up.
		 */
		@SideOnly(Side.CLIENT)
		private void overrideDatawatcher() {
			this.dataManager = new LockableEntityDataManager(this);
		}

		/**
		 * Is called when the Entity is killed
		 */
		@Override
		public void setDead() {
			vehicleBase.preDeath();
			super.setDead();
			vehicleBase.postDeath();
		}

		/**
		 * Initiates the entity, used for initiating data watchers.
		 */
		@Override
		protected void entityInit() {
			if (this.world.isRemote && !(dataManager instanceof LockableEntityDataManager)) {
				this.overrideDatawatcher();
			}
			dataManager.register(VehicleCart.IS_WORKING, false);
			dataManager.register(VehicleCart.IS_DISANABLED, false);
			super.entityInit();
		}

		/**
		 * Get the "Eye height" of the cart
		 */
		@Override
		public float getEyeHeight() {
			return 0.9F;
		}

		@Override
		protected void setSize(float width, float height) {
			if (vehicleBase == null || vehicleBase.isPlaceholder) {
				return;
			}
			super.setSize(width, height);
		}

		/**
		 * Get the offset the riding entity should be rendered at
		 */
		@Override
		public double getMountedYOffset() {
			float offset = vehicleBase.getMountedYOffset();
			if (offset != 0) {
				return offset;
			}
			return super.getMountedYOffset();
		}

		/**
		 * Get this cart as an item. Used when breaking the cart or "picking"(middle
		 * mouse button) it for instance.
		 */
		@Override
		public ItemStack getCartItem() {
			return vehicleBase.getVehicleItem();
		}

		@Override
		public void killMinecart(DamageSource dmg) {
			this.setDead();
		}

		/**
		 * Get the max speed this cart can move at
		 */
		@Override
		public float getMaxCartSpeedOnRail() {
			return vehicleBase == null ? super.getMaxCartSpeedOnRail() : vehicleBase.getMaxSpeed(super.getMaxCartSpeedOnRail());
		}

		/**
		 * Returns if this cart can be powered
		 */
		@Override
		public boolean isPoweredCart() {
			return vehicleBase.isPoweredEntity();
		}

		@Override
		public IBlockState getDefaultDisplayTile() {
			return null;
		}

		@Override
		public Type getType() {
			return null;
		}

		/**
		 * Return the Y level in the world the cart is aiming for
		 * 
		 * @return The target level
		 */
		public int getYTarget() {
			if (vehicleBase.getModules() != null) {
				for (ModuleBase module : vehicleBase.getModules()) {
					int yTarget = module.getYTarget();
					if (yTarget != -1) {
						return yTarget;
					}
				}
			}
			return (int) posY;
		}

		/**
		 * When the cart takes damage
		 */
		@Override
		public boolean attackEntityFrom(DamageSource type, float dmg) {
			return vehicleBase.canBeAttacked(type, dmg) && super.attackEntityFrom(type, dmg);
		}

		/**
		 * When the cart passes a activator rail
		 * 
		 * @param x
		 *            The X coordinate of the rail
		 * @param y
		 *            The Y coordinate of the rail
		 * @param z
		 *            The Z coordinate of the rail
		 * @param active
		 *            If the rail is active or not
		 */
		@Override
		public void onActivatorRailPass(int x, int y, int z, boolean active) {
			if (vehicleBase.getModules() != null) {
				for (ModuleBase module : vehicleBase.getModules()) {
					module.activatedByRail(x, y, z, active);
				}
			}
		}

		/**
		 * Called when the cart is moving on top of a rail
		 * 
		 * @param x
		 *            The X coordinate
		 * @param y
		 *            The Y coordinate
		 * @param z
		 *            The Z coordinate
		 * @param acceleration
		 *            seems like the acceleration
		 */
		@Override
		public void moveMinecartOnRail(BlockPos pos) {
			super.moveMinecartOnRail(pos);
			if (vehicleBase.getModules() != null) {
				for (ModuleBase module : vehicleBase.getModules()) {
					module.moveMinecartOnRail(pos);
				}
			}
			IBlockState state = world.getBlockState(pos);
			IBlockState stateBelow = world.getBlockState(pos.down());
			EnumRailDirection railDirection = ((BlockRailBase) state.getBlock()).getRailDirection(world, pos, state, this);
			if (((railDirection == EnumRailDirection.SOUTH_EAST || railDirection == EnumRailDirection.SOUTH_WEST) && motionX < 0) || ((railDirection == EnumRailDirection.NORTH_WEST || railDirection == EnumRailDirection.NORTH_EAST) && motionX > 0)) {
				cornerFlip = true;
			} else {
				cornerFlip = false;
			}
			if (state.getBlock() != ModBlocks.ADVANCED_DETECTOR.getBlock() && vehicleBase.isDisabled()) {
				releaseCart();
			}
			boolean canBeDisabled = state.getBlock() == ModBlocks.ADVANCED_DETECTOR.getBlock()
					&& (stateBelow.getBlock() != ModBlocks.DETECTOR_UNIT.getBlock() || !DetectorType.getTypeFromSate(stateBelow).canInteractWithCart() || DetectorType.getTypeFromSate(stateBelow).shouldStopCart());
			boolean forceUnDisable = (wasDisabled && disabledPos != null && disabledPos.equals(pos));
			if (!forceUnDisable && wasDisabled) {
				wasDisabled = false;
			}
			canBeDisabled = forceUnDisable ? false : canBeDisabled;
			if (canBeDisabled && !vehicleBase.isDisabled()) {
				vehicleBase.setIsDisabled(true);
				if (pushX != 0 || pushZ != 0) {
					temppushX = pushX;
					temppushZ = pushZ;
					pushX = pushZ = 0;
				}
				disabledPos = pos;
			}
			if (fixedRailPos != null && !fixedRailPos.equals(pos)) {
				fixedRailDirection = null;
				if (fixedRailPos == null) {
					fixedRailPos = new BlockPos(0, -1, 0);
				} else {
					fixedRailPos = new BlockPos(fixedRailPos.getX(), -1, fixedRailPos.getZ());
				}
			}
		}

		/**
		 * Allows the cart to override the rail's meta data when traveling
		 * 
		 * @param pos
		 *            The coordinates of the rail
		 * @return The meta data of the rail
		 */
		public EnumRailDirection getRailDirection(BlockPos pos) {
			ModuleBase.RailDirection dir = ModuleBase.RailDirection.DEFAULT;
			for (ModuleBase module : vehicleBase.getModules()) {
				dir = module.getSpecialRailDirection(pos);
				if (dir != ModuleBase.RailDirection.DEFAULT) {
					break;
				}
			}
			if (dir == ModuleBase.RailDirection.DEFAULT) {
				return null;
			}
			int Yaw = (int) (rotationYaw % 180);
			if (Yaw < 0) {
				Yaw += 180;
			}
			boolean flag = Yaw >= 45 && Yaw <= 135;
			if (fixedRailDirection == null) {
				switch (dir) {
					case FORWARD:
						fixedRailDirection = flag ? EnumRailDirection.NORTH_SOUTH : EnumRailDirection.EAST_WEST;
						break;
					case LEFT:
						if (flag) {
							fixedRailDirection = motionZ > 0 ? EnumRailDirection.NORTH_EAST : EnumRailDirection.SOUTH_WEST;
						} else {
							fixedRailDirection = motionX > 0 ? EnumRailDirection.NORTH_WEST : EnumRailDirection.SOUTH_EAST;
						}
						break;
					case RIGHT:
						if (flag) {
							fixedRailDirection = motionZ > 0 ? EnumRailDirection.NORTH_WEST : EnumRailDirection.SOUTH_EAST;
						} else {
							fixedRailDirection = motionX > 0 ? EnumRailDirection.SOUTH_WEST : EnumRailDirection.NORTH_EAST;
						}
						break;
						// doesn't work
					case NORTH:
						if (flag) {
							fixedRailDirection = motionZ > 0 ? EnumRailDirection.NORTH_SOUTH : null;
						} else {
							fixedRailDirection = motionX > 0 ? EnumRailDirection.SOUTH_WEST : EnumRailDirection.SOUTH_EAST;
						}
						break;
					default:
						fixedRailDirection = null;
				}
				if (fixedRailDirection == null) {
					return null;
				}
				fixedRailPos = pos;
			}
			return fixedRailDirection;
		}

		/**
		 * Reset the modified meta data
		 */
		public void resetRailDirection() {
			fixedRailDirection = null;
		}

		/**
		 * Turn the cart around
		 **/
		public void turnback() {
			pushX *= -1;
			pushZ *= -1;
			temppushX *= -1;
			temppushZ *= -1;
			motionX *= -1;
			motionY *= -1;
			motionZ *= -1;
		}

		/**
		 * Allows the cart to move again after being disabled
		 */
		public void releaseCart() {
			wasDisabled = true;
			vehicleBase.setIsDisabled(false);
			pushX = temppushX;
			pushZ = temppushZ;
		}

		@Override
		protected void moveAlongTrack(BlockPos pos, IBlockState state) {
			Entity riddenByEntity = getControllingPassenger();
			if (riddenByEntity != null && riddenByEntity instanceof EntityLivingBase) {
				float move = ((EntityLivingBase) riddenByEntity).moveForward;
				((EntityLivingBase) riddenByEntity).moveForward = 0;
				super.moveAlongTrack(pos, state);
				((EntityLivingBase) riddenByEntity).moveForward = move;
			} else {
				super.moveAlongTrack(pos, state);
			}
			double d2 = this.pushX * this.pushX + this.pushZ * this.pushZ;
			if (d2 > 1.0E-4D && this.motionX * this.motionX + this.motionZ * this.motionZ > 0.001D) {
				d2 = MathHelper.sqrt(d2);
				this.pushX /= d2;
				this.pushZ /= d2;
				if (this.pushX * this.motionX + this.pushZ * this.motionZ < 0.0D) {
					this.pushX = 0.0D;
					this.pushZ = 0.0D;
				} else {
					this.pushX = this.motionX;
					this.pushZ = this.motionZ;
				}
			}
		}

		@Override
		protected void applyDrag() {
			double d0 = this.pushX * this.pushX + this.pushZ * this.pushZ;
			vehicleBase.setEngineFlag(d0 > 1.0E-4D);
			if (vehicleBase.isDisabled()) {
				motionX = 0;
				motionY = 0;
				motionZ = 0;
			} else if (vehicleBase.getEngineFlag()) {
				d0 = MathHelper.sqrt(d0);
				this.pushX /= d0;
				this.pushZ /= d0;
				double d1 = getPushFactor();
				this.motionX *= 0.800000011920929D;
				this.motionY *= 0.0D;
				this.motionZ *= 0.800000011920929D;
				this.motionX += this.pushX * d1;
				this.motionZ += this.pushZ * d1;
			} else {
				this.motionX *= 0.9800000190734863D;
				this.motionY *= 0.0D;
				this.motionZ *= 0.9800000190734863D;
			}
			super.applyDrag();
		}

		/**
		 * Get the factor this cart will push itself with when powered
		 * 
		 * @return
		 */
		protected double getPushFactor() {
			if (vehicleBase.getModules() != null) {
				for (ModuleBase module : vehicleBase.getModules()) {
					double factor = module.getPushFactor();
					if (factor >= 0) {
						return factor;
					}
				}
			}
			return 0.05D;
		}

		/**
		 * Saves data of the cart, also allows all modules to save their data
		 */
		@Override
		public NBTTagCompound writeToNBT(NBTTagCompound tagCompound) {
			super.writeToNBT(tagCompound);
			vehicleBase.writeToNBT(tagCompound);
			tagCompound.setDouble("pushX", pushX);
			tagCompound.setDouble("pushZ", pushZ);
			tagCompound.setDouble("temppushX", temppushX);
			tagCompound.setDouble("temppushZ", temppushZ);
			return tagCompound;
		}

		/**
		 * Loads the data of the cart, also allows the modules to load their data
		 */
		@Override
		public void readFromNBT(NBTTagCompound tagCompound) {
			super.readFromNBT(tagCompound);
			vehicleBase.readFromNBT(tagCompound);
			pushX = tagCompound.getDouble("pushX");
			pushZ = tagCompound.getDouble("pushZ");
			temppushX = tagCompound.getDouble("temppushX");
			temppushZ = tagCompound.getDouble("temppushZ");
		}

		@Override
		public void onUpdate() {
			super.onUpdate();
			vehicleBase.onUpdate();
			if (world.isRemote) {
				updateSounds();
			}
			setCurrentCartSpeedCapOnRail(getMaxCartSpeedOnRail());
		}

		/**
		 * Returns if this inventory(the cart) is usuable by the specific player
		 */
		@Override
		public boolean isUsableByPlayer(EntityPlayer entityplayer) {
			return entityplayer.getDistanceSq(posX, posY, posZ) <= 64D;
		}

		@Override
		public EnumActionResult applyPlayerInteraction(EntityPlayer player, Vec3d vec, EnumHand hand) {
			if(MinecraftForge.EVENT_BUS.post(new MinecartInteractEvent(this, player, hand))){
			 	return EnumActionResult.SUCCESS;
			}
			if (!vehicleBase.canInteractWithEntity(player)) {
				return EnumActionResult.PASS;
			}
			if (!world.isRemote) {
				// Saves which direction the player activated the cart from
				if (!vehicleBase.isDisabled() && getControllingPassenger() != player) {
					temppushX = posX - player.posX;
					temppushZ = posZ - player.posZ;
				}
				// if the cart can move, start it in the desired direction.
				if (!vehicleBase.isDisabled() && vehicleBase.hasFuel() && pushX == 0 && pushZ == 0) {
					pushX = temppushX;
					pushZ = temppushZ;
				}
			}
			vehicleBase.onInteractWith(player);
			return EnumActionResult.SUCCESS;
		}

		public boolean getRenderFlippedYaw(float yaw) {
			yaw = yaw % 360;
			if (yaw < 0) {
				yaw += 360;
			}
			if (!oldRender || Math.abs(yaw - lastRenderYaw) < 90 || Math.abs(yaw - lastRenderYaw) > 270 || (motionX > 0 && lastMotionX < 0) || (motionZ > 0 && lastMotionZ < 0) || (motionX < 0 && lastMotionX > 0) || (motionZ < 0 && lastMotionZ > 0)
					|| wrongRender >= 50) {
				lastMotionX = motionX;
				lastMotionZ = motionZ;
				lastRenderYaw = yaw;
				oldRender = true;
				wrongRender = 0;
				return false;
			} else {
				wrongRender++;
				return true;
			}
		}

		// Inventory, Tank and a few other methods. These methods are required due
		// to the implementing interfaces, but all logic is handled in the
		// VehicleBase.
		@Override
		public void markDirty() {
			vehicleBase.onInventoryUpdate();
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
		public int fill(FluidStack resource, boolean doFill) {
			return vehicleBase.fill(resource, doFill);
		}

		@Override
		public FluidStack drain(FluidStack resource, boolean doDrain) {
			return vehicleBase.drain(resource, doDrain);
		}

		@Override
		public FluidStack drain(int maxDrain, boolean doDrain) {
			return vehicleBase.drain(maxDrain, doDrain);
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

		@SideOnly(Side.CLIENT)
		private MovingSound sound;
		@SideOnly(Side.CLIENT)
		private MovingSound soundRiding;
		@SideOnly(Side.CLIENT)
		private int keepSilent;

		@SideOnly(Side.CLIENT)
		public void setSound(MovingSound sound, boolean riding) {
			if (riding) {
				this.soundRiding = sound;
			} else {
				this.sound = sound;
			}
		}

		@SideOnly(Side.CLIENT)
		public void silent() {
			keepSilent = 6;
		}

		@SideOnly(Side.CLIENT)
		private void updateSounds() {
			if (keepSilent > 1) {
				keepSilent--;
				stopSound(sound);
				stopSound(soundRiding);
				sound = null;
				soundRiding = null;
			} else if (keepSilent == 1) {
				keepSilent = 0;
				Minecraft.getMinecraft().getSoundHandler().playSound(new MovingSoundMinecart(this));
				Minecraft.getMinecraft().getSoundHandler().playSound(new MovingSoundMinecartRiding(Minecraft.getMinecraft().player, this));
			}
		}

		@SideOnly(Side.CLIENT)
		private void stopSound(MovingSound sound) {
			if (sound != null) {
				ReflectionHelper.setPrivateValue(MovingSound.class, sound, true, 0);
			}
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
