package stevesvehicles.common.modules.common.addon;

import java.io.IOException;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import stevesvehicles.client.gui.assembler.SimulationInfo;
import stevesvehicles.client.gui.assembler.SimulationInfoBoolean;
import stevesvehicles.client.gui.screen.GuiVehicle;
import stevesvehicles.client.localization.entry.block.LocalizationAssembler;
import stevesvehicles.client.localization.entry.module.LocalizationVisual;
import stevesvehicles.common.modules.IActivatorModule;
import stevesvehicles.common.network.DataReader;
import stevesvehicles.common.vehicles.VehicleBase;

public class ModuleInvisible extends ModuleAddon implements IActivatorModule {
	private DataParameter<Boolean> VISABLE;

	public ModuleInvisible(VehicleBase vehicleBase) {
		super(vehicleBase);
	}

	@Override
	public void loadSimulationInfo(List<SimulationInfo> simulationInfo) {
		simulationInfo.add(new SimulationInfoBoolean(LocalizationAssembler.INFO_INVISIBLE, "invisible"));
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

	@Override
	public int guiWidth() {
		return 90;
	}

	@Override
	public int guiHeight() {
		return 40;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void drawBackground(GuiVehicle gui, int x, int y) {
		drawToggleBox(gui, "invisible", !isVisible(), x, y);
	}

	@Override
	public void drawMouseOver(GuiVehicle gui, int x, int y) {
		drawStringOnMouseOver(gui, getStateName(), x, y, TOGGLE_IMAGE_RECT);
	}

	@Override
	public void update() {
		super.update();
		if (!isVisible() && !getVehicle().hasFuelForModule() && !getVehicle().getWorld().isRemote) {
			setIsVisible(true);
		}
	}

	private boolean isVisible() {
		if (isPlaceholder()) {
			return !getBooleanSimulationInfo();
		} else {
			return getDw(VISABLE);
		}
	}

	private String getStateName() {
		return LocalizationVisual.INVISIBILITY_TOGGLE.translate(isVisible() ? "0" : "1");
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
	public void readData(DataReader dr, EntityPlayer player) {
		setIsVisible(!isVisible());
	}

	public void setIsVisible(boolean val) {
		updateDw(VISABLE, val);
	}

	@Override
	public boolean shouldVehicleRender() {
		return isVisible();
	}

	@Override
	public int numberOfDataWatchers() {
		return 1;
	}

	@Override
	public void initDw() {
		VISABLE = createDw(DataSerializers.BOOLEAN);
		registerDw(VISABLE, true);
	}

	@Override
	public int getConsumption(boolean isMoving) {
		return isVisible() ? super.getConsumption(isMoving) : 3;
	}

	@Override
	protected void save(NBTTagCompound tagCompound) {
		tagCompound.setBoolean("Invisible", !isVisible());
	}

	@Override
	protected void load(NBTTagCompound tagCompound) {
		setIsVisible(!tagCompound.getBoolean("Invisible"));
	}

	@Override
	public void doActivate(int id) {
		setIsVisible(false);
	}

	@Override
	public void doDeActivate(int id) {
		setIsVisible(true);
	}

	@Override
	public boolean isActive(int id) {
		return !isVisible();
	}
}
