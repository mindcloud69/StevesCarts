package stevesvehicles.client.localization;

import net.minecraft.util.text.translation.I18n;
import stevesvehicles.api.modules.data.ILocalizedText;

public class LocalizedTextSimple implements ILocalizedText {
	private String unlocalizedText;

	public LocalizedTextSimple(String unlocalizedText) {
		this.unlocalizedText = unlocalizedText;
	}

	@Override
	public String translate(String... params) {
		return I18n.translateToLocal(unlocalizedText);
	}
}
