package stevesvehicles.client.localization.entry.block;

import stevesvehicles.api.modules.data.ILocalizedText;
import stevesvehicles.client.localization.LocalizedTextAdvanced;
import stevesvehicles.client.localization.LocalizedTextSimple;

public final class LocalizationLiquid {
	public static final ILocalizedText TITLE = createSimple("title");
	public static final ILocalizedText CHANGE_LAYOUT = createSimple("change_layout");
	public static final ILocalizedText SHARED_LAYOUT = createSimple("shared_layout");
	public static final ILocalizedText SIDE_LAYOUT = createSimple("side_layout");
	public static final ILocalizedText COLOR_LAYOUT = createSimple("color_layout");
	public static final ILocalizedText TRANSFER_ALL = createSimple("transfer_all");
	public static final ILocalizedText TRANSFER_BUCKETS = createAdvanced("transfer_buckets");
	public static final ILocalizedText TRANSFER_ALL_SHORT = createSimple("transfer_all_short");
	public static final ILocalizedText TRANSFER_BUCKETS_SHORT = createSimple("transfer_buckets_short");
	private static final String HEADER = "steves_vehicles:gui.liquid:";

	private static ILocalizedText createSimple(String code) {
		return new LocalizedTextSimple(HEADER + code);
	}

	private static ILocalizedText createAdvanced(String code) {
		return new LocalizedTextAdvanced(HEADER + code);
	}

	private LocalizationLiquid() {
	}
}
