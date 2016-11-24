package stevesvehicles.common.arcade.monopoly;

import java.util.ArrayList;

public class PropertyGroup {
	private ArrayList<Property> properties;

	public PropertyGroup() {
		properties = new ArrayList<>();
	}

	public ArrayList<Property> getProperties() {
		return properties;
	}

	public void add(Property property) {
		properties.add(property);
	}
}
