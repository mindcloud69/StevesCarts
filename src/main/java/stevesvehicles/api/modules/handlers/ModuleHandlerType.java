package stevesvehicles.api.modules.handlers;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * A ModuleHandlerType is used to differentiate between different {@link IModuleHandler}s. This is necessary for models and other things.
 */
public class ModuleHandlerType {

	public static final ModuleHandlerType TILE_ENTITY = new ModuleHandlerType("tile_entity");
	public static final ModuleHandlerType ENTITY = new ModuleHandlerType("entity");

	@Nullable
	protected final ModuleHandlerType parentType;
	protected final Class<? extends IModuleHandler> handlerClass;
	protected final String name;

	public ModuleHandlerType(@Nonnull ModuleHandlerType parentType, @Nonnull String name, @Nonnull Class<? extends IModuleHandler> handlerClass) {
		this.parentType = parentType;
		this.name = name;
		this.handlerClass = handlerClass;
	}

	private ModuleHandlerType(String name) {
		this.parentType = null;
		this.name = name;
		this.handlerClass = IModuleHandler.class;
	}

	@Nullable
	public ModuleHandlerType getParentType(){
		return parentType;
	}

	public ModuleHandlerType getSuperParentType(){
		if(parentType == null){
			return this;
		}
		return parentType.getSuperParentType();
	}

	public Class<? extends IModuleHandler> getHandlerClass() {
		return handlerClass;
	}
}
