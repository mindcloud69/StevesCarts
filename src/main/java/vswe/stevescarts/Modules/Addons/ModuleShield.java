package vswe.stevescarts.Modules.Addons;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vswe.stevescarts.Carts.MinecartModular;
import vswe.stevescarts.Helpers.Localization;
import vswe.stevescarts.Helpers.ResourceHelper;
import vswe.stevescarts.Interfaces.GuiMinecart;
import vswe.stevescarts.Modules.IActivatorModule;

public class ModuleShield extends ModuleAddon implements IActivatorModule {
	private boolean shield;
	private float shieldDistance;
	private float shieldAngle;
	private int[] buttonRect;

	public ModuleShield(final MinecartModular cart) {
		super(cart);
		this.shield = true;
		this.shieldDistance = 18.0f;
		this.buttonRect = new int[] { 20, 20, 24, 12 };
	}

	protected boolean shieldSetting() {
		return this.getShieldStatus();
	}

	public float getShieldDistance() {
		return this.shieldDistance;
	}

	public float getShieldAngle() {
		return this.shieldAngle;
	}

	public boolean hasShield() {
		return this.shield;
	}

	@Override
	public void update() {
		super.update();
		if (this.hasShield() && !this.getCart().hasFuelForModule() && !this.getCart().worldObj.isRemote) {
			this.setShieldStatus(false);
		}
		if (this.shield) {
			this.getCart().extinguish();
		}
		if (!this.getShieldStatus() && this.shieldDistance > 0.0f) {
			this.shieldDistance -= 0.25f;
			if (this.shieldDistance <= 0.0f) {
				this.shield = false;
			}
		} else if (this.getShieldStatus() && this.shieldDistance < 18.0f) {
			this.shieldDistance += 0.25f;
			this.shield = true;
		}
		if (this.shield) {
			this.shieldAngle = (float) ((this.shieldAngle + 0.125f) % 314.1592653589793);
		}
	}

	public boolean receiveDamage(final DamageSource source, final int val) {
		return !this.hasShield();
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
		return 75;
	}

	@Override
	public int guiHeight() {
		return 35;
	}

	@Override
	public void drawForeground(final GuiMinecart gui) {
		this.drawString(gui, this.getModuleName(), 8, 6, 4210752);
	}

	public void setShieldStatus(final boolean val) {
		if (!this.isPlaceholder()) {
			this.updateDw(0, (byte) (val ? 1 : 0));
		}
	}

	private boolean getShieldStatus() {
		if (this.isPlaceholder()) {
			return this.getSimInfo().getShieldActive();
		}
		return this.getDw(0) != 0;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void drawBackground(final GuiMinecart gui, final int x, final int y) {
		ResourceHelper.bindResource("/gui/shield.png");
		final int imageID = this.getShieldStatus() ? 1 : 0;
		int borderID = 0;
		if (this.inRect(x, y, this.buttonRect)) {
			borderID = 1;
		}
		this.drawImage(gui, this.buttonRect, 0, this.buttonRect[3] * borderID);
		final int srcY = this.buttonRect[3] * 2 + imageID * (this.buttonRect[3] - 2);
		this.drawImage(gui, this.buttonRect[0] + 1, this.buttonRect[1] + 1, 0, srcY, this.buttonRect[2] - 2, this.buttonRect[3] - 2);
	}

	@Override
	public void drawMouseOver(final GuiMinecart gui, final int x, final int y) {
		this.drawStringOnMouseOver(gui, this.getStateName(), x, y, this.buttonRect);
	}

	private String getStateName() {
		return Localization.MODULES.ADDONS.SHIELD.translate(this.getShieldStatus() ? "1" : "0");
	}

	@Override
	public void mouseClicked(final GuiMinecart gui, final int x, final int y, final int button) {
		if (button == 0 && this.inRect(x, y, this.buttonRect)) {
			this.sendPacket(0);
		}
	}

	@Override
	protected void receivePacket(final int id, final byte[] data, final EntityPlayer player) {
		if (id == 0) {
			this.updateDw(0, this.getShieldStatus() ? 0 : 1);
		}
	}

	public int numberOfPackets() {
		return 1;
	}

	@Override
	public int numberOfDataWatchers() {
		return 1;
	}

	@Override
	public void initDw() {
		this.addDw(0, 0);
	}

	@Override
	public int getConsumption(final boolean isMoving) {
		return this.hasShield() ? 20 : super.getConsumption(isMoving);
	}

	@Override
	protected void Save(final NBTTagCompound tagCompound, final int id) {
		tagCompound.setBoolean(this.generateNBTName("Shield", id), this.getShieldStatus());
	}

	@Override
	protected void Load(final NBTTagCompound tagCompound, final int id) {
		this.setShieldStatus(tagCompound.getBoolean(this.generateNBTName("Shield", id)));
	}

	@Override
	public void doActivate(final int id) {
		this.setShieldStatus(true);
	}

	@Override
	public void doDeActivate(final int id) {
		this.setShieldStatus(false);
	}

	@Override
	public boolean isActive(final int id) {
		return this.getShieldStatus();
	}
}
