package butterfork.internal;

import com.google.common.base.Joiner;
import com.google.testing.compile.JavaFileObjects;

import org.junit.Test;

import javax.tools.JavaFileObject;

import static com.google.common.truth.Truth.ASSERT;
import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;

/** This augments {@link OnClickTest} with tests that exercise callbacks with return types. */
public class OnLongClickTest {
  @Test public void onLongClickBinding() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.app.Activity;",
        "import butterfork.OnLongClick;",
        "public class Test extends Activity {",
        "  @OnLongClick(\"one\") boolean doStuff() {",
        "    return true;",
        "  }",
        "}"));

    JavaFileObject expectedSource = JavaFileObjects.forSourceString("test/Test$$ViewBinder",
        Joiner.on('\n').join(
            "package test;",
            "import android.view.View;",
            "import butterfork.ButterFork;",
            "import butterfork.internal.R;",
            "import java.lang.Object;",
            "import java.lang.Override;",
            "public class Test$$ViewBinder<T extends Test> implements ButterFork.ViewBinder<T> {",
            "  @Override public void bind(final ButterFork.Finder finder, final T target, Object source) {",
            "    View view;",
            "    view = finder.findRequiredView(source, R.id.one, \"method 'doStuff'\");",
            "    view.setOnLongClickListener(new View.OnLongClickListener() {",
            "      @Override public boolean onLongClick(View p0) {",
            "        return target.doStuff();",
            "      }",
            "    });",
            "  }",
            "  @Override public void unbind(T target) {",
            "  }",
            "}"
        ));

    ASSERT.about(javaSource()).that(source)
        .withCompilerOptions("-Arespackagename=" + R.class.getPackage().getName())
        .processedWith(new ButterForkProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(expectedSource);
  }

  @Test public void failsIfMissingReturnType() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.app.Activity;",
        "import butterfork.OnLongClick;",
        "public class Test extends Activity {",
        "  @OnLongClick(\"one\")",
        "  public void doStuff() {",
        "  }",
        "}"));

    ASSERT.about(javaSource()).that(source)
        .withCompilerOptions("-Arespackagename=" + R.class.getPackage().getName())
        .processedWith(new ButterForkProcessor())
        .failsToCompile()
        .withErrorContaining(
            "@OnLongClick methods must have a 'boolean' return type. (test.Test.doStuff)")
        .in(source).onLine(6);
  }
}
