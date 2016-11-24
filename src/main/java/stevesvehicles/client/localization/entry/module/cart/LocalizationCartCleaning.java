package stevesvehicles.client.localization.entry.module.cart;

import stevesvehicles.client.localization.ILocalizedText;
import stevesvehicles.client.localization.LocalizedTextAdvanced;
import stevesvehicles.client.localization.LocalizedTextSimple;

public final class LocalizationCartCleaning {
	public static final ILocalizedText TITLE = createSimple("title");
	public static final ILocalizedText LEVEL = createAdvanced("level");
	public static final ILocalizedText EXTRACT = createSimple("extract");
	public static final ILocalizedText PLAYER_LEVEL = createAdvanced("player_level");
	private static final String HEADER = "steves_vehicles:gui.cart.cleaning:experience_bank.";

	private static ILocalizedText createSimple(String code) {
		return new LocalizedTextSimple(HEADER + code);
	}

	private static ILocalizedText createAdvanced(String code) {
		return new LocalizedTextAdvanced(HEADER + code);
	}

	private LocalizationCartCleaning() {
	}
}
