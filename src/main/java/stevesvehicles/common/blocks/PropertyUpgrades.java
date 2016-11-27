package stevesvehicles.common.blocks;

import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.property.IUnlistedProperty;

public class PropertyUpgrades implements IUnlistedProperty<PropertyUpgrades.Upgrades> {
	@Override
	public String getName() {
		return "upgrades";
	}

	@Override
	public boolean isValid(Upgrades value) {
		return true;
	}

	@Override
	public Class<Upgrades> getType() {
		return Upgrades.class;
	}

	@Override
	public String valueToString(Upgrades value) {
		return null;
	}

	public static class Upgrades {
		public static final Upgrades EMPTY = new Upgrades();
		static {
			EMPTY.upgrades = ImmutableMap.of();
		}
		public Map<EnumFacing, Integer> upgrades = Maps.newLinkedHashMap();
	}
}
