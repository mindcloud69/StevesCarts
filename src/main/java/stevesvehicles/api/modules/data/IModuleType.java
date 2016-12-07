package stevesvehicles.api.modules.data;

import stevesvehicles.api.modules.Module;

public interface IModuleType {

	Class<? extends Module> getModuleClass();

	String getName();
}
