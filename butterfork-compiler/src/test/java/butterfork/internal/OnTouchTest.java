package butterfork.internal;

import com.google.common.base.Joiner;
import com.google.testing.compile.JavaFileObjects;

import org.junit.Test;

import javax.tools.JavaFileObject;

import static com.google.common.truth.Truth.ASSERT;
import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;

public class OnTouchTest {
  @Test public void touch() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.app.Activity;",
        "import butterfork.BindResources;",
        "import butterfork.OnTouch;",
        "@BindResources(butterfork.internal.R.class)",
        "public class Test extends Activity {",
        "  @OnTouch(\"one\") boolean doStuff() { return false; }",
        "}"
    ));

    JavaFileObject expectedSource = JavaFileObjects.forSourceString("test/Test$$ViewBinder",
        Joiner.on('\n').join(
            "package test;",
            "import android.view.MotionEvent;",
            "import android.view.View;",
            "import butterfork.ButterFork;",
            "import butterfork.internal.R;",
            "import java.lang.Object;",
            "import java.lang.Override;",
            "public class Test$$ViewBinder<T extends Test> implements ButterFork.ViewBinder<T> {",
            "  @Override public void bind(final ButterFork.Finder finder, final T target, Object source) {",
            "    View view;",
            "    view = finder.findRequiredView(source, R.id.one, \"method 'doStuff'\");",
            "    view.setOnTouchListener(new View.OnTouchListener() {",
            "      @Override public boolean onTouch(View p0, MotionEvent p1) {",
            "        return target.doStuff();",
            "      }",
            "    });",
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

  @Test public void failsMultipleListenersWithReturnValue() throws Exception {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.app.Activity;",
        "import butterfork.BindResources;",
        "import butterfork.OnTouch;",
        "@BindResources(butterfork.internal.R.class)",
        "public class Test extends Activity {",
        "  @OnTouch(\"one\") boolean doStuff1() {}",
        "  @OnTouch(\"one\") boolean doStuff2() {}",
        "}"));

    ASSERT.about(javaSource()).that(source)
        .processedWith(new ButterForkProcessor())
        .failsToCompile()
        .withErrorContaining(
            "Multiple listener methods with return value specified for ID one. (test.Test.doStuff2)")
        .in(source).onLine(8);
  }
}
