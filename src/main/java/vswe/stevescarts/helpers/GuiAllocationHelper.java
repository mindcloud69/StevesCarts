package vswe.stevescarts.helpers;

import java.util.ArrayList;

import vswe.stevescarts.modules.ModuleBase;

public class GuiAllocationHelper {
	public int width;
	public int maxHeight;
	public ArrayList<ModuleBase> modules;

	public GuiAllocationHelper() {
		this.modules = new ArrayList<>();
	}
}
