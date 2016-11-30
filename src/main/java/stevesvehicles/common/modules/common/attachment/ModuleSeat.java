package stevesvehicles.common.modules.common.attachment;

import java.io.IOException;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import stevesvehicles.api.network.DataReader;
import stevesvehicles.client.ResourceHelper;
import stevesvehicles.client.gui.screen.GuiVehicle;
import stevesvehicles.client.localization.entry.module.LocalizationTravel;
import stevesvehicles.common.modules.cart.attachment.ModuleAttachment;
import stevesvehicles.common.vehicles.VehicleBase;

public class ModuleSeat extends ModuleAttachment {
	public ModuleSeat(VehicleBase vehicleBase) {
		super(vehicleBase);
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
	public void drawForeground(GuiVehicle gui) {
		drawString(gui, getModuleName(), 8, 6, 0x404040);
	}

	private static final int TEXTURE_SPACING = 1;
	private static final ResourceLocation TEXTURE = ResourceHelper.getResource("/gui/chair.png");

	@Override
	@SideOnly(Side.CLIENT)
	public void drawBackground(GuiVehicle gui, int x, int y) {
		ResourceHelper.bindResource(TEXTURE);
		int imageID = getState();
		int borderID = 0;
		if (inRect(x, y, BUTTON_RECT)) {
			if (imageID == 0) {
				borderID = 2;
			} else {
				borderID = 1;
			}
		}
		drawImage(gui, BUTTON_RECT, TEXTURE_SPACING, TEXTURE_SPACING + (TEXTURE_SPACING + BUTTON_RECT[3]) * borderID);
		int srcY = TEXTURE_SPACING + (TEXTURE_SPACING + BUTTON_RECT[3]) * 3 + imageID * (TEXTURE_SPACING + BUTTON_RECT[3] - 2);
		drawImage(gui, BUTTON_RECT[0] + 1, BUTTON_RECT[1] + 1, TEXTURE_SPACING, srcY, BUTTON_RECT[2] - 2, BUTTON_RECT[3] - 2);
	}

	private static final int[] BUTTON_RECT = new int[] { 20, 20, 24, 12 };

	@Override
	public void drawMouseOver(GuiVehicle gui, int x, int y) {
		drawStringOnMouseOver(gui, getStateName(), x, y, BUTTON_RECT);
	}

	private int getState() {
		if (getVehicle().getEntity().getControllingPassenger() == null) {
			return 1;
		} else if (getVehicle().getEntity().getControllingPassenger() == getClientPlayer()) {
			return 2;
		} else {
			return 0;
		}
	}

	private String getStateName() {
		return LocalizationTravel.SEAT_MESSAGE.translate(String.valueOf(getState()));
	}

	@Override
	public void mouseClicked(GuiVehicle gui, int x, int y, int button) throws IOException {
		if (button == 0) {
			if (inRect(x, y, BUTTON_RECT)) {
				sendPacketToServer(getDataWriter());
			}
		}
	}

	@Override
	public void readData(DataReader dr, EntityPlayer player) throws IOException {
		if (player != null) {
			if (getVehicle().getEntity().getControllingPassenger() == null) {
				player.startRiding(getVehicle().getEntity());
			} else if (getVehicle().getEntity().getControllingPassenger() == player) {
				player.dismountRidingEntity();
			}
		}
	}

	@Override
	public void update() {
		super.update();
		if (getVehicle().getEntity().getControllingPassenger() != null) {
			relative = false;
			chairAngle = (float) (Math.PI + Math.PI * getVehicle().getEntity().getControllingPassenger().rotationYaw / 180F);
		} else {
			relative = true;
			chairAngle = (float) Math.PI / 2;
		}
	}

	private boolean relative;
	private float chairAngle;

	public float getChairAngle() {
		return chairAngle;
	}

	public boolean useRelativeRender() {
		return relative;
	}

	@Override
	public float mountedOffset(Entity rider) {
		return -0.1F;
	}
}
