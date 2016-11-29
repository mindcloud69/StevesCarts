package stevesvehicles.common.modules.cart.addon.cultivation;

import java.io.IOException;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import stevesvehicles.api.network.DataReader;
import stevesvehicles.api.network.DataWriter;
import stevesvehicles.client.ResourceHelper;
import stevesvehicles.client.gui.screen.GuiVehicle;
import stevesvehicles.client.localization.entry.module.cart.LocalizationCartCultivationUtil;
import stevesvehicles.common.modules.common.addon.ModuleAddon;
import stevesvehicles.common.vehicles.VehicleBase;

public class ModulePlantSize extends ModuleAddon {
	public ModulePlantSize(VehicleBase vehicleBase) {
		super(vehicleBase);
	}

	private int size = 1;

	public int getSize() {
		return size;
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
		return 70;
	}

	@Override
	public void drawForeground(GuiVehicle gui) {
		drawString(gui, LocalizationCartCultivationUtil.PLANTER_RANGE_TITLE.translate(), 8, 6, 0x404040);
	}

	private static final ResourceLocation TEXTURE = ResourceHelper.getResource("/gui/plant_size.png");

	@Override
	@SideOnly(Side.CLIENT)
	public void drawBackground(GuiVehicle gui, int x, int y) {
		ResourceHelper.bindResource(TEXTURE);
		int srcX = 1 + ((size - 1) % 5) * 45;
		int srcY = 1 + ((size - 1) / 5 + 1) * 45;
		drawImage(gui, box, srcX, srcY);
		if (inRect(x, y, box)) {
			drawImage(gui, box, 1, 1);
		}
	}

	private int[] box = new int[] { 10, 18, 44, 44 };

	@Override
	public void drawMouseOver(GuiVehicle gui, int x, int y) {
		drawStringOnMouseOver(gui, LocalizationCartCultivationUtil.PLANTER_RANGE_SIZE.translate() + ": " + size + "x" + size, x, y, box);
	}

	@Override
	public void mouseClicked(GuiVehicle gui, int x, int y, int button) throws IOException {
		if (button == 0 || button == 1) {
			if (inRect(x, y, box)) {
				DataWriter dw = getDataWriter();
				dw.writeBoolean(button == 0);
				sendPacketToServer(dw);
			}
		}
	}

	@Override
	protected void receivePacket(DataReader dr, EntityPlayer player) throws IOException {
		if (dr.readBoolean()) {
			size--;
			if (size < 1) {
				size = 7;
			}
		} else {
			size++;
			if (size > 7) {
				size = 1;
			}
		}
	}

	@Override
	public int numberOfGuiData() {
		return 1;
	}

	@Override
	protected void checkGuiData(Object[] info) {
		updateGuiData(info, 0, (short) size);
	}

	@Override
	public void receiveGuiData(int id, short data) {
		if (id == 0) {
			size = data;
		}
	}

	@Override
	protected void save(NBTTagCompound tagCompound) {
		tagCompound.setByte("size", (byte) size);
	}

	@Override
	protected void load(NBTTagCompound tagCompound) {
		size = tagCompound.getByte("size");
	}
}
