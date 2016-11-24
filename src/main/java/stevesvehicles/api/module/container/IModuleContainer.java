package stevesvehicles.api.module.container;

import java.util.Collection;
import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;
import stevesvehicles.api.module.data.ModuleData;

/**
 * An IModuleContainer provides informations about modules.
 */
public interface IModuleContainer {

	/**
	 * @return The stack that is used to display this container in a gui or if a module has no parent.
	 */
	ItemStack getParent();

	/**
	 * @return The {@link ModuleData}s that this container contains.
	 */
	Collection<ModuleData> getDatas();

	@Nullable
	boolean matches(ItemStack stack);
}
