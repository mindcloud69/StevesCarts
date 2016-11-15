package vswe.stevescarts.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * Added to class files to register them with SC's, use with ISCPlguin
 */
@Target(ElementType.TYPE)
public @interface SCLoadingPlugin {

	/**
	 * Set this if the Plugin depends on a mod, no need to set this if you are adding support from within your own mod
	 *
	 * @return the mod id of the mdo
	 */
	String dependentMod() default "";
}
