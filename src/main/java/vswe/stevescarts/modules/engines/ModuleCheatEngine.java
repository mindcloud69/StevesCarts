package vswe.stevescarts.modules.engines;

import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import vswe.stevescarts.entitys.EntityMinecartModular;
import vswe.stevescarts.guis.GuiMinecart;
import vswe.stevescarts.helpers.Localization;

public class ModuleCheatEngine extends ModuleEngine {
	private DataParameter<Integer> PRIORITY;

	public ModuleCheatEngine(final EntityMinecartModular cart) {
		super(cart);
	}

	@Override
	protected DataParameter<Integer> getPriorityDw() {
		return PRIORITY;
	}

	@Override
	public void loadFuel() {
	}

	@Override
	public int getFuelLevel() {
		return 9001;
	}

	@Override
	public void initDw() {
		PRIORITY = createDw(DataSerializers.VARINT);
		super.initDw();
	}

	@Override
	public void setFuelLevel(final int val) {
	}

	@Override
	public void drawForeground(final GuiMinecart gui) {
		final String[] split = getModuleName().split(" ");
		drawString(gui, split[0], 8, 6, 4210752);
		if (split.length > 1) {
			drawString(gui, split[1], 8, 16, 4210752);
		}
		drawString(gui, Localization.MODULES.ENGINES.OVER_9000.translate(String.valueOf(getFuelLevel())), 8, 42, 4210752);
	}

	@Override
	public int getTotalFuel() {
		return 9001000;
	}

	@Override
	public float[] getGuiBarColor() {
		return new float[] { 0.97f, 0.58f, 0.11f };
	}

	@Override
	public boolean hasSlots() {
		return false;
	}
}
