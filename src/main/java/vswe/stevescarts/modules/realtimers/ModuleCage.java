package vswe.stevescarts.modules.realtimers;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityFlying;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.monster.EntityCaveSpider;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntityGiantZombie;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.monster.SkeletonType;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.entity.passive.EntityWaterMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vswe.stevescarts.entitys.MinecartModular;
import vswe.stevescarts.guis.GuiMinecart;
import vswe.stevescarts.helpers.Localization;
import vswe.stevescarts.helpers.ResourceHelper;
import vswe.stevescarts.modules.IActivatorModule;
import vswe.stevescarts.modules.ModuleBase;

public class ModuleCage extends ModuleBase implements IActivatorModule {
	private int[] autoRect;
	private int[] manualRect;
	private EntityNearestTarget sorter;
	private int cooldown;
	private boolean disablePickup;

	public ModuleCage(final MinecartModular cart) {
		super(cart);
		this.autoRect = new int[] { 15, 20, 24, 12 };
		this.manualRect = new int[] { this.autoRect[0] + this.autoRect[2] + 5, this.autoRect[1], this.autoRect[2], this.autoRect[3] };
		this.sorter = new EntityNearestTarget(this.getCart());
		this.cooldown = 0;
	}

	@Override
	public boolean hasSlots() {
		return false;
	}

	@Override
	public boolean hasGui() {
		return true;
	}

	@Override
	public int guiWidth() {
		return 80;
	}

	@Override
	public int guiHeight() {
		return 35;
	}

	@Override
	public void drawForeground(final GuiMinecart gui) {
		this.drawString(gui, this.getModuleName(), 8, 6, 4210752);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void drawBackground(final GuiMinecart gui, final int x, final int y) {
		ResourceHelper.bindResource("/gui/cage.png");
		this.drawButton(gui, x, y, this.autoRect, this.disablePickup ? 2 : 3);
		this.drawButton(gui, x, y, this.manualRect, this.isCageEmpty() ? 0 : 1);
	}

	private void drawButton(final GuiMinecart gui, final int x, final int y, final int[] coords, final int imageID) {
		if (this.inRect(x, y, coords)) {
			this.drawImage(gui, coords, 0, coords[3]);
		} else {
			this.drawImage(gui, coords, 0, 0);
		}
		final int srcY = coords[3] * 2 + imageID * (coords[3] - 2);
		this.drawImage(gui, coords[0] + 1, coords[1] + 1, 0, srcY, coords[2] - 2, coords[3] - 2);
	}

	@Override
	public void drawMouseOver(final GuiMinecart gui, final int x, final int y) {
		this.drawStringOnMouseOver(gui, Localization.MODULES.ATTACHMENTS.CAGE_AUTO.translate(this.disablePickup ? "0" : "1"), x, y, this.autoRect);
		this.drawStringOnMouseOver(gui, Localization.MODULES.ATTACHMENTS.CAGE.translate(this.isCageEmpty() ? "0" : "1"), x, y, this.manualRect);
	}

	private boolean isCageEmpty() {
		return this.getCart().getRidingEntity() == null;
	}

	@Override
	public void mouseClicked(final GuiMinecart gui, final int x, final int y, final int button) {
		if (button == 0) {
			if (this.inRect(x, y, this.autoRect)) {
				this.sendPacket(0);
			} else if (this.inRect(x, y, this.manualRect)) {
				this.sendPacket(1);
			}
		}
	}

	@Override
	protected void receivePacket(final int id, final byte[] data, final EntityPlayer player) {
		if (id == 0) {
			this.disablePickup = !this.disablePickup;
		} else if (id == 1) {
			if (!this.isCageEmpty()) {
				this.manualDrop();
			} else {
				this.manualPickUp();
			}
		}
	}

	@Override
	public int numberOfPackets() {
		return 2;
	}

	@Override
	public void update() {
		super.update();
		if (this.cooldown > 0) {
			--this.cooldown;
		} else if (!this.disablePickup) {
			this.pickUpCreature(2);
			this.cooldown = 20;
		}
	}

	private void manualDrop() {
		if (!this.isCageEmpty()) {
			this.getCart().startRiding((Entity) null);
			this.cooldown = 20;
		}
	}

	private void manualPickUp() {
		this.pickUpCreature(5);
	}

	private void pickUpCreature(final int searchDistance) {
		if (this.getCart().worldObj.isRemote || !this.isCageEmpty()) {
			return;
		}
		final List<EntityLivingBase> entities = this.getCart().worldObj.getEntitiesWithinAABB(EntityLivingBase.class, this.getCart().getEntityBoundingBox().expand(searchDistance, 4.0, searchDistance));
		Collections.sort( entities, this.sorter);
		for (EntityLivingBase target : entities) {
			if (!(target instanceof EntityPlayer) && !(target instanceof EntityIronGolem) && !(target instanceof EntityDragon) && !(target instanceof EntitySlime) && !(target instanceof EntityWaterMob) && !(target instanceof EntityWither) && !(target instanceof EntityEnderman) && (!(target instanceof EntitySpider) || target instanceof EntityCaveSpider) && !(target instanceof EntityGiantZombie) && !(target instanceof EntityFlying)) {
				if (target instanceof EntitySkeleton && ((EntitySkeleton) target).getSkeletonType() == SkeletonType.NORMAL) {
					continue;
				}
				if (target.getRidingEntity() == null) {
					target.startRiding(this.getCart());
					return;
				}
				continue;
			}
		}
	}

	@Override
	public float mountedOffset(final Entity rider) {
		if (rider instanceof EntityBat) {
			return 0.5f;
		}
		if (rider instanceof EntityZombie || rider instanceof EntitySkeleton) {
			return -0.75f;
		}
		return super.mountedOffset(rider);
	}

	@Override
	public int numberOfGuiData() {
		return 1;
	}

	@Override
	protected void checkGuiData(final Object[] info) {
		this.updateGuiData(info, 0, (byte) (this.disablePickup ? 1 : 0));
	}

	@Override
	public void receiveGuiData(final int id, final short data) {
		if (id == 0) {
			this.disablePickup = (data != 0);
		}
	}

	@Override
	protected void Save(final NBTTagCompound tagCompound, final int id) {
		tagCompound.setBoolean(this.generateNBTName("disablePickup", id), this.disablePickup);
	}

	@Override
	protected void Load(final NBTTagCompound tagCompound, final int id) {
		this.disablePickup = tagCompound.getBoolean(this.generateNBTName("disablePickup", id));
	}

	@Override
	public boolean isActive(final int id) {
		if (id == 0) {
			return !this.disablePickup;
		}
		return !this.isCageEmpty();
	}

	@Override
	public void doActivate(final int id) {
		if (id == 0) {
			this.disablePickup = false;
		} else {
			this.manualPickUp();
		}
	}

	@Override
	public void doDeActivate(final int id) {
		if (id == 0) {
			this.disablePickup = true;
		} else {
			this.manualDrop();
		}
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
