package vswe.stevesvehicles.module.common.engine;

import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import vswe.stevesvehicles.client.gui.screen.GuiVehicle;
import vswe.stevesvehicles.localization.entry.module.LocalizationEngine;
import vswe.stevesvehicles.vehicle.VehicleBase;

public class ModuleCheatEngine extends ModuleEngine {
	private DataParameter<Integer> PRIORITY;
	public ModuleCheatEngine(VehicleBase vehicleBase) {
		super(vehicleBase);
	}

	@Override
	public void initDw() {
		PRIORITY = createDw(DataSerializers.VARINT);
		super.initDw();
	}

	@Override
	protected DataParameter<Integer> getPriorityDw() {
		return PRIORITY;
	}

	@Override
	public void loadFuel() {
		// no reason to load fuel
	}

	@Override
	public int getFuelLevel() {
		return 9001;
	}

	@Override
	public void setFuelLevel(int val) {
		// in your face
	}

	@Override
	public void drawForeground(GuiVehicle gui) {
		String[] split = getModuleName().split(" ");
		drawString(gui, split[0], 8, 6, 0x404040);
		if (split.length > 1) {
			drawString(gui, split[1], 8, 16, 0x404040);
		}
		drawString(gui, LocalizationEngine.CREATIVE_POWER.translate(String.valueOf(getFuelLevel())), 8, 42, 0x404040);
	}

	@Override
	public int getTotalFuel() {
		return 9001000;
	}

	@Override
	public float[] getGuiBarColor() {
		return new float[] { 0.97F, 0.58F, 0.11F };
	}

	@Override
	public boolean hasSlots() {
		return false;
	}
}