package vswe.stevesvehicles.block;

import java.util.Collection;

import com.google.common.base.Optional;

import net.minecraft.block.properties.PropertyHelper;
import vswe.stevesvehicles.upgrade.Upgrade;
import vswe.stevesvehicles.upgrade.registry.UpgradeRegistry;

public class PropertyUpgrade extends PropertyHelper<Upgrade> {
	public PropertyUpgrade(String name) {
		super(name, Upgrade.class);
	}

	@Override
	public Collection<Upgrade> getAllowedValues() {
		return UpgradeRegistry.getAllUpgrades();
	}

	@Override
	public Optional<Upgrade> parseValue(String value) {
		return null;
	}

	@Override
	public String getName(Upgrade value) {
		return value.getName();
	}
}
