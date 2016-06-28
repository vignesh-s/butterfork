package butterfork;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.CLASS;

/**
 * Bind a field to the specified drawable resource ID.
 * <pre><code>
 * {@literal @}BindDrawable(R.drawable.placeholder)
 * Drawable placeholder;
 * {@literal @}BindDrawable(value = R.drawable.placeholder, tint = R.attr.colorAccent)
 * Drawable tintedPlaceholder;
 * </code></pre>
 */
@Retention(CLASS) @Target(FIELD)
public @interface BindDrawable {
  /** Drawable resource ID to which the field will be bound. */
  String value();

  /** Color attribute resource ID that is used to tint the drawable. */
  String tint() default "";
}
