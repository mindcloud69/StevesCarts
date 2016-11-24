package stevesvehicles.api.module.handlers;

import stevesvehicles.api.module.IModule;

/**
 * The IContentHandlerFactory is used to create {@link IContentHandler}s automatically. 
 */
public interface IContentHandlerFactory {

	<M extends IModule> IContentHandler<M> createHandler(IModule module);
}
