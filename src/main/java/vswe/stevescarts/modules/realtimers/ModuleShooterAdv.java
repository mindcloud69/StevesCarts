package vswe.stevescarts.modules.realtimers;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import vswe.stevescarts.entitys.EntityMinecartModular;
import vswe.stevescarts.guis.GuiMinecart;
import vswe.stevescarts.helpers.Localization;
import vswe.stevescarts.helpers.ResourceHelper;
import vswe.stevescarts.modules.ModuleBase;
import vswe.stevescarts.modules.addons.mobdetectors.ModuleMobdetector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ModuleShooterAdv extends ModuleShooter {
	private ArrayList<ModuleMobdetector> detectors;
	private EntityNearestTarget sorter;
	private float detectorAngle;
	private DataParameter<Byte> OPTION;
	private DataParameter<Byte> RIFLE_DIRECTION;

	public ModuleShooterAdv(final EntityMinecartModular cart) {
		super(cart);
		this.sorter = new EntityNearestTarget(this.getCart());
	}

	@Override
	public void preInit() {
		super.preInit();
		this.detectors = new ArrayList<>();
		for (final ModuleBase module : this.getCart().getModules()) {
			if (module instanceof ModuleMobdetector) {
				this.detectors.add((ModuleMobdetector) module);
			}
		}
	}

	@Override
	protected void generatePipes(final ArrayList<Integer> list) {
		list.add(1);
	}

	@Override
	protected int guiExtraWidth() {
		return 100;
	}

	@Override
	protected int guiRequiredHeight() {
		return 10 + 10 * this.detectors.size();
	}

	private int[] getSelectionBox(final int id) {
		return new int[] { 90, id * 10 + (this.guiHeight() - 10 * this.detectors.size()) / 2, 8, 8 };
	}

	@Override
	protected void generateInterfaceRegions() {
	}

	@Override
	public void drawForeground(final GuiMinecart gui) {
		this.drawString(gui, Localization.MODULES.ATTACHMENTS.SHOOTER.translate(), 8, 6, 4210752);
		for (int i = 0; i < this.detectors.size(); ++i) {
			final int[] box = this.getSelectionBox(i);
			this.drawString(gui, this.detectors.get(i).getName(), box[0] + 12, box[1], 4210752);
		}
	}

	@Override
	public void drawBackground(final GuiMinecart gui, final int x, final int y) {
		ResourceHelper.bindResource("/gui/mobdetector.png");
		for (int i = 0; i < this.detectors.size(); ++i) {
			final int srcX = this.isOptionActive(i) ? 0 : 8;
			final int srcY = this.inRect(x, y, this.getSelectionBox(i)) ? 8 : 0;
			this.drawImage(gui, this.getSelectionBox(i), srcX, srcY);
		}
	}

	@Override
	public void mouseClicked(final GuiMinecart gui, final int x, final int y, final int button) {
		if (button == 0) {
			for (int i = 0; i < this.detectors.size(); ++i) {
				if (this.inRect(x, y, this.getSelectionBox(i))) {
					this.sendPacket(0, (byte) i);
					break;
				}
			}
		}
	}

	@Override
	public void mouseMovedOrUp(final GuiMinecart gui, final int x, final int y, final int button) {
	}

	@Override
	public int numberOfGuiData() {
		return 0;
	}

	@Override
	protected void checkGuiData(final Object[] info) {
	}

	@Override
	protected void Shoot() {
		this.setTimeToNext(15);
		if (!this.getCart().hasFuel()) {
			return;
		}
		final Entity target = this.getTarget();
		if (target != null) {
			if (this.hasProjectileItem()) {
				this.shootAtTarget(target);
			} else {
				this.getCart().world.playEvent(1001, getCart().getPosition(), 0);
			}
		}
	}

	private void shootAtTarget(final Entity target) {
		if (target == null) {
			return;
		}
		final Entity projectile = this.getProjectile(target, this.getProjectileItem(true));
		projectile.posY = this.getCart().posY + this.getCart().getEyeHeight() - 0.10000000149011612;
		final double disX = target.posX - this.getCart().posX;
		final double disY = target.posY + target.getEyeHeight() - 0.699999988079071 - projectile.posY;
		final double disZ = target.posZ - this.getCart().posZ;
		final double dis = MathHelper.sqrt(disX * disX + disZ * disZ);
		if (dis >= 1.0E-7) {
			final float theta = (float) (Math.atan2(disZ, disX) * 180.0 / 3.141592653589793) - 90.0f;
			final float phi = (float) (-(Math.atan2(disY, dis) * 180.0 / 3.141592653589793));
			this.setRifleDirection((float) Math.atan2(disZ, disX));
			final double disPX = disX / dis;
			final double disPZ = disZ / dis;
			projectile.setLocationAndAngles(this.getCart().posX + disPX * 1.5, projectile.posY, this.getCart().posZ + disPZ * 1.5, theta, phi);
			projectile.setRenderYawOffset(0.0f);
			final float disD5 = (float) dis * 0.2f;
			this.setHeading(projectile, disX, disY + disD5, disZ, 1.6f, 0.0f);
		}
		BlockPos pos = getCart().getPosition();
		this.getCart().world.playSound(pos.getX(), pos.getY(), pos.getZ(), SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.NEUTRAL, 1.0f, 1.0f / (this.getCart().rand.nextFloat() * 0.4f + 0.8f), false);
		this.setProjectileDamage(projectile);
		this.setProjectileOnFire(projectile);
		this.setProjectileKnockback(projectile);
		this.getCart().world.spawnEntity(projectile);
		this.damageEnchant();
	}

	protected int getTargetDistance() {
		return 16;
	}

	private Entity getTarget() {
		final List<Entity> entities = this.getCart().world.getEntitiesWithinAABB(Entity.class, this.getCart().getEntityBoundingBox().expand(this.getTargetDistance(), 4.0, this.getTargetDistance()));
		Collections.sort(entities, this.sorter);
		for (final Entity target : entities) {
			if (target != this.getCart() && this.canSee(target)) {
				for (int i = 0; i < this.detectors.size(); ++i) {
					if (this.isOptionActive(i)) {
						final ModuleMobdetector detector = this.detectors.get(i);
						if (detector.isValidTarget(target)) {
							return target;
						}
					}
				}
			}
		}
		return null;
	}

	private boolean canSee(final Entity target) {
		return target != null && this.getCart().world.rayTraceBlocks(new Vec3d(this.getCart().posX, this.getCart().posY + this.getCart().getEyeHeight(), this.getCart().posZ), new Vec3d(target.posX, target.posY + target.getEyeHeight(), target.posZ)) == null;
	}

	@Override
	protected void receivePacket(final int id, final byte[] data, final EntityPlayer player) {
		if (id == 0) {
			this.switchOption(data[0]);
		}
	}

	@Override
	public int numberOfPackets() {
		return 1;
	}

	@Override
	public int numberOfDataWatchers() {
		return 2;
	}

	@Override
	public void initDw() {
		OPTION = createDw(DataSerializers.BYTE);
		RIFLE_DIRECTION = createDw(DataSerializers.BYTE);
		registerDw(OPTION, (byte) 0);
		registerDw(RIFLE_DIRECTION, (byte) 0);
	}

	private void switchOption(final int id) {
		byte val = this.getDw(OPTION);
		val ^= (byte) (1 << id);
		this.updateDw(OPTION, val);
	}

	public void setOptions(final byte val) {
		this.updateDw(OPTION, val);
	}

	public byte selectedOptions() {
		return this.getDw(OPTION);
	}

	private boolean isOptionActive(final int id) {
		return (this.selectedOptions() & 1 << id) != 0x0;
	}

	@Override
	protected boolean isPipeActive(final int id) {
		if (this.isPlaceholder()) {
			return this.getSimInfo().getIsPipeActive();
		}
		return this.selectedOptions() != 0;
	}

	public float getDetectorAngle() {
		return this.detectorAngle;
	}

	@Override
	public void update() {
		super.update();
		if (this.isPipeActive(0)) {
			this.detectorAngle = (float) ((this.detectorAngle + 0.1f) % 6.283185307179586);
		}
	}

	private void setRifleDirection(float val) {
		val /= 6.2831855f;
		val *= 256.0f;
		val %= 256.0f;
		if (val < 0.0f) {
			val += 256.0f;
		}
		this.updateDw(RIFLE_DIRECTION, (byte) val);
	}

	public float getRifleDirection() {
		float val;
		if (this.isPlaceholder()) {
			val = 0.0f;
		} else {
			val = this.getDw(RIFLE_DIRECTION);
		}
		val /= 256.0f;
		val *= 6.2831855f;
		return val;
	}

	@Override
	protected void Save(final NBTTagCompound tagCompound, final int id) {
		tagCompound.setByte(this.generateNBTName("Options", id), this.selectedOptions());
		this.saveTick(tagCompound, id);
	}

	@Override
	protected void Load(final NBTTagCompound tagCompound, final int id) {
		this.setOptions(tagCompound.getByte(this.generateNBTName("Options", id)));
		this.loadTick(tagCompound, id);
	}

	private static class EntityNearestTarget implements Comparator {
		private Entity entity;

		public EntityNearestTarget(final Entity entity) {
			this.entity = entity;
		}

		public int compareDistanceSq(final Entity entity1, final Entity entity2) {
			final double distance1 = this.entity.getDistanceSqToEntity(entity1);
			final double distance2 = this.entity.getDistanceSqToEntity(entity2);
			return (distance1 < distance2) ? -1 : ((distance1 > distance2) ? 1 : 0);
		}

		@Override
		public int compare(final Object obj1, final Object obj2) {
			return this.compareDistanceSq((Entity) obj1, (Entity) obj2);
		}
	}
}
