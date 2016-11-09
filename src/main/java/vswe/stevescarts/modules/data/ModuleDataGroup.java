package vswe.stevescarts.modules.data;

import java.util.ArrayList;

import vswe.stevescarts.helpers.Localization;

public class ModuleDataGroup {
	private Localization.MODULE_INFO name;
	private ArrayList<ModuleData> modules;
	private int count;

	public ModuleDataGroup(final Localization.MODULE_INFO name) {
		this.name = name;
		this.count = 1;
		this.modules = new ArrayList<>();
	}

	public String getName() {
		return this.name.translate(String.valueOf(this.getCount()));
	}

	public ArrayList<ModuleData> getModules() {
		return this.modules;
	}

	public int getCount() {
		return this.count;
	}

	public ModuleDataGroup add(final ModuleData module) {
		this.modules.add(module);
		return this;
	}

	public ModuleDataGroup setCount(final int count) {
		this.count = count;
		return this;
	}

	public ModuleDataGroup copy() {
		final ModuleDataGroup newObj = new ModuleDataGroup(this.name).setCount(this.getCount());
		for (final ModuleData obj : this.getModules()) {
			newObj.add(obj);
		}
		return newObj;
	}

	public ModuleDataGroup copy(final int count) {
		final ModuleDataGroup newObj = new ModuleDataGroup(this.name).setCount(count);
		for (final ModuleData obj : this.getModules()) {
			newObj.add(obj);
		}
		return newObj;
	}

	public String getCountName() {
		switch (this.count) {
			case 1: {
				return Localization.MODULE_INFO.MODULE_COUNT_1.translate();
			}
			case 2: {
				return Localization.MODULE_INFO.MODULE_COUNT_2.translate();
			}
			case 3: {
				return Localization.MODULE_INFO.MODULE_COUNT_3.translate();
			}
			default: {
				return "???";
			}
		}
	}

	public static ModuleDataGroup getCombinedGroup(final Localization.MODULE_INFO name, final ModuleDataGroup group1, final ModuleDataGroup group2) {
		final ModuleDataGroup newgroup = group1.copy();
		newgroup.add(group2);
		newgroup.name = name;
		return newgroup;
	}

	public void add(final ModuleDataGroup group) {
		for (final ModuleData obj : group.getModules()) {
			this.add(obj);
		}
	}
}
