package stevesvehicles.client.localization.entry.module.cart;

import stevesvehicles.client.localization.ILocalizedText;
import stevesvehicles.client.localization.LocalizedTextSimple;

public final class LocalizationCartCultivationUtil {
	public static final ILocalizedText PLANTER_RANGE_TITLE = createSimple("planter_range_extender.title");
	public static final ILocalizedText PLANTER_RANGE_SIZE = createSimple("planter_range_extender.size");
	public static final ILocalizedText FERTILIZER_TITLE = createSimple("fertilizer.title");
	private static final String HEADER = "steves_vehicles:gui.cart.cultivation:";

	private static ILocalizedText createSimple(String code) {
		return new LocalizedTextSimple(HEADER + code);
	}

	private LocalizationCartCultivationUtil() {
	}
}
