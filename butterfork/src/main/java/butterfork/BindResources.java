package butterfork;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.CLASS;

/**
 * Bind an R class where Ids will be searched in.
 */
@Retention(CLASS) @Target(TYPE) @Inherited
public @interface BindResources {
  /** R class. */
  Class value();
}
