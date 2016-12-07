package stevesvehicles.api.modules.container;

import java.util.Collection;
import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;
import stevesvehicles.common.modules.ModuleData;

/**
 * An IModuleContainer provides informations about modules.
 */
public interface IModuleContainer {
	/**
	 * @return The stack that is used to display this container in a guit.
	 */
	ItemStack getParent();

	/**
	 * @return The {@link ModuleData}s that this container contains.
	 */
	Collection<ModuleData> getDatas();

	@Nullable
	boolean matches(ItemStack stack);
}
