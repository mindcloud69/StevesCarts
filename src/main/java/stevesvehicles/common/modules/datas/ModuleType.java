package stevesvehicles.common.modules.datas;

import stevesvehicles.client.localization.ILocalizedText;
import stevesvehicles.client.localization.entry.info.LocalizationCategory;
import stevesvehicles.common.modules.ModuleBase;
import stevesvehicles.common.modules.cart.attachment.ModuleAttachment;
import stevesvehicles.common.modules.cart.tool.ModuleTool;
import stevesvehicles.common.modules.common.addon.ModuleAddon;
import stevesvehicles.common.modules.common.engine.ModuleEngine;
import stevesvehicles.common.modules.common.hull.ModuleHull;
import stevesvehicles.common.modules.common.storage.ModuleStorage;

public enum ModuleType{
	HULL(ModuleHull.class, LocalizationCategory.HULL),
	ENGINE(ModuleEngine.class, LocalizationCategory.ENGINE),
	TOOL(ModuleTool.class, LocalizationCategory.TOOL),
	ATTACHMENT(ModuleAttachment.class, LocalizationCategory.ATTACHMENT),
	STORAGE(ModuleStorage.class, LocalizationCategory.STORAGE),
	ADDON(ModuleAddon.class, LocalizationCategory.ADDON),
	INVALID(ModuleBase.class, LocalizationCategory.INVALID);
	private Class<? extends ModuleBase> clazz;
	private ILocalizedText name;

	ModuleType(Class<? extends ModuleBase> clazz, ILocalizedText name) {
		this.clazz = clazz;
		this.name = name;
	}

	public Class<? extends ModuleBase> getClazz() {
		return clazz;
	}

	public String getName() {
		return name.translate();
	}
}
