package vswe.stevesvehicles.localization;

public class LocalizedTextSimple implements ILocalizedText {
	private String unlocalizedText;

	public LocalizedTextSimple(String unlocalizedText) {
		this.unlocalizedText = unlocalizedText;
	}

	@Override
	public String translate(String... params) {
		return StatCollector.translateToLocal(unlocalizedText);
	}
}
