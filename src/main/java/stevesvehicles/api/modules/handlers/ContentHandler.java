package stevesvehicles.api.modules.handlers;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import stevesvehicles.api.modules.IModule;

/**
 * An ContentHandler handle a specific type of contend in a module. You can use the IContentHandlerFactory to create {@link ContentHandler}s
 */
public class ContentHandler<M extends IModule, C> {

	protected M parent;
	protected Class<? extends C> contentClass;
	
	public ContentHandler(M parent, Class<? extends C> contentClass) {
		this.parent = parent;
		this.contentClass = contentClass;
	}
	
	/**
	 * @return The module from that the handler is from.
	 */
	public M getParent(){
		return parent;
	}

	/**
	 * Used to add tooltips to items that contains the capability of the IModuleHandler.
	 */
	public void addTooltip(List<String> tooltip, ItemStack stack){
	}

	/**
	 * Push the content of this handler into neighbor blocks or other modules.
	 * 
	 * @return SUCCESS if the handler was cleaned, PASS by default if the handler cannot be cleaned and FAIL if anything fails at the clean progress.
	 */
	public EnumActionResult cleanHandler(){
		return EnumActionResult.PASS;
	}

	/**
	 * @return The class of the content handled by this IContentHandler.
	 */
	@Nullable
	public Class<? extends C> getContentClass(){
		return contentClass;
	}
}
