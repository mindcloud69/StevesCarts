package vswe.stevescarts.modules.realtimers;

import java.util.ArrayList;

import net.minecraft.entity.Entity;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.util.math.MathHelper;
import vswe.stevescarts.containers.slots.SlotArrow;
import vswe.stevescarts.containers.slots.SlotBase;
import vswe.stevescarts.entitys.EntityMinecartModular;
import vswe.stevescarts.guis.GuiMinecart;
import vswe.stevescarts.helpers.EnchantmentInfo;
import vswe.stevescarts.helpers.Localization;
import vswe.stevescarts.helpers.ResourceHelper;
import vswe.stevescarts.modules.ISuppliesModule;
import vswe.stevescarts.modules.ModuleBase;
import vswe.stevescarts.modules.addons.ModuleEnchants;
import vswe.stevescarts.modules.addons.projectiles.ModuleProjectile;

public class ModuleShooter extends ModuleBase implements ISuppliesModule {
	private ArrayList<ModuleProjectile> projectiles;
	private ModuleEnchants enchanter;
	private int pipeSelectionX;
	private int pipeSelectionY;
	private int intervalSelectionX;
	private int intervalSelectionY;
	private int[] intervalSelection;
	private int[] intervalDragArea;
	private int currentCooldownState;
	private int dragState;
	private final ArrayList<Integer> pipes;
	private final float[] pipeRotations;
	private final int[] AInterval;
	private int arrowTick;
	private int arrowInterval;
	private static DataParameter<Byte> ACTIVE_PIPE = createDw(DataSerializers.BYTE);

	public ModuleShooter(final EntityMinecartModular cart) {
		super(cart);
		this.dragState = -1;
		this.AInterval = new int[] { 1, 3, 5, 7, 10, 13, 17, 21, 27, 35, 44, 55, 70, 95, 130, 175, 220, 275, 340, 420, 520, 650 };
		this.arrowInterval = 5;
		this.generatePipes(this.pipes = new ArrayList<Integer>());
		this.pipeRotations = new float[this.pipes.size()];
		this.generateInterfaceRegions();
	}

	@Override
	public void init() {
		super.init();
		this.projectiles = new ArrayList<ModuleProjectile>();
		for (final ModuleBase module : this.getCart().getModules()) {
			if (module instanceof ModuleProjectile) {
				this.projectiles.add((ModuleProjectile) module);
			} else {
				if (!(module instanceof ModuleEnchants)) {
					continue;
				}
				(this.enchanter = (ModuleEnchants) module).addType(EnchantmentInfo.ENCHANTMENT_TYPE.SHOOTER);
			}
		}
	}

	@Override
	protected int getInventoryHeight() {
		return 2;
	}

	@Override
	protected SlotBase getSlot(final int slotId, final int x, final int y) {
		return new SlotArrow(this.getCart(), this, slotId, 8 + x * 18, 23 + y * 18);
	}

	@Override
	public boolean hasGui() {
		return true;
	}

	@Override
	public void drawForeground(final GuiMinecart gui) {
		this.drawString(gui, Localization.MODULES.ATTACHMENTS.SHOOTER.translate(), 8, 6, 4210752);
		final int delay = this.AInterval[this.arrowInterval];
		final double freq = 20.0 / (delay + 1);
		String s = String.valueOf((int) (freq * 1000.0) / 1000.0);
		this.drawString(gui, Localization.MODULES.ATTACHMENTS.FREQUENCY.translate() + ":", this.intervalDragArea[0] + this.intervalDragArea[2] + 5, 15, 4210752);
		this.drawString(gui, s, this.intervalDragArea[0] + this.intervalDragArea[2] + 5, 23, 4210752);
		s = String.valueOf(delay / 20.0 + Localization.MODULES.ATTACHMENTS.SECONDS.translate(new String[0]));
		this.drawString(gui, Localization.MODULES.ATTACHMENTS.DELAY.translate() + ":", this.intervalDragArea[0] + this.intervalDragArea[2] + 5, 35, 4210752);
		this.drawString(gui, s, this.intervalDragArea[0] + this.intervalDragArea[2] + 5, 43, 4210752);
	}

	@Override
	public int guiWidth() {
		return super.guiWidth() + this.guiExtraWidth();
	}

	protected int guiExtraWidth() {
		return 112;
	}

	@Override
	public int guiHeight() {
		return Math.max(super.guiHeight(), this.guiRequiredHeight());
	}

	protected int guiRequiredHeight() {
		return 67;
	}

	protected void generateInterfaceRegions() {
		this.pipeSelectionX = this.guiWidth() - 110;
		this.pipeSelectionY = (this.guiHeight() - 12 - 26) / 2 + 12;
		this.intervalSelectionX = this.pipeSelectionX + 26 + 8;
		this.intervalSelectionY = 10;
		this.intervalSelection = new int[] { this.intervalSelectionX, this.intervalSelectionY, 14, 53 };
		this.intervalDragArea = new int[] { this.intervalSelectionX - 4, this.intervalSelectionY, 40, 53 };
	}

	@Override
	public void drawBackground(final GuiMinecart gui, final int x, final int y) {
		ResourceHelper.bindResource("/gui/shooter.png");
		this.drawImage(gui, this.pipeSelectionX + 9, this.pipeSelectionY + 9 - 1, 0, 104, 8, 9);
		for (int i = 0; i < this.pipes.size(); ++i) {
			final int pipe = this.pipes.get(i);
			final int pipeX = pipe % 3;
			final int pipeY = pipe / 3;
			final boolean active = this.isPipeActive(i);
			final boolean selected = this.inRect(x, y, this.getRectForPipe(pipe)) || (this.currentCooldownState == 0 && active);
			int srcX = pipeX * 9;
			if (!active) {
				srcX += 26;
			}
			int srcY = pipeY * 9;
			if (selected) {
				srcY += 26;
			}
			this.drawImage(gui, this.getRectForPipe(pipe), srcX, srcY);
		}
		this.drawImage(gui, this.intervalSelection, 42, 52);
		final int size = (int) (this.arrowInterval / this.AInterval.length * 4.0f);
		int targetX = this.intervalSelectionX + 7;
		final int targetY = this.intervalSelectionY + this.arrowInterval * 2;
		int srcX2 = 0;
		final int srcY2 = 52 + size * 13;
		this.drawImage(gui, targetX, targetY, srcX2, srcY2, 25, 13);
		srcX2 += 25;
		targetX += 7;
		this.drawImage(gui, targetX, targetY + 1, srcX2, srcY2 + 1, 1, 11);
		this.drawImage(gui, targetX + 1, targetY + 2, srcX2 + 1, srcY2 + 2, 1, 9);
		this.drawImage(gui, targetX + 1, targetY + 1, srcX2 + 1, srcY2 + 1, Math.min(this.currentCooldownState, 15), 2);
		this.drawImage(gui, targetX + 15, targetY + 1, srcX2 + 15, srcY2 + 1, 2, Math.max(Math.min(this.currentCooldownState, 25) - 15, 0));
		final int len = Math.max(Math.min(this.currentCooldownState, 41) - 25, 0);
		this.drawImage(gui, targetX + 1 + (16 - len), targetY + 10, srcX2 + 1 + (16 - len), srcY2 + 10, len, 2);
	}

	private int getCurrentCooldownState() {
		final double perc = this.arrowTick / this.AInterval[this.arrowInterval];
		return this.currentCooldownState = (int) (41.0 * perc);
	}

	private int[] getRectForPipe(final int pipe) {
		return new int[] { this.pipeSelectionX + pipe % 3 * 9, this.pipeSelectionY + pipe / 3 * 9, 8, 8 };
	}

	@Override
	public void mouseClicked(final GuiMinecart gui, final int x, final int y, final int button) {
		if (button == 0) {
			if (this.inRect(x, y, this.intervalDragArea)) {
				this.dragState = y - (this.intervalSelectionY + this.arrowInterval * 2);
			} else {
				for (int i = 0; i < this.pipes.size(); ++i) {
					if (this.inRect(x, y, this.getRectForPipe(this.pipes.get(i)))) {
						this.sendPacket(0, (byte) i);
						break;
					}
				}
			}
		}
	}

	@Override
	public void mouseMovedOrUp(final GuiMinecart gui, final int x, final int y, final int button) {
		if (button != -1) {
			this.dragState = -1;
		} else if (this.dragState != -1) {
			final int interval = (y + this.getCart().getRealScrollY() - this.intervalSelectionY - this.dragState) / 2;
			if (interval != this.arrowInterval && interval >= 0 && interval < this.AInterval.length) {
				this.sendPacket(1, (byte) interval);
			}
		}
	}

	@Override
	protected void receivePacket(final int id, final byte[] data, final EntityPlayer player) {
		if (id == 0) {
			byte info = this.getActivePipes();
			info ^= (byte) (1 << data[0]);
			this.setActivePipes(info);
		} else if (id == 1) {
			byte info = data[0];
			if (info < 0) {
				info = 0;
			} else if (info >= this.AInterval.length) {
				info = (byte) (this.AInterval.length - 1);
			}
			this.arrowInterval = info;
			this.arrowTick = this.AInterval[info];
		}
	}

	@Override
	public int numberOfPackets() {
		return 2;
	}

	@Override
	public int numberOfGuiData() {
		return 2;
	}

	@Override
	protected void checkGuiData(final Object[] info) {
		this.updateGuiData(info, 0, (short) this.currentCooldownState);
		this.updateGuiData(info, 1, (short) this.arrowInterval);
	}

	@Override
	public void receiveGuiData(final int id, final short data) {
		if (id == 0) {
			this.currentCooldownState = data;
		} else if (id == 1) {
			this.arrowInterval = data;
		}
	}

	@Override
	public void update() {
		super.update();
		if (!this.getCart().worldObj.isRemote) {
			if (this.arrowTick > 0) {
				--this.arrowTick;
			} else {
				this.Shoot();
			}
		} else {
			this.rotatePipes(false);
		}
	}

	protected void generatePipes(final ArrayList<Integer> list) {
		for (int i = 0; i < 9; ++i) {
			if (i != 4) {
				list.add(i);
			}
		}
	}

	protected boolean hasProjectileItem() {
		return this.getProjectileItem(false) != null;
	}

	protected ItemStack getProjectileItem(boolean flag) {
		if (flag && this.enchanter != null && this.enchanter.useInfinity()) {
			flag = false;
		}
		for (int i = 0; i < this.getInventorySize(); ++i) {
			if (this.getStack(i) != null && this.isValidProjectileItem(this.getStack(i))) {
				final ItemStack projectile = this.getStack(i).copy();
				projectile.stackSize = 1;
				if (flag && !this.getCart().hasCreativeSupplies()) {
					final ItemStack stack = this.getStack(i);
					--stack.stackSize;
					if (this.getStack(i).stackSize == 0) {
						this.setStack(i, null);
					}
				}
				return projectile;
			}
		}
		return null;
	}

	protected void Shoot() {
		this.setTimeToNext(this.AInterval[this.arrowInterval]);
		if ((this.getCart().pushX != 0.0 && this.getCart().pushZ != 0.0) || (this.getCart().pushX == 0.0 && this.getCart().pushZ == 0.0) || !this.getCart().hasFuel()) {
			return;
		}
		boolean hasShot = false;
		for (int i = 0; i < this.pipes.size(); ++i) {
			if (this.isPipeActive(i)) {
				final int pipe = this.pipes.get(i);
				if (!this.hasProjectileItem()) {
					break;
				}
				int x = pipe % 3 - 1;
				int y = pipe / 3 - 1;
				if (this.getCart().pushZ > 0.0) {
					y *= -1;
					x *= -1;
				} else if (this.getCart().pushZ >= 0.0) {
					if (this.getCart().pushX < 0.0) {
						final int temp = -x;
						x = y;
						y = temp;
					} else if (this.getCart().pushX > 0.0) {
						final int temp = x;
						x = -y;
						y = temp;
					}
				}
				final Entity projectile = this.getProjectile(null, this.getProjectileItem(true));
				projectile.setPosition(this.getCart().posX + x * 1.5, this.getCart().posY + 0.75, this.getCart().posZ + y * 1.5);
				this.setHeading(projectile, x, 0.10000000149011612, y, 1.6f, 12.0f);
				this.setProjectileDamage(projectile);
				this.setProjectileOnFire(projectile);
				this.setProjectileKnockback(projectile);
				this.getCart().worldObj.spawnEntityInWorld(projectile);
				hasShot = true;
				this.damageEnchant();
			}
		}
		if (hasShot) {
			this.getCart().worldObj.playEvent(1002, this.getCart().getPosition(), 0);
		}
	}

	protected void damageEnchant() {
		if (this.enchanter != null) {
			this.enchanter.damageEnchant(EnchantmentInfo.ENCHANTMENT_TYPE.SHOOTER, 1);
		}
	}

	protected void setProjectileOnFire(final Entity projectile) {
		if (this.enchanter != null && this.enchanter.useFlame()) {
			projectile.setFire(100);
		}
	}

	protected void setProjectileDamage(final Entity projectile) {
		if (this.enchanter != null && projectile instanceof EntityArrow) {
			final int power = this.enchanter.getPowerLevel();
			if (power > 0) {
				final EntityArrow arrow = (EntityArrow) projectile;
				arrow.setDamage(arrow.getDamage() + power * 0.5 + 0.5);
			}
		}
	}

	protected void setProjectileKnockback(final Entity projectile) {
		if (this.enchanter != null && projectile instanceof EntityArrow) {
			final int punch = this.enchanter.getPunchLevel();
			if (punch > 0) {
				final EntityArrow arrow = (EntityArrow) projectile;
				arrow.setKnockbackStrength(punch);
			}
		}
	}

	protected void setHeading(final Entity projectile, final double motionX, final double motionY, final double motionZ, final float motionMult, final float motionNoise) {
		if (projectile instanceof IProjectile) {
			((IProjectile) projectile).setThrowableHeading(motionX, motionY, motionZ, motionMult, motionNoise);
		} else if (projectile instanceof EntityFireball) {
			final EntityFireball fireball = (EntityFireball) projectile;
			final double totalMotion = MathHelper.sqrt_double(motionX * motionX + motionY * motionY + motionZ * motionZ);
			fireball.accelerationX = motionX / totalMotion * 0.1;
			fireball.accelerationY = motionY / totalMotion * 0.1;
			fireball.accelerationZ = motionZ / totalMotion * 0.1;
		}
	}

	protected Entity getProjectile(final Entity target, final ItemStack item) {
		for (final ModuleProjectile module : this.projectiles) {
			if (module.isValidProjectile(item)) {
				return module.createProjectile(target, item);
			}
		}
		return new EntityArrow(this.getCart().worldObj) {
			@Override
			protected ItemStack getArrowStack() {
				return item;
			}
		};
	}

	public boolean isValidProjectileItem(final ItemStack item) {
		for (final ModuleProjectile module : this.projectiles) {
			if (module.isValidProjectile(item)) {
				return true;
			}
		}
		return item.getItem() == Items.ARROW;
	}

	protected void setTimeToNext(final int val) {
		this.arrowTick = val;
	}

	private void rotatePipes(final boolean isNew) {
		final float minRotation = 0.0f;
		final float maxRotation = 0.7853982f;
		final float speed = 0.15f;
		for (int i = 0; i < this.pipes.size(); ++i) {
			final boolean isActive = this.isPipeActive(i);
			if (isNew && isActive) {
				this.pipeRotations[i] = minRotation;
			} else if (isNew && !isActive) {
				this.pipeRotations[i] = maxRotation;
			} else if (isActive && this.pipeRotations[i] > minRotation) {
				final float[] pipeRotations = this.pipeRotations;
				final int n = i;
				pipeRotations[n] -= speed;
				if (this.pipeRotations[i] < minRotation) {
					this.pipeRotations[i] = minRotation;
				}
			} else if (!isActive && this.pipeRotations[i] < maxRotation) {
				final float[] pipeRotations2 = this.pipeRotations;
				final int n2 = i;
				pipeRotations2[n2] += speed;
				if (this.pipeRotations[i] > maxRotation) {
					this.pipeRotations[i] = maxRotation;
				}
			}
		}
	}

	@Override
	public int numberOfDataWatchers() {
		return 1;
	}

	@Override
	public void initDw() {
		registerDw(ACTIVE_PIPE, (byte)0);
	}

	public void setActivePipes(final byte val) {
		this.updateDw(ACTIVE_PIPE, val);
	}

	public byte getActivePipes() {
		if (this.isPlaceholder()) {
			return this.getSimInfo().getActivePipes();
		}
		return this.getDw(ACTIVE_PIPE);
	}

	protected boolean isPipeActive(final int id) {
		return (this.getActivePipes() & 1 << id) != 0x0;
	}

	public int getPipeCount() {
		return this.pipes.size();
	}

	public float getPipeRotation(final int id) {
		return this.pipeRotations[id];
	}

	@Override
	protected void Save(final NBTTagCompound tagCompound, final int id) {
		tagCompound.setByte(this.generateNBTName("Pipes", id), this.getActivePipes());
		tagCompound.setByte(this.generateNBTName("Interval", id), (byte) this.arrowInterval);
		this.saveTick(tagCompound, id);
	}

	@Override
	protected void Load(final NBTTagCompound tagCompound, final int id) {
		this.setActivePipes(tagCompound.getByte(this.generateNBTName("Pipes", id)));
		this.arrowInterval = tagCompound.getByte(this.generateNBTName("Interval", id));
		this.loadTick(tagCompound, id);
	}

	protected void saveTick(final NBTTagCompound tagCompound, final int id) {
		tagCompound.setByte(this.generateNBTName("Tick", id), (byte) this.arrowTick);
	}

	protected void loadTick(final NBTTagCompound tagCompound, final int id) {
		this.arrowTick = tagCompound.getByte(this.generateNBTName("Tick", id));
	}

	@Override
	public boolean haveSupplies() {
		return this.hasProjectileItem();
	}
}
