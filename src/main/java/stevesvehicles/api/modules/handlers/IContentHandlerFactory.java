package stevesvehicles.api.modules.handlers;

import akka.actor.ExtendedActorSystem;
import stevesvehicles.api.modules.IModule;

public interface IContentHandlerFactory {

	<M extends IModule, C, H extends ContentHandler<M, C>> H createHandler(M module);
}
