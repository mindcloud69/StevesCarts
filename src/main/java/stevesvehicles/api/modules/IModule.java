package stevesvehicles.api.modules;

import java.util.List;

import javax.annotation.Nullable;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import stevesvehicles.api.modules.handlers.ContentHandler;
import stevesvehicles.api.modules.handlers.IModuleHandler;

public interface IModule {

	/**
	 * @return The ItemStack from that the module was created.
	 */
	ItemStack getItemParent();

	/**
	 * Initialize a module.
	 */
	void init();

	/**
	 * Returns true if the  module was already initialized.
	 */
	boolean wasInitialized();

	/**
	 * Add a IContentHandler to this module. This will only work before the module is initialized.
	 */
	void addContendHandler(ContentHandler handler);

	@Nullable
	<H> List<H> getHandlers(Class<? extends H> handlerClass);

	/**
	 * @return A list with all {@link ContentHandler}s that this module have.
	 */
	List<ContentHandler> getHandlers();

	/**
	 * @return The hander that handle this module.
	 */
	IModuleHandler getParent();

	/**
	 * @return The position of this module in the handler.
	 */
	int getPosition();

	NBTTagCompound writeToNBT(NBTTagCompound tagCompound);

	void readFromNBT(NBTTagCompound tagCompound);
}
