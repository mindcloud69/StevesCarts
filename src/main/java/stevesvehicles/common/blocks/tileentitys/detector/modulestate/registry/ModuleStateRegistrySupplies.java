package stevesvehicles.common.blocks.tileentitys.detector.modulestate.registry;

import stevesvehicles.common.blocks.tileentitys.detector.modulestate.ModuleState;
import stevesvehicles.common.blocks.tileentitys.detector.modulestate.ModuleStateSupplies;
import stevesvehicles.common.modules.ISuppliesModule;
import stevesvehicles.common.modules.cart.attachment.ModuleBridge;
import stevesvehicles.common.modules.cart.attachment.ModuleFertilizer;
import stevesvehicles.common.modules.cart.attachment.ModuleRailer;
import stevesvehicles.common.modules.cart.attachment.ModuleTorch;
import stevesvehicles.common.modules.cart.tool.ModuleFarmer;
import stevesvehicles.common.modules.cart.tool.ModuleWoodcutter;
import stevesvehicles.common.modules.common.attachment.ModuleCakeServer;
import stevesvehicles.common.modules.common.attachment.ModuleShooter;

public class ModuleStateRegistrySupplies extends ModuleStateRegistry {
	public ModuleStateRegistrySupplies() {
		super("supplies");
		createAndRegisterSupplies("rail", ModuleRailer.class);
		createAndRegisterSupplies("torch", ModuleTorch.class);
		createAndRegisterSupplies("sapling", ModuleWoodcutter.class);
		createAndRegisterSupplies("seed", ModuleFarmer.class);
		createAndRegisterSupplies("bridge", ModuleBridge.class);
		createAndRegisterSupplies("arrow", ModuleShooter.class);
		createAndRegisterSupplies("fertilizer", ModuleFertilizer.class);
		createAndRegisterSupplies("cake", ModuleCakeServer.class);
	}

	private void createAndRegisterSupplies(String unlocalizedName, Class<? extends ISuppliesModule> moduleClass) {
		ModuleState supplies = new ModuleStateSupplies(unlocalizedName, moduleClass);
		register(supplies);
	}
}
