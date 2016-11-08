package vswe.stevescarts.Helpers;

public abstract class LabelInformation {
	private Localization.MODULES.ADDONS name;

	public LabelInformation(final Localization.MODULES.ADDONS name) {
		this.name = name;
	}

	public String getName() {
		return this.name.translate();
	}

	public abstract String getLabel();
}
