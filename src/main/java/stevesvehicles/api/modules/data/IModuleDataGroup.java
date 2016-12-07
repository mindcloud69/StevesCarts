package stevesvehicles.api.modules.data;

import java.util.List;

public interface IModuleDataGroup {
	String getName();

	List<IModuleData> getModules();

	int getCount();

	IModuleDataGroup add(IModuleData module);

	IModuleDataGroup setCount(int count);

	IModuleDataGroup copy(String key);

	IModuleDataGroup copy(String key, int count);

	IModuleDataGroup copyWithName(String key, int count);

	IModuleDataGroup getUnlinkedCopy(String key, int count);

	String getCountName();

	void add(IModuleDataGroup group);

	void setName(ILocalizedText name);
}
