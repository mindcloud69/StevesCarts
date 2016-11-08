package vswe.stevescarts.Arcade;

import java.util.ArrayList;

public class PropertyGroup {
	private ArrayList<Property> properties;

	public PropertyGroup() {
		this.properties = new ArrayList<Property>();
	}

	public ArrayList<Property> getProperties() {
		return this.properties;
	}

	public void add(final Property property) {
		this.properties.add(property);
	}
}
