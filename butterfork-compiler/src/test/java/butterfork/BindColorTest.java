package butterfork;

import com.google.testing.compile.JavaFileObjects;

import org.junit.Test;

import javax.tools.JavaFileObject;

import butterfork.compiler.ButterKnifeProcessor;

import static com.google.common.truth.Truth.assertAbout;
import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;

public class BindColorTest {
  @Test public void simpleInt() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", ""
        + "package test;\n"
        + "import android.app.Activity;\n"
        + "import butterfork.BindColor;\n"
        + "public class Test extends Activity {\n"
        + "  @BindColor(B.color.one) int one;\n"
        + "}"
    );

    JavaFileObject expectedSource = JavaFileObjects.forSourceString("test/Test$$ViewBinder", ""
        + "package test;\n"
        + "import android.content.Context;\n"
        + "import android.content.res.Resources;\n"
        + "import butterfork.Unbinder;\n"
        + "import butterfork.internal.Finder;\n"
        + "import butterfork.internal.Utils;\n"
        + "import butterfork.internal.ViewBinder;\n"
        + "import java.lang.Object;\n"
        + "import java.lang.Override;\n"
        + "import java.lang.SuppressWarnings;\n"
        + "public class Test$$ViewBinder<T extends Test> implements ViewBinder<T> {\n"
        + "  @Override\n"
        + "  public Unbinder bind(Finder finder, T target, Object source) {\n"
        + "    Context context = finder.getContext(source);\n"
        + "    Resources res = context.getResources();\n"
        + "    Resources.Theme theme = context.getTheme();\n"
        + "    bindToTarget(target, res, theme);\n"
        + "    return Unbinder.EMPTY;\n"
        + "  }\n"
        + "  @SuppressWarnings(\"ResourceType\")\n"
        + "  protected static void bindToTarget(Test target, Resources res, Resources.Theme theme) {\n"
        + "    target.one = Utils.getColor(res, theme, test.R.color.one);\n"
        + "  }\n"
        + "}"
    );

    assertAbout(javaSource()).that(source)
        .processedWith(new ButterKnifeProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(expectedSource);
  }

  @Test public void simpleColorStateList() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", ""
        + "package test;\n"
        + "import android.app.Activity;\n"
        + "import android.content.res.ColorStateList;\n"
        + "import butterfork.BindColor;\n"
        + "public class Test extends Activity {\n"
        + "  @BindColor(B.color.one) ColorStateList one;\n"
        +"}"
    );

    JavaFileObject expectedSource = JavaFileObjects.forSourceString("test/Test$$ViewBinder", ""
        + "package test;\n"
        + "import android.content.Context;\n"
        + "import android.content.res.Resources;\n"
        + "import butterfork.Unbinder;\n"
        + "import butterfork.internal.Finder;\n"
        + "import butterfork.internal.Utils;\n"
        + "import butterfork.internal.ViewBinder;\n"
        + "import java.lang.Object;\n"
        + "import java.lang.Override;\n"
        + "import java.lang.SuppressWarnings;\n"
        + "public class Test$$ViewBinder<T extends Test> implements ViewBinder<T> {\n"
        + "  @Override\n"
        + "  public Unbinder bind(Finder finder, T target, Object source) {\n"
        + "    Context context = finder.getContext(source);\n"
        + "    Resources res = context.getResources();\n"
        + "    Resources.Theme theme = context.getTheme();\n"
        + "    bindToTarget(target, res, theme);\n"
        + "    return Unbinder.EMPTY;\n"
        + "  }\n"
        + "  @SuppressWarnings(\"ResourceType\")\n"
        + "  protected static void bindToTarget(Test target, Resources res, Resources.Theme theme) {\n"
        + "    target.one = Utils.getColorStateList(res, theme, test.R.color.one);\n"
        + "  }\n"
        + "}"
    );

    assertAbout(javaSource()).that(source)
        .processedWith(new ButterKnifeProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(expectedSource);
  }

  @Test public void typeMustBeIntOrColorStateList() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", ""
        + "package test;\n"
        + "import android.app.Activity;\n"
        + "import butterfork.BindColor;\n"
        + "public class Test extends Activity {\n"
        + "  @BindColor(B.color.one) String one;\n"
        + "}"
    );

    assertAbout(javaSource()).that(source)
        .processedWith(new ButterKnifeProcessor())
        .failsToCompile()
        .withErrorContaining("@BindColor field type must be 'int' or 'ColorStateList'. (test.Test.one)")
        .in(source).onLine(5);
  }
}
