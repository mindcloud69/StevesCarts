package stevesvehicles.api.module.handlers;

import java.util.List;

import net.minecraft.item.ItemStack;
import stevesvehicles.api.module.IModule;

/**
 * An IContentHandler handle a specific type of contend in a module. You can use the IContentHandlerFactory to create {@link IContentHandler}s
 */
public interface IContentHandler<M extends IModule> {

	/**
	 * @return The module from that the handler is from.
	 */
	M getParent();

	/**
	 * Used to add tooltips to items that contains the capability of the IModuleHandler.
	 */
	void addTooltip(List<String> tooltip, ItemStack stack);

	/**
	 * Push the content of this handler into neighbor blocks or other modules.
	 * 
	 * @return True if the handler was cleaned. Returns false by default if the handler cannot be cleaned.
	 */
	boolean cleanHandler();

	/**
	 * @return The class of the content handled by this IContentHandler.
	 */
	Class getContentClass();

	/**
	 * @return True when the handler has no content.
	 */
	boolean isEmpty();
}
