package stevesvehicles.common.blocks.tileentitys.detector.modulestate.registry;

import stevesvehicles.common.blocks.tileentitys.detector.modulestate.ModuleState;
import stevesvehicles.common.blocks.tileentitys.detector.modulestate.ModuleStatePower;

public class ModuleStateRegistryPower extends ModuleStateRegistry {
	public ModuleStateRegistryPower() {
		super("power");
		createAndRegisterPower("red", 0);
		createAndRegisterPower("blue", 1);
		createAndRegisterPower("green", 2);
		createAndRegisterPower("yellow", 3);
	}

	private void createAndRegisterPower(String unlocalizedName, int colorId) {
		ModuleState power = new ModuleStatePower(unlocalizedName, colorId);
		register(power);
	}
}
