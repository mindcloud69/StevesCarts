package stevesvehicles.common.core.tabs;

import stevesvehicles.client.localization.LocalizedTextSimple;

public class CreativeTabSimple extends CreativeTabCustom {
	public CreativeTabSimple(String label) {
		super(new LocalizedTextSimple("steves_vehicles:gui.tab:" + label));
	}
}
