package vswe.stevescarts.ModuleData;

import vswe.stevescarts.Helpers.ColorHelper;
import vswe.stevescarts.Helpers.Localization;
import vswe.stevescarts.Modules.ModuleBase;

import java.util.List;

public class ModuleDataHull extends ModuleData {
	private int modularCapacity;
	private int engineMaxCount;
	private int addonMaxCount;
	private int complexityMax;

	public ModuleDataHull(final int id, final String name, final Class<? extends ModuleBase> moduleClass) {
		super(id, name, moduleClass, 0);
	}

	public ModuleDataHull setCapacity(final int val) {
		this.modularCapacity = val;
		return this;
	}

	public ModuleDataHull setEngineMax(final int val) {
		this.engineMaxCount = val;
		return this;
	}

	public ModuleDataHull setAddonMax(final int val) {
		this.addonMaxCount = val;
		return this;
	}

	public ModuleDataHull setComplexityMax(final int val) {
		this.complexityMax = val;
		return this;
	}

	public int getEngineMax() {
		return this.engineMaxCount;
	}

	public int getAddonMax() {
		return this.addonMaxCount;
	}

	public int getCapacity() {
		return this.modularCapacity;
	}

	public int getComplexityMax() {
		return this.complexityMax;
	}

	@Override
	public void addSpecificInformation(final List list) {
		list.add(ColorHelper.YELLOW + Localization.MODULE_INFO.MODULAR_CAPACITY.translate(String.valueOf(this.modularCapacity)));
		list.add(ColorHelper.PURPLE + Localization.MODULE_INFO.COMPLEXITY_CAP.translate(String.valueOf(this.complexityMax)));
		list.add(ColorHelper.ORANGE + Localization.MODULE_INFO.MAX_ENGINES.translate(String.valueOf(this.engineMaxCount)));
		list.add(ColorHelper.GREEN + Localization.MODULE_INFO.MAX_ADDONS.translate(String.valueOf(this.addonMaxCount)));
	}
}
