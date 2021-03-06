package butterfork;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import butterfork.internal.ListenerClass;
import butterfork.internal.ListenerMethod;

import static android.widget.CompoundButton.OnCheckedChangeListener;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.CLASS;

/**
 * Bind a method to an {@link OnCheckedChangeListener OnCheckedChangeListener} on the view for
 * each ID specified.
 * <pre><code>
 * {@literal @}OnCheckedChanged(R.id.example) void onChecked(boolean checked) {
 *   Toast.makeText(this, checked ? "Checked!" : "Unchecked!", Toast.LENGTH_SHORT).show();
 * }
 * </code></pre>
 * Any number of parameters from
 * {@link OnCheckedChangeListener#onCheckedChanged(android.widget.CompoundButton, boolean)
 * onCheckedChanged} may be used on the method.
 *
 * @see OnCheckedChangeListener
 */
@Target(METHOD)
@Retention(CLASS)
@ListenerClass(
    targetType = "android.widget.CompoundButton",
    setter = "setOnCheckedChangeListener",
    type = "android.widget.CompoundButton.OnCheckedChangeListener",
    method = @ListenerMethod(
        name = "onCheckedChanged",
        parameters = {
            "android.widget.CompoundButton",
            "boolean"
        }
    )
)
public @interface OnCheckedChanged {
  /** View IDs to which the method will be bound. */
  String[] value() default { "" };
}
