package butterfork.internal;

import com.google.common.base.Joiner;
import com.google.testing.compile.JavaFileObjects;

import org.junit.Test;

import javax.tools.JavaFileObject;

import static com.google.common.truth.Truth.ASSERT;
import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;

public class BindColorTest {
  @Test public void simpleInt() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.app.Activity;",
        "import butterfork.BindColor;",
        "public class Test extends Activity {",
        "  @BindColor(1) int one;",
        "}"
    ));

    JavaFileObject expectedSource = JavaFileObjects.forSourceString("test/Test$$ViewBinder",
        Joiner.on('\n').join(
            "package test;",
            "import android.content.res.Resources;",
            "import butterfork.ButterFork;",
            "import java.lang.Object;",
            "import java.lang.Override;",
            "public class Test$$ViewBinder<T extends Test> implements ButterFork.ViewBinder<T> {",
            "  @Override public void bind(final ButterFork.Finder finder, final T target, Object source) {",
            "    Resources res = finder.getContext(source).getResources();",
            "    target.one = res.getColor(1);",
            "  }",
            "  @Override public void unbind(T target) {",
            "  }",
            "}"
        ));

    ASSERT.about(javaSource()).that(source)
        .processedWith(new ButterForkProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(expectedSource);
  }

  @Test public void simpleColorStateList() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.app.Activity;",
        "import android.content.res.ColorStateList;",
        "import butterfork.BindColor;",
        "public class Test extends Activity {",
        "  @BindColor(1) ColorStateList one;",
        "}"
    ));

    JavaFileObject expectedSource = JavaFileObjects.forSourceString("test/Test$$ViewBinder",
        Joiner.on('\n').join(
            "package test;",
            "import android.content.res.Resources;",
            "import butterfork.ButterFork;",
            "import java.lang.Object;",
            "import java.lang.Override;",
            "public class Test$$ViewBinder<T extends Test> implements ButterFork.ViewBinder<T> {",
            "  @Override public void bind(final ButterFork.Finder finder, final T target, Object source) {",
            "    Resources res = finder.getContext(source).getResources();",
            "    target.one = res.getColorStateList(1);",
            "  }",
            "  @Override public void unbind(T target) {",
            "  }",
            "}"
        ));

    ASSERT.about(javaSource()).that(source)
        .processedWith(new ButterForkProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(expectedSource);
  }

  @Test public void typeMustBeIntOrColorStateList() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.app.Activity;",
        "import butterfork.BindColor;",
        "public class Test extends Activity {",
        "  @BindColor(1) String one;",
        "}"
    ));

    ASSERT.about(javaSource()).that(source)
        .processedWith(new ButterForkProcessor())
        .failsToCompile()
        .withErrorContaining("@BindColor field type must be 'int' or 'ColorStateList'. (test.Test.one)")
        .in(source).onLine(5);
  }
}
