package stevesvehicles.common.modules.datas;

import stevesvehicles.api.modules.data.ILocalizedText;
import stevesvehicles.api.modules.data.IModuleSide;
import stevesvehicles.client.localization.entry.info.LocalizationLabel;

public enum ModuleSide implements IModuleSide {
	TOP(LocalizationLabel.TOP), CENTER(LocalizationLabel.CENTER), BOTTOM(LocalizationLabel.BOTTOM), BACK(LocalizationLabel.BACK), LEFT(LocalizationLabel.LEFT), RIGHT(LocalizationLabel.RIGHT), FRONT(LocalizationLabel.FRONT);
	private ILocalizedText name;

	private ModuleSide(ILocalizedText name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return name.translate();
	}
}
