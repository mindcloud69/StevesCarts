package stevesvehicles.api.modules.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.IForgeRegistryEntry.Impl;
import stevesvehicles.api.modules.IModule;
import stevesvehicles.api.modules.container.IModuleContainer;
import stevesvehicles.api.modules.handlers.ContentHandler;
import stevesvehicles.api.modules.handlers.IContentHandlerFactory;
import stevesvehicles.api.modules.handlers.IModuleHandler;

public class ModuleData extends Impl<ModuleData> {
	public IModule createModule(IModuleContainer container, IModuleHandler handler, ItemStack stack) {
		return null;
	}

	protected void initOptionalHandlers(IModule module) {
		for (IContentHandlerFactory factory : factorys) {
			ContentHandler handler = factory.createHandler(module);
		}
	}

	private final List<IContentHandlerFactory> factorys = new ArrayList<>();

	public void addOptionalHandlers(IContentHandlerFactory... factorys) {
		if (factorys != null) {
			Collections.addAll(this.factorys, factorys);
		}
	}

	/**
	 * @return The name of the module in guis or tooltips.
	 */
	public String getDisplayName() {
		return null;
	}
}
