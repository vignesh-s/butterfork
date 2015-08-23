package butterfork.internal;

import com.google.common.base.Joiner;
import com.google.testing.compile.JavaFileObjects;

import org.junit.Test;

import javax.tools.JavaFileObject;

import static com.google.common.truth.Truth.ASSERT;
import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;

public class BindArrayTest {
  @Test public void stringArray() throws Exception {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.app.Activity;",
        "import butterfork.BindArray;",
        "import butterfork.BindResources;",
        "@BindResources(butterfork.internal.R.class)",
        "public class Test extends Activity {",
        "  @BindArray(\"one\") String[] one;",
        "}"
    ));

    JavaFileObject expectedSource = JavaFileObjects.forSourceString("test/Test$$ViewBinder",
        Joiner.on('\n').join(
            "package test;",
            "import android.content.res.Resources;",
            "import butterfork.ButterFork;",
            "import butterfork.internal.R;",
            "import java.lang.Object;",
            "import java.lang.Override;",
            "public class Test$$ViewBinder<T extends Test> implements ButterFork.ViewBinder<T> {",
            "  @Override public void bind(final ButterFork.Finder finder, final T target, Object source) {",
            "    Resources res = finder.getContext(source).getResources();",
            "    target.one = res.getStringArray(R.array.one);",
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

  @Test public void intArray() throws Exception {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.app.Activity;",
        "import butterfork.BindArray;",
        "import butterfork.BindResources;",
        "@BindResources(butterfork.internal.R.class)",
        "public class Test extends Activity {",
        "  @BindArray(\"one\") int[] one;",
        "}"
    ));

    JavaFileObject expectedSource = JavaFileObjects.forSourceString("test/Test$$ViewBinder",
        Joiner.on('\n').join(
            "package test;",
            "import android.content.res.Resources;",
            "import butterfork.ButterFork;",
            "import butterfork.internal.R;",
            "import java.lang.Object;",
            "import java.lang.Override;",
            "public class Test$$ViewBinder<T extends Test> implements ButterFork.ViewBinder<T> {",
            "  @Override public void bind(final ButterFork.Finder finder, final T target, Object source) {",
            "    Resources res = finder.getContext(source).getResources();",
            "    target.one = res.getIntArray(R.array.one);",
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

  @Test public void textArray() throws Exception {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.app.Activity;",
        "import butterfork.BindArray;",
        "import butterfork.BindResources;",
        "@BindResources(butterfork.internal.R.class)",
        "public class Test extends Activity {",
        "  @BindArray(\"one\") CharSequence[] one;",
        "}"
    ));

    JavaFileObject expectedSource = JavaFileObjects.forSourceString("test/Test$$ViewBinder",
        Joiner.on('\n').join(
            "package test;",
            "import android.content.res.Resources;",
            "import butterfork.ButterFork;",
            "import butterfork.internal.R;",
            "import java.lang.Object;",
            "import java.lang.Override;",
            "public class Test$$ViewBinder<T extends Test> implements ButterFork.ViewBinder<T> {",
            "  @Override public void bind(final ButterFork.Finder finder, final T target, Object source) {",
            "    Resources res = finder.getContext(source).getResources();",
            "    target.one = res.getTextArray(R.array.one);",
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

  @Test public void typedArray() throws Exception {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.app.Activity;",
        "import butterfork.BindArray;",
        "import butterfork.BindResources;",
        "import android.content.res.TypedArray;",
        "@BindResources(butterfork.internal.R.class)",
        "public class Test extends Activity {",
        "  @BindArray(\"one\") TypedArray one;",
        "}"
    ));

    JavaFileObject expectedSource = JavaFileObjects.forSourceString("test/Test$$ViewBinder",
        Joiner.on('\n').join(
            "package test;",
            "import android.content.res.Resources;",
            "import butterfork.ButterFork;",
            "import butterfork.internal.R;",
            "import java.lang.Object;",
            "import java.lang.Override;",
            "public class Test$$ViewBinder<T extends Test> implements ButterFork.ViewBinder<T> {",
            "  @Override public void bind(final ButterFork.Finder finder, final T target, Object source) {",
            "    Resources res = finder.getContext(source).getResources();",
            "    target.one = res.obtainTypedArray(R.array.one);",
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

  @Test public void typeMustBeSupported() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.app.Activity;",
        "import butterfork.BindArray;",
        "import butterfork.BindResources;",
        "@BindResources(butterfork.internal.R.class)",
        "public class Test extends Activity {",
        "  @BindArray(\"one\") String one;",
        "}"
    ));

    ASSERT.about(javaSource()).that(source)
        .processedWith(new ButterForkProcessor())
        .failsToCompile()
        .withErrorContaining(
            "@BindArray field type must be one of: String[], int[], CharSequence[], "
                + "android.content.res.TypedArray. (test.Test.one)")
        .in(source).onLine(7);
  }
}
