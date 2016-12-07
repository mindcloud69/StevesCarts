package stevesvehicles.client.localization.entry.module.cart;

import stevesvehicles.api.modules.data.ILocalizedText;
import stevesvehicles.client.localization.LocalizedTextAdvanced;
import stevesvehicles.client.localization.LocalizedTextSimple;

public final class LocalizationCartDrillUtility {
	public static final ILocalizedText DRILL_INTELLIGENCE_LOCKED = createSimple("locked");
	public static final ILocalizedText DRILL_INTELLIGENCE_CHANGE = createSimple("change");
	public static final ILocalizedText DRILL_INTELLIGENCE_CURRENT = createAdvanced("current");
	private static final String HEADER = "steves_vehicles:gui.cart.drill_utility:drill_intelligence.";

	private static ILocalizedText createSimple(String code) {
		return new LocalizedTextSimple(HEADER + code);
	}

	private static ILocalizedText createAdvanced(String code) {
		return new LocalizedTextAdvanced(HEADER + code);
	}

	private LocalizationCartDrillUtility() {
	}
}
