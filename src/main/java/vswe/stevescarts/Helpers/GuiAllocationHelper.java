package vswe.stevescarts.Helpers;

import vswe.stevescarts.Modules.ModuleBase;

import java.util.ArrayList;

public class GuiAllocationHelper {
	public int width;
	public int maxHeight;
	public ArrayList<ModuleBase> modules;

	public GuiAllocationHelper() {
		this.modules = new ArrayList<ModuleBase>();
	}
}
