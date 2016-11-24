package stevesvehicles.common.blocks.tileentitys.detector.modulestate.registry;

import stevesvehicles.common.blocks.tileentitys.detector.modulestate.ModuleState;
import stevesvehicles.common.blocks.tileentitys.detector.modulestate.ModuleStateActivation;
import stevesvehicles.common.modules.IActivatorModule;
import stevesvehicles.common.modules.cart.tool.ModuleDrill;
import stevesvehicles.common.modules.common.addon.ModuleInvisible;
import stevesvehicles.common.modules.common.addon.ModuleShield;
import stevesvehicles.common.modules.common.addon.chunk.ModuleChunkLoader;
import stevesvehicles.common.modules.common.attachment.ModuleCage;

public class ModuleStateRegistryActivation extends ModuleStateRegistry {
	public ModuleStateRegistryActivation() {
		super("activation");
		createAndRegisterActivation("shield", ModuleShield.class);
		createAndRegisterActivation("chunk", ModuleChunkLoader.class);
		createAndRegisterActivation("invisibility", ModuleInvisible.class);
		createAndRegisterActivation("drill", ModuleDrill.class);
		createAndRegisterActivation("cage", ModuleCage.class);
	}

	private void createAndRegisterActivation(String unlocalizedName, Class<? extends IActivatorModule> moduleClass) {
		ModuleState activation = new ModuleStateActivation(unlocalizedName, moduleClass, 0);
		register(activation);
	}
}
