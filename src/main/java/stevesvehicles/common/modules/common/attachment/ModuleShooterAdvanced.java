package stevesvehicles.common.modules.common.attachment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import stevesvehicles.api.network.DataReader;
import stevesvehicles.api.network.DataWriter;
import stevesvehicles.client.ResourceHelper;
import stevesvehicles.client.gui.assembler.SimulationInfo;
import stevesvehicles.client.gui.assembler.SimulationInfoBoolean;
import stevesvehicles.client.gui.screen.GuiVehicle;
import stevesvehicles.client.localization.entry.block.LocalizationAssembler;
import stevesvehicles.client.localization.entry.module.LocalizationShooter;
import stevesvehicles.common.modules.ModuleBase;
import stevesvehicles.common.modules.common.addon.mobdetector.ModuleEntityDetector;
import stevesvehicles.common.vehicles.VehicleBase;

public class ModuleShooterAdvanced extends ModuleShooter {
	private static DataParameter<Byte> OPTION;
	private static DataParameter<Byte> RIFLE_DIRECTION;

	public ModuleShooterAdvanced(VehicleBase vehicleBase) {
		super(vehicleBase);
	}

	@Override
	public void loadSimulationInfo(List<SimulationInfo> simulationInfo) {
		simulationInfo.add(new SimulationInfoBoolean(LocalizationAssembler.INFO_BARREL, "barrel"));
	}

	private ArrayList<ModuleEntityDetector> detectors;

	@Override
	public void preInit() {
		super.preInit();
		detectors = new ArrayList<>();
		for (ModuleBase module : getVehicle().getModules()) {
			if (module instanceof ModuleEntityDetector) {
				detectors.add((ModuleEntityDetector) module);
			}
		}
	}

	@Override
	protected void generatePipes(ArrayList<Integer> list) {
		list.add(1);
	}

	@Override
	protected int guiExtraWidth() {
		return 100;
	}

	@Override
	protected int guiRequiredHeight() {
		return 10 + 10 * detectors.size();
	}

	private int[] getSelectionBox(int id) {
		return new int[] { 90, id * 10 + (guiHeight() - 10 * detectors.size()) / 2, 8, 8 };
	}

	@Override
	protected void generateInterfaceRegions() {
	}

	@Override
	public void drawForeground(GuiVehicle gui) {
		drawString(gui, LocalizationShooter.SHOOTER_TITLE.translate(), 8, 6, 0x404040);
		for (int i = 0; i < detectors.size(); i++) {
			int[] box = getSelectionBox(i);
			drawString(gui, detectors.get(i).getName(), box[0] + 12, box[1], 0x404040);
		}
	}

	private static final ResourceLocation TEXTURE = ResourceHelper.getResource("/gui/mob_detector.png");

	@Override
	public void drawBackground(GuiVehicle gui, int x, int y) {
		ResourceHelper.bindResource(TEXTURE);
		for (int i = 0; i < detectors.size(); i++) {
			int srcX = isOptionActive(i) ? 1 : 10;
			int srcY = inRect(x, y, getSelectionBox(i)) ? 10 : 1;
			drawImage(gui, getSelectionBox(i), srcX, srcY);
		}
	}

	@Override
	public void mouseClicked(GuiVehicle gui, int x, int y, int button) throws IOException {
		if (button == 0) {
			for (int i = 0; i < detectors.size(); i++) {
				if (inRect(x, y, getSelectionBox(i))) {
					DataWriter dw = getDataWriter();
					dw.writeByte(i);
					sendPacketToServer(dw);
					break;
				}
			}
		}
	}

	@Override
	public void mouseMovedOrUp(GuiVehicle gui, int x, int y, int button) {
	}

	@Override
	public int numberOfGuiData() {
		return 0;
	}

	@Override
	protected void checkGuiData(Object[] info) {
	}

	@Override
	protected void shoot() {
		setTimeToNext(15);
		if (!getVehicle().hasFuel()) {
			return;
		}
		Entity target = getTarget();
		if (target != null) {
			if (hasProjectileItem()) {
				shootAtTarget(target);
			} else {
				getVehicle().getWorld().playEvent(1001, getVehicle().pos(), 0);
			}
		}
	}

	private void shootAtTarget(Entity target) {
		if (target == null) {
			return;
		}
		Entity projectile = getProjectile(target, getProjectileItem(true));
		projectile.posY = getVehicle().getEntity().posY + getVehicle().getEntity().getEyeHeight() - 0.10000000149011612D;
		double disX = target.posX - getVehicle().getEntity().posX;
		double disY = target.posY + target.getEyeHeight() - 0.699999988079071D - projectile.posY;
		double disZ = target.posZ - getVehicle().getEntity().posZ;
		double dis = MathHelper.sqrt(disX * disX + disZ * disZ);
		if (dis >= 1.0E-7D) {
			float theta = (float) (Math.atan2(disZ, disX) * 180.0D / Math.PI) - 90.0F;
			float phi = (float) (-(Math.atan2(disY, dis) * 180.0D / Math.PI));
			setRifleDirection((float) Math.atan2(disZ, disX));
			double disPX = disX / dis;
			double disPZ = disZ / dis;
			projectile.setLocationAndAngles(getVehicle().getEntity().posX + disPX * 1.5F, projectile.posY, getVehicle().getEntity().posZ + disPZ * 1.5, theta, phi);
			projectile.setRenderYawOffset(0.0F);
			float disD5 = (float) dis * 0.2F;
			setHeading(projectile, disX, disY + disD5, disZ, 1.6F, 0/* 12.0F */);
		}
		getVehicle().getEntity().playSound(SoundEvents.ENTITY_ARROW_SHOOT, 1.0f, 1.0f / (getVehicle().getWorld().rand.nextFloat() * 0.4f + 0.8f));
		setProjectileDamage(projectile);
		setProjectileOnFire(projectile);
		setProjectileKnockBack(projectile);
		getVehicle().getWorld().spawnEntity(projectile);
		damageEnchant();
	}

	protected int getTargetDistance() {
		return 16;
	}

	private EntityNearestTarget sorter = new EntityNearestTarget(getVehicle().getEntity());

	private Entity getTarget() {
		List entities = getVehicle().getWorld().getEntitiesWithinAABB(Entity.class, getVehicle().getEntity().getEntityBoundingBox().expand(getTargetDistance(), 4.0D, getTargetDistance()));
		Collections.sort(entities, sorter);
		for (Object entity : entities) {
			Entity target = (Entity) entity;
			if (target != getVehicle().getEntity() && canSee(target)) {
				for (int i = 0; i < detectors.size(); i++) {
					if (isOptionActive(i)) {
						ModuleEntityDetector detector = detectors.get(i);
						if (detector.isValidTarget(target)) {
							return target;
						}
					}
				}
			}
		}
		return null;
	}

	private boolean canSee(Entity target) {
		return target != null && getVehicle().getWorld().rayTraceBlocks(new Vec3d(getVehicle().getEntity().posX, getVehicle().getEntity().posY + getVehicle().getEntity().getEyeHeight(), getVehicle().getEntity().posZ),
				new Vec3d(target.posX, target.posY + target.getEyeHeight(), target.posZ)) == null;
	}

	@Override
	public void readData(DataReader dr, EntityPlayer player) throws IOException {
		switchOption(dr.readByte());
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

	private void switchOption(int id) {
		byte val = getDw(OPTION);
		val ^= 1 << id;
		updateDw(OPTION, val);
	}

	public void setOptions(byte val) {
		updateDw(OPTION, val);
	}

	public byte selectedOptions() {
		return getDw(OPTION);
	}

	private boolean isOptionActive(int id) {
		return (selectedOptions() & (1 << id)) != 0;
	}

	@Override
	protected boolean isPipeActive(int id) {
		if (isPlaceholder()) {
			return getBooleanSimulationInfo();
		} else {
			return selectedOptions() != 0;
		}
	}

	private float detectorAngle;

	public float getDetectorAngle() {
		return detectorAngle;
	}

	@Override
	public void update() {
		super.update();
		if (isPipeActive(0)) {
			detectorAngle = (float) ((detectorAngle + 0.1F) % (Math.PI * 2));
		}
	}

	private void setRifleDirection(float val) {
		val /= 2 * (float) Math.PI;
		val *= 256;
		val %= 256;
		if (val < 0) {
			val += 256;
		}
		updateDw(RIFLE_DIRECTION, (byte) val);
	}

	public float getRifleDirection() {
		float val;
		if (isPlaceholder()) {
			val = 0F;
		} else {
			val = getDw(RIFLE_DIRECTION);
		}
		val /= 256F;
		val *= (float) Math.PI * 2;
		return val;
	}

	@Override
	protected void save(NBTTagCompound tagCompound) {
		tagCompound.setByte("Options", selectedOptions());
		saveTick(tagCompound);
	}

	@Override
	protected void load(NBTTagCompound tagCompound) {
		setOptions(tagCompound.getByte("Options"));
		loadTick(tagCompound);
	}

	private static class EntityNearestTarget implements Comparator {
		private Entity entity;

		public EntityNearestTarget(Entity entity) {
			this.entity = entity;
		}

		public int compareDistanceSq(Entity entity1, Entity entity2) {
			double distance1 = this.entity.getDistanceSqToEntity(entity1);
			double distance2 = this.entity.getDistanceSqToEntity(entity2);
			return distance1 < distance2 ? -1 : distance1 > distance2 ? 1 : 0;
		}

		@Override
		public int compare(Object obj1, Object obj2) {
			return this.compareDistanceSq((Entity) obj1, (Entity) obj2);
		}
	}
}
