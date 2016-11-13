package vswe.stevescarts.modules.realtimers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import vswe.stevescarts.containers.slots.SlotBase;
import vswe.stevescarts.containers.slots.SlotExplosion;
import vswe.stevescarts.entitys.EntityMinecartModular;
import vswe.stevescarts.guis.GuiMinecart;
import vswe.stevescarts.helpers.ComponentTypes;
import vswe.stevescarts.helpers.Localization;
import vswe.stevescarts.helpers.ResourceHelper;
import vswe.stevescarts.modules.ModuleBase;

public class ModuleDynamite extends ModuleBase {
	private boolean markerMoving;
	private int fuseStartX;
	private int fuseStartY;
	private final int maxFuseLength = 150;

	private DataParameter<Byte> FUSE = createDw(DataSerializers.BYTE);
	private DataParameter<Byte> FUSE_LENGTH = createDw(DataSerializers.BYTE);
	private DataParameter<Byte> EXPLOSION = createDw(DataSerializers.BYTE);

	public ModuleDynamite(final EntityMinecartModular cart) {
		super(cart);
		this.fuseStartX = super.guiWidth() + 5;
		this.fuseStartY = 27;
	}

	@Override
	public void drawForeground(final GuiMinecart gui) {
		this.drawString(gui, Localization.MODULES.ATTACHMENTS.EXPLOSIVES.translate(), 8, 6, 4210752);
	}

	@Override
	protected SlotBase getSlot(final int slotId, final int x, final int y) {
		return new SlotExplosion(this.getCart(), slotId, 8 + x * 18, 23 + y * 18);
	}

	@Override
	public boolean hasGui() {
		return true;
	}

	@Override
	protected int getInventoryWidth() {
		return 1;
	}

	@Override
	public void activatedByRail(final int x, final int y, final int z, final boolean active) {
		if (active && this.getFuse() == 0) {
			this.prime();
		}
	}

	@Override
	public void update() {
		super.update();
		if (this.isPlaceholder()) {
			if (this.getFuse() == 0 && this.getSimInfo().getShouldExplode()) {
				this.setFuse(1);
			} else if (this.getFuse() != 0 && !this.getSimInfo().getShouldExplode()) {
				this.setFuse(0);
			}
		}
		if (this.getFuse() > 0) {
			this.setFuse(this.getFuse() + 1);
			if (this.getFuse() == this.getFuseLength()) {
				this.explode();
				if (!this.isPlaceholder()) {
					this.getCart().setDead();
				}
			}
		}
	}

	@Override
	public int guiWidth() {
		return super.guiWidth() + 136;
	}

	private int[] getMovableMarker() {
		return new int[] { this.fuseStartX + (int) (105.0f * (1.0f - this.getFuseLength() / 150.0f)), this.fuseStartY, 4, 10 };
	}

	@Override
	public void drawBackground(final GuiMinecart gui, final int x, final int y) {
		ResourceHelper.bindResource("/gui/explosions.png");
		this.drawImage(gui, this.fuseStartX, this.fuseStartY + 3, 12, 0, 105, 4);
		this.drawImage(gui, this.fuseStartX + 105, this.fuseStartY - 4, 0, 10, 16, 16);
		this.drawImage(gui, this.fuseStartX + (int) (105.0f * (1.0f - (this.getFuseLength() - this.getFuse()) / 150.0f)), this.fuseStartY, this.isPrimed() ? 8 : 4, 0, 4, 10);
		this.drawImage(gui, this.getMovableMarker(), 0, 0);
	}

	@Override
	public void mouseClicked(final GuiMinecart gui, final int x, final int y, final int button) {
		if (button == 0 && this.getFuse() == 0 && this.inRect(x, y, this.getMovableMarker())) {
			this.markerMoving = true;
		}
	}

	@Override
	public void mouseMovedOrUp(final GuiMinecart gui, final int x, final int y, final int button) {
		if (this.getFuse() != 0) {
			this.markerMoving = false;
		} else if (this.markerMoving) {
			int tempfuse = 150 - (int) ((x - this.fuseStartX) / 0.7f);
			if (tempfuse < 2) {
				tempfuse = 2;
			} else if (tempfuse > 150) {
				tempfuse = 150;
			}
			this.sendPacket(0, (byte) tempfuse);
		}
		if (button != -1) {
			this.markerMoving = false;
		}
	}

	private boolean isPrimed() {
		return this.getFuse() / 5 % 2 == 0 && this.getFuse() != 0;
	}

	private void explode() {
		if (this.isPlaceholder()) {
			this.setFuse(1);
		} else {
			final float f = this.explosionSize();
			this.setStack(0, null);
			this.getCart().worldObj.createExplosion(null, this.getCart().posX, this.getCart().posY, this.getCart().posZ, f, true);
		}
	}

	@Override
	public void onInventoryChanged() {
		super.onInventoryChanged();
		this.createExplosives();
	}

	@Override
	public boolean dropOnDeath() {
		return this.getFuse() == 0;
	}

	@Override
	public void onDeath() {
		if (this.getFuse() > 0 && this.getFuse() < this.getFuseLength()) {
			this.explode();
		}
	}

	public float explosionSize() {
		if (this.isPlaceholder()) {
			return this.getSimInfo().getExplosionSize() / 2.5f;
		}
		return this.getDw(EXPLOSION) / 2.5f;
	}

	public void createExplosives() {
		if (this.isPlaceholder() || this.getCart().worldObj.isRemote) {
			return;
		}
		int f = 8;
		if (ComponentTypes.DYNAMITE.isStackOfType(this.getStack(0))) {
			f += this.getStack(0).stackSize * 2;
		}
		this.updateDw(EXPLOSION, (byte) f);
	}

	@Override
	public int numberOfDataWatchers() {
		return 3;
	}

	@Override
	public void initDw() {
		registerDw(FUSE, (byte)0);
		registerDw(FUSE_LENGTH, (byte)70);
		registerDw(EXPLOSION, (byte)8);
	}

	public int getFuse() {
		if (this.isPlaceholder()) {
			return this.getSimInfo().fuse;
		}
		final int val = this.getDw(FUSE);
		if (val < 0) {
			return val + 256;
		}
		return val;
	}

	private void setFuse(final int val) {
		if (this.isPlaceholder()) {
			this.getSimInfo().fuse = val;
		} else {
			this.updateDw(FUSE, (byte) val);
		}
	}

	public void setFuseLength(int val) {
		if (val > 150) {
			val = 150;
		}
		this.updateDw(FUSE_LENGTH, (byte) val);
	}

	public int getFuseLength() {
		if (this.isPlaceholder()) {
			return this.getSimInfo().getFuseLength();
		}
		final int val = this.getDw(FUSE_LENGTH);
		if (val < 0) {
			return val + 256;
		}
		return val;
	}

	public void prime() {
		this.setFuse(1);
	}

	protected int getMaxFuse() {
		return 150;
	}

	@Override
	public int numberOfPackets() {
		return 1;
	}

	@Override
	protected void receivePacket(final int id, final byte[] data, final EntityPlayer player) {
		if (id == 0) {
			this.setFuseLength(data[0]);
		}
	}

	@Override
	protected void Save(final NBTTagCompound tagCompound, final int id) {
		tagCompound.setShort(this.generateNBTName("FuseLength", id), (short) this.getFuseLength());
		tagCompound.setShort(this.generateNBTName("Fuse", id), (short) this.getFuse());
	}

	@Override
	protected void Load(final NBTTagCompound tagCompound, final int id) {
		this.setFuseLength(tagCompound.getShort(this.generateNBTName("FuseLength", id)));
		this.setFuse(tagCompound.getShort(this.generateNBTName("Fuse", id)));
		this.createExplosives();
	}
}
