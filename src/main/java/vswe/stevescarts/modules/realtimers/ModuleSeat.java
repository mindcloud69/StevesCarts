package vswe.stevescarts.modules.realtimers;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vswe.stevescarts.entitys.EntityMinecartModular;
import vswe.stevescarts.guis.GuiMinecart;
import vswe.stevescarts.helpers.Localization;
import vswe.stevescarts.helpers.ResourceHelper;
import vswe.stevescarts.modules.ModuleBase;

public class ModuleSeat extends ModuleBase {
	private int[] buttonRect;
	private boolean relative;
	private float chairAngle;

	public ModuleSeat(final EntityMinecartModular cart) {
		super(cart);
		this.buttonRect = new int[] { 20, 20, 24, 12 };
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
		return 55;
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
		ResourceHelper.bindResource("/gui/chair.png");
		final int imageID = this.getState();
		int borderID = 0;
		if (this.inRect(x, y, this.buttonRect)) {
			if (imageID == 0) {
				borderID = 2;
			} else {
				borderID = 1;
			}
		}
		this.drawImage(gui, this.buttonRect, 0, this.buttonRect[3] * borderID);
		final int srcY = this.buttonRect[3] * 3 + imageID * (this.buttonRect[3] - 2);
		this.drawImage(gui, this.buttonRect[0] + 1, this.buttonRect[1] + 1, 0, srcY, this.buttonRect[2] - 2, this.buttonRect[3] - 2);
	}

	@Override
	public void drawMouseOver(final GuiMinecart gui, final int x, final int y) {
		this.drawStringOnMouseOver(gui, this.getStateName(), x, y, this.buttonRect);
	}

	private int getState() {
		if (this.getCart().getRidingEntity() == null) {
			return 1;
		}
		if (this.getCart().getRidingEntity() == this.getClientPlayer()) {
			return 2;
		}
		return 0;
	}

	private String getStateName() {
		return Localization.MODULES.ATTACHMENTS.SEAT_MESSAGE.translate(String.valueOf(this.getState()));
	}

	@Override
	public void mouseClicked(final GuiMinecart gui, final int x, final int y, final int button) {
		if (button == 0 && this.inRect(x, y, this.buttonRect)) {
			this.sendPacket(0);
		}
	}

	@Override
	protected void receivePacket(final int id, final byte[] data, final EntityPlayer player) {
		if (id == 0 && player != null) {
			if (this.getCart().getRidingEntity() == null) {
				player.startRiding(this.getCart());
			} else if (this.getCart().getRidingEntity() == player) {
				player.dismountRidingEntity();
			}
		}
	}

	@Override
	public int numberOfPackets() {
		return 1;
	}

	@Override
	public void update() {
		super.update();
		if (this.getCart().getRidingEntity() != null) {
			this.relative = false;
			this.chairAngle = (float) (3.141592653589793 + 3.141592653589793 * this.getCart().getRidingEntity().rotationYaw / 180.0);
		} else {
			this.relative = true;
			this.chairAngle = 1.5707964f;
		}
	}

	public float getChairAngle() {
		return this.chairAngle;
	}

	public boolean useRelativeRender() {
		return this.relative;
	}

	@Override
	public float mountedOffset(final Entity rider) {
		return -0.1f;
	}
}
