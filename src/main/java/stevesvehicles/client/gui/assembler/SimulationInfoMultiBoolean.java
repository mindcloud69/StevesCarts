package stevesvehicles.client.gui.assembler;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import stevesvehicles.api.modules.data.ILocalizedText;
import stevesvehicles.client.gui.screen.GuiCartAssembler;

public class SimulationInfoMultiBoolean extends SimulationInfo {
	private boolean[] values;

	public SimulationInfoMultiBoolean(ILocalizedText name, String texture, int count) {
		this(name, texture, count, false);
	}

	public SimulationInfoMultiBoolean(ILocalizedText name, String texture, int count, boolean defaultValue) {
		super(name, texture);
		values = new boolean[count];
		for (int i = 0; i < values.length; i++) {
			values[i] = defaultValue;
		}
	}

	@Override
	public boolean hasSubMenu() {
		return true;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void draw(GuiCartAssembler gui, int i, int x, int y) {
		super.draw(gui, i, x, y);
		if (getIsSubMenuOpen()) {
			int[] subRect = getSubRect(gui.getDropDownX(), gui.getDropDownY(), i);
			for (int j = 0; j < values.length; j++) {
				drawBooleanBox(gui, x, y, subRect[0] + getOffSetXForSubMenuBox(j, values.length), subRect[1] + 3, values[j]);
			}
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void onMouseClick(GuiCartAssembler gui, int i, int x, int y) {
		if (getIsSubMenuOpen()) {
			int[] subRect = getSubRect(gui.getDropDownX(), gui.getDropDownY(), i);
			for (int j = 0; j < values.length; j++) {
				if (clickBox(gui, x, y, subRect[0] + getOffSetXForSubMenuBox(j, values.length), subRect[1] + 3)) {
					values[j] = !values[j];
					break;
				}
			}
		}
	}

	public int getIntegerValue() {
		int val = 0;
		for (int i = 0; i < values.length; i++) {
			if (values[i]) {
				val |= 1 << i;
			}
		}
		return val;
	}
}
