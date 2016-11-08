package vswe.stevescarts.Helpers;

import vswe.stevescarts.Modules.ModuleBase;

public class ActivatorOption {
	private Class<? extends ModuleBase> module;
	private int id;
	private Localization.GUI.TOGGLER name;
	private int option;

	public ActivatorOption(final Localization.GUI.TOGGLER name, final Class<? extends ModuleBase> module, final int id) {
		this.name = name;
		this.module = module;
		this.id = id;
	}

	public ActivatorOption(final Localization.GUI.TOGGLER name, final Class<? extends ModuleBase> module) {
		this(name, module, 0);
	}

	public Class<? extends ModuleBase> getModule() {
		return this.module;
	}

	public String getName() {
		return this.name.translate();
	}

	public int getOption() {
		return this.option;
	}

	public int getId() {
		return this.id;
	}

	public void setOption(final int val) {
		this.option = val;
	}

	public void changeOption(final boolean dif) {
		if (dif) {
			if (++this.option > 5) {
				this.option = 0;
			}
		} else if (--this.option < 0) {
			this.option = 5;
		}
	}

	public boolean isDisabled() {
		return this.option == 0;
	}

	public boolean shouldActivate(final boolean isOrange) {
		return this.option == 2 || (this.option == 4 && !isOrange) || (this.option == 5 && isOrange);
	}

	public boolean shouldDeactivate(final boolean isOrange) {
		return this.option == 1 || (this.option == 4 && isOrange) || (this.option == 5 && !isOrange);
	}

	public boolean shouldToggle() {
		return this.option == 3;
	}

	public String getInfo() {
		if (this.isDisabled()) {
			return Localization.GUI.TOGGLER.SETTING_DISABLED.translate();
		}
		return "�6" + Localization.GUI.TOGGLER.SETTING_ORANGE.translate() + ": " + (this.shouldActivate(true) ? ("�2" + Localization.GUI.TOGGLER.STATE_ACTIVATE.translate())
				: (this.shouldDeactivate(true)
						? ("�4" + Localization.GUI.TOGGLER.STATE_DEACTIVATE.translate())
								: ("�E" + Localization.GUI.TOGGLER.STATE_TOGGLE.translate()))) + "\n" + "�1" + Localization.GUI.TOGGLER.SETTING_BLUE.translate() + ": " + (
										this.shouldActivate(false) ? ("�2" + Localization.GUI.TOGGLER.STATE_ACTIVATE.translate())
												: (this.shouldDeactivate(false) ? ("�4" + Localization.GUI.TOGGLER.STATE_DEACTIVATE.translate())
														: ("�E" + Localization.GUI.TOGGLER.STATE_TOGGLE.translate())));
	}
}
