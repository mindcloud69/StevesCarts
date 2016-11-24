package stevesvehicles.client.localization.entry.module;

import stevesvehicles.client.localization.ILocalizedText;
import stevesvehicles.client.localization.LocalizedTextAdvanced;
import stevesvehicles.client.localization.LocalizedTextSimple;

public final class LocalizationCake {
	public static final ILocalizedText TITLE = createSimple("title");
	public static final ILocalizedText CAKES_LABEL = createAdvanced("cakes_label");
	public static final ILocalizedText SLICES_LABEL = createAdvanced("slices_label");
	private static final String HEADER = "steves_vehicles:gui.common.cake:common.";

	private static ILocalizedText createSimple(String code) {
		return new LocalizedTextSimple(HEADER + code);
	}

	private static ILocalizedText createAdvanced(String code) {
		return new LocalizedTextAdvanced(HEADER + code);
	}

	private LocalizationCake() {
	}
}
