package stevesvehicles.common.registries;

import java.util.Collection;

@Deprecated
public interface IRegistry<E> {
	String getFullCode(E obj);

	Collection<E> getElements();

	String getCode();
}
