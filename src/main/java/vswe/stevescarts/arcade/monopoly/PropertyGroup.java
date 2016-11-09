package vswe.stevescarts.arcade.monopoly;

import java.util.ArrayList;

public class PropertyGroup {
	private ArrayList<Property> properties;

	public PropertyGroup() {
		this.properties = new ArrayList<>();
	}

	public ArrayList<Property> getProperties() {
		return this.properties;
	}

	public void add(final Property property) {
		this.properties.add(property);
	}
}
