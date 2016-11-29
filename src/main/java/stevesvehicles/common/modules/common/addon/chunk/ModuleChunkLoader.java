package stevesvehicles.common.modules.common.addon.chunk;

import java.io.IOException;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import stevesvehicles.api.network.DataReader;
import stevesvehicles.client.gui.screen.GuiVehicle;
import stevesvehicles.client.localization.entry.module.LocalizationIndependence;
import stevesvehicles.common.modules.IActivatorModule;
import stevesvehicles.common.modules.common.addon.ModuleAddon;
import stevesvehicles.common.vehicles.VehicleBase;

public class ModuleChunkLoader extends ModuleAddon implements IActivatorModule {
	private DataParameter<Boolean> LOADING_CHUNK;

	public ModuleChunkLoader(VehicleBase vehicleBase) {
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
	public void drawForeground(GuiVehicle gui) {
		drawString(gui, getModuleName(), 8, 6, 0x404040);
	}

	private int cooldown;

	@Override
	public void update() {
		super.update();
		if (!rdyToInit) {
			rdyToInit = true;
		}
		if (isLoadingChunk() && !getVehicle().getWorld().isRemote) {
			if (getVehicle().hasFuelForModule()) {
				cooldown = 20;
			} else {
				if (cooldown > 0) {
					cooldown--;
				} else {
					setChunkLoading(false);
				}
			}
		}
	}

	private boolean rdyToInit;

	public void setChunkLoading(boolean val) {
		if (!isPlaceholder()) {
			updateDw(LOADING_CHUNK, val);
			// just to make sure
			if (!getVehicle().getWorld().isRemote && rdyToInit) {
				if (val) {
					getVehicle().initChunkLoading();
				} else {
					getVehicle().dropChunkLoading();
				}
			}
		}
	}

	private boolean isLoadingChunk() {
		return !isPlaceholder() && getDw(LOADING_CHUNK);
	}

	@Override
	public int guiWidth() {
		return 80;
	}

	@Override
	public int guiHeight() {
		return 40;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void drawBackground(GuiVehicle gui, int x, int y) {
		drawToggleBox(gui, "chunk", isLoadingChunk(), x, y);
	}

	@Override
	public void drawMouseOver(GuiVehicle gui, int x, int y) {
		drawStringOnMouseOver(gui, getStateName(), x, y, TOGGLE_IMAGE_RECT);
	}

	private String getStateName() {
		return LocalizationIndependence.CHUNK.translate(isLoadingChunk() ? "1" : "0");
	}

	@Override
	public void mouseClicked(GuiVehicle gui, int x, int y, int button) throws IOException {
		if (button == 0) {
			if (inRect(x, y, TOGGLE_BOX_RECT)) {
				sendPacketToServer(getDataWriter());
			}
		}
	}

	@Override
	protected void receivePacket(DataReader dr, EntityPlayer player) throws IOException {
		setChunkLoading(!isLoadingChunk());
	}

	@Override
	public int numberOfDataWatchers() {
		return 1;
	}

	@Override
	public void initDw() {
		LOADING_CHUNK = createDw(DataSerializers.BOOLEAN);
		registerDw(LOADING_CHUNK, false);
	}

	@Override
	public int getConsumption(boolean isMoving) {
		return isLoadingChunk() ? 5 : super.getConsumption(isMoving);
	}

	@Override
	protected void save(NBTTagCompound tagCompound) {
		tagCompound.setBoolean("ChunkLoading", isLoadingChunk());
		tagCompound.setByte("ChunkLoadingCooldown", (byte) cooldown);
	}

	@Override
	protected void load(NBTTagCompound tagCompound) {
		setChunkLoading(tagCompound.getBoolean("ChunkLoading"));
		cooldown = tagCompound.getByte("ChunkLoadingCooldown");
	}

	@Override
	public void doActivate(int id) {
		setChunkLoading(true);
	}

	@Override
	public void doDeActivate(int id) {
		setChunkLoading(false);
	}

	@Override
	public boolean isActive(int id) {
		return isLoadingChunk();
	}
}
