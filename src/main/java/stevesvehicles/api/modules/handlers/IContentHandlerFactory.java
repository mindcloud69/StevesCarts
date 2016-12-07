package stevesvehicles.api.modules.handlers;

import stevesvehicles.api.modules.Module;
import stevesvehicles.common.modules.ModuleBase;

public interface IContentHandlerFactory {
	<M extends Module, C, H extends ContentHandler<M, C>> H createHandler(M module);
}
