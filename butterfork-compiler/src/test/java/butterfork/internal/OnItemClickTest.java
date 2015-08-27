package butterfork.internal;

import com.google.common.base.Joiner;
import com.google.testing.compile.JavaFileObjects;

import org.junit.Test;

import javax.tools.JavaFileObject;

import static com.google.common.truth.Truth.ASSERT;
import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;

/** This augments {@link OnClickTest} with tests that exercise callbacks with parameters. */
public class OnItemClickTest {
  @Test public void onItemClickBinding() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.app.Activity;",
        "import butterfork.OnItemClick;",
        "public class Test extends Activity {",
        "  @OnItemClick(\"one\") void doStuff() {}",
        "}"));

    JavaFileObject expectedSource = JavaFileObjects.forSourceString("test/Test$$ViewBinder",
        Joiner.on('\n').join(
            "package test;",
            "import android.view.View;",
            "import android.widget.AdapterView;",
            "import butterfork.ButterFork;",
            "import butterfork.internal.R;",
            "import java.lang.Object;",
            "import java.lang.Override;",
            "public class Test$$ViewBinder<T extends Test> implements ButterFork.ViewBinder<T> {",
            "  @Override public void bind(final ButterFork.Finder finder, final T target, Object source) {",
            "    View view;",
            "    view = finder.findRequiredView(source, R.id.one, \"method 'doStuff'\");",
            "    ((AdapterView<?>) view).setOnItemClickListener(new AdapterView.OnItemClickListener() {",
            "      @Override public void onItemClick(AdapterView<?> p0, View p1, int p2, long p3) {",
            "        target.doStuff();",
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

  @Test public void onItemClickBindingWithParameters() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.app.Activity;",
        "import android.view.View;",
        "import android.widget.AdapterView;",
        "import butterfork.OnItemClick;",
        "public class Test extends Activity {",
        "  @OnItemClick(\"one\") void doStuff(",
        "    AdapterView<?> parent,",
        "    View view,",
        "    int position,",
        "    long id",
        "  ) {}",
        "}"));

    JavaFileObject expectedSource = JavaFileObjects.forSourceString("test/Test$$ViewBinder",
        Joiner.on('\n').join(
            "package test;",
            "import android.view.View;",
            "import android.widget.AdapterView;",
            "import butterfork.ButterFork;",
            "import butterfork.internal.R;",
            "import java.lang.Object;",
            "import java.lang.Override;",
            "public class Test$$ViewBinder<T extends Test> implements ButterFork.ViewBinder<T> {",
            "  @Override public void bind(final ButterFork.Finder finder, final T target, Object source) {",
            "    View view;",
            "    view = finder.findRequiredView(source, R.id.one, \"method 'doStuff'\");",
            "    ((AdapterView<?>) view).setOnItemClickListener(new AdapterView.OnItemClickListener() {",
            "      @Override public void onItemClick(AdapterView<?> p0, View p1, int p2, long p3) {",
            "        target.doStuff(p0, p1, p2, p3);",
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

  @Test public void onItemClickBindingWithParameterSubset() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.app.Activity;",
        "import android.view.View;",
        "import android.widget.ListView;",
        "import butterfork.OnItemClick;",
        "public class Test extends Activity {",
        "  @OnItemClick(\"one\") void doStuff(",
        "    ListView parent,",
        "    int position",
        "  ) {}",
        "}"));

    JavaFileObject expectedSource = JavaFileObjects.forSourceString("test/Test$$ViewBinder",
        Joiner.on('\n').join(
            "package test;",
            "import android.view.View;",
            "import android.widget.AdapterView;",
            "import android.widget.ListView;",
            "import butterfork.ButterFork;",
            "import butterfork.internal.R;",
            "import java.lang.Object;",
            "import java.lang.Override;",
            "public class Test$$ViewBinder<T extends Test> implements ButterFork.ViewBinder<T> {",
            "  @Override public void bind(final ButterFork.Finder finder, final T target, Object source) {",
            "    View view;",
            "    view = finder.findRequiredView(source, R.id.one, \"method 'doStuff'\");",
            "    ((AdapterView<?>) view).setOnItemClickListener(new AdapterView.OnItemClickListener() {",
            "      @Override public void onItemClick(AdapterView<?> p0, View p1, int p2, long p3) {",
            "        target.doStuff(finder.<ListView>castParam(p0, \"onItemClick\", 0, \"doStuff\", 0), p2);",
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

  @Test public void onItemClickBindingWithParameterSubsetAndGenerics() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.app.Activity;",
        "import android.view.View;",
        "import android.widget.ListView;",
        "import butterfork.OnItemClick;",
        "public class Test<T extends ListView> extends Activity {",
        "  @OnItemClick(\"one\") void doStuff(",
        "    T parent,",
        "    int position",
        "  ) {}",
        "}"));

    JavaFileObject expectedSource = JavaFileObjects.forSourceString("test/Test$$ViewBinder",
        Joiner.on('\n').join(
            "package test;",
            "import android.view.View;",
            "import android.widget.AdapterView;",
            "import android.widget.ListView;",
            "import butterfork.ButterFork;",
            "import butterfork.internal.R;",
            "import java.lang.Object;",
            "import java.lang.Override;",
            "public class Test$$ViewBinder<T extends Test> implements ButterFork.ViewBinder<T> {",
            "  @Override public void bind(final ButterFork.Finder finder, final T target, Object source) {",
            "    View view;",
            "    view = finder.findRequiredView(source, R.id.one, \"method 'doStuff'\");",
            "    ((AdapterView<?>) view).setOnItemClickListener(new AdapterView.OnItemClickListener() {",
            "      @Override public void onItemClick(AdapterView<?> p0, View p1, int p2, long p3) {",
            "        target.doStuff(finder.<ListView>castParam(p0, \"onItemClick\", 0, \"doStuff\", 0), p2);",
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

  @Test public void onClickRootViewBinding() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.content.Context;",
        "import android.widget.ListView;",
        "import butterfork.OnItemClick;",
        "public class Test extends ListView {",
        "  @OnItemClick void doStuff() {}",
        "  public Test(Context context) {",
        "    super(context);",
        "  }",
        "}"));

    JavaFileObject expectedSource = JavaFileObjects.forSourceString("test/Test$$ViewBinder",
        Joiner.on('\n').join(
            "package test;",
            "import android.view.View;",
            "import android.widget.AdapterView;",
            "import butterfork.ButterFork;",
            "import java.lang.Object;",
            "import java.lang.Override;",
            "public class Test$$ViewBinder<T extends Test> implements ButterFork.ViewBinder<T> {",
            "  @Override public void bind(final ButterFork.Finder finder, final T target, Object source) {",
            "    View view;",
            "    view = target;",
            "    ((AdapterView<?>) view).setOnItemClickListener(new AdapterView.OnItemClickListener() {",
            "      @Override public void onItemClick(AdapterView<?> p0, View p1, int p2, long p3) {",
            "        target.doStuff();",
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

  @Test public void failsWithInvalidId() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.content.Context;",
        "import android.app.Activity;",
        "import butterfork.OnItemClick;",
        "public class Test extends Activity {",
        "  @OnItemClick({\"one\", \"\"}) void doStuff() {}",
        "}"));

    ASSERT.about(javaSource()).that(source)
        .withCompilerOptions("-Arespackagename=" + R.class.getPackage().getName())
        .processedWith(new ButterForkProcessor())
        .failsToCompile()
        .withErrorContaining("@OnItemClick annotation contains empty ID. (test.Test.doStuff)")
        .in(source).onLine(6);
  }

  @Test public void failsWithInvalidParameterConfiguration() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.app.Activity;",
        "import android.view.View;",
        "import android.widget.AdapterView;",
        "import butterfork.OnItemClick;",
        "public class Test extends Activity {",
        "  @OnItemClick(\"one\") void doStuff(",
        "    AdapterView<?> parent,",
        "    View view,",
        "    View whatIsThis",
        "  ) {}",
        "}"));

    ASSERT.about(javaSource()).that(source)
        .withCompilerOptions("-Arespackagename=" + R.class.getPackage().getName())
        .processedWith(new ButterForkProcessor())
        .failsToCompile()
        .withErrorContaining(Joiner.on('\n').join(
            "Unable to match @OnItemClick method arguments. (test.Test.doStuff)",
            "  ",
            "    Parameter #1: android.widget.AdapterView<?>",
            "      matched listener parameter #1: android.widget.AdapterView<?>",
            "  ",
            "    Parameter #2: android.view.View",
            "      matched listener parameter #2: android.view.View",
            "  ",
            "    Parameter #3: android.view.View",
            "      did not match any listener parameters",
            "  ",
            "  Methods may have up to 4 parameter(s):",
            "  ",
            "    android.widget.AdapterView<?>",
            "    android.view.View",
            "    int",
            "    long",
            "  ",
            "  These may be listed in any order but will be searched for from top to bottom."))
        .in(source).onLine(7);
  }
}
