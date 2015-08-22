package butterfork.internal;

import com.google.common.base.Joiner;
import com.google.testing.compile.JavaFileObjects;

import org.junit.Test;

import javax.tools.JavaFileObject;

import static com.google.common.truth.Truth.ASSERT;
import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;

public class BindBitmapTest {
  @Test public void simple() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.app.Activity;",
        "import android.graphics.Bitmap;",
        "import butterfork.BindBitmap;",
        "public class Test extends Activity {",
        "  @BindBitmap(1) Bitmap one;",
        "}"
    ));

    JavaFileObject expectedSource = JavaFileObjects.forSourceString("test/Test$$ViewBinder",
        Joiner.on('\n').join(
            "package test;",
            "import android.content.res.Resources;",
            "import android.graphics.BitmapFactory;",
            "import butterfork.ButterFork;",
            "import java.lang.Object;",
            "import java.lang.Override;",
            "public class Test$$ViewBinder<T extends Test> implements ButterFork.ViewBinder<T> {",
            "  @Override public void bind(final ButterFork.Finder finder, final T target, Object source) {",
            "    Resources res = finder.getContext(source).getResources();",
            "    target.one = BitmapFactory.decodeResource(res, 1);",
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

  @Test public void typeMustBeBitmap() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.app.Activity;",
        "import butterfork.BindBitmap;",
        "public class Test extends Activity {",
        "  @BindBitmap(1) String one;",
        "}"
    ));

    ASSERT.about(javaSource()).that(source)
        .processedWith(new ButterForkProcessor())
        .failsToCompile()
        .withErrorContaining("@BindBitmap field type must be 'Bitmap'. (test.Test.one)")
        .in(source).onLine(5);
  }
}
