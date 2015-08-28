package butterfork.internal;

import com.google.common.base.Joiner;
import com.google.testing.compile.JavaFileObjects;

import org.junit.Test;

import javax.tools.JavaFileObject;

import static com.google.common.truth.Truth.ASSERT;
import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;

public class OnClickTest {
  @Test public void onClickBinding() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.app.Activity;",
        "import butterfork.OnClick;",
        "public class Test extends Activity {",
        "  @OnClick(\"one\") void doStuff() {}",
        "}"));

    JavaFileObject expectedSource = JavaFileObjects.forSourceString("test/Test$$ViewBinder",
        Joiner.on('\n').join(
            "package test;",
            "import android.view.View;",
            "import butterfork.ButterFork;",
            "import butterfork.internal.DebouncingOnClickListener;",
            "import butterfork.internal.R;",
            "import java.lang.Object;",
            "import java.lang.Override;",
            "public class Test$$ViewBinder<T extends Test> implements ButterFork.ViewBinder<T> {",
            "  @Override public void bind(final ButterFork.Finder finder, final T target, Object source) {",
            "    View view;",
            "    view = finder.findRequiredView(source, R.id.one, \"method 'doStuff'\");",
            "    view.setOnClickListener(new DebouncingOnClickListener() {",
            "      @Override public void doClick(View p0) {",
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

  @Test public void onClickMultipleBindings() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.view.View;",
        "import android.app.Activity;",
        "import butterfork.OnClick;",
        "public class Test extends Activity {",
        "  @OnClick(\"one\") void doStuff1() {}",
        "  @OnClick(\"one\") void doStuff2() {}",
        "  @OnClick({\"one\", \"two\"}) void doStuff3(View v) {}",
        "}"));

    JavaFileObject expectedSource = JavaFileObjects.forSourceString("test/Test$$ViewBinder",
        Joiner.on('\n').join(
            "package test;",
            "import android.view.View;",
            "import butterfork.ButterFork;",
            "import butterfork.internal.DebouncingOnClickListener;",
            "import butterfork.internal.R;",
            "import java.lang.Object;",
            "import java.lang.Override;",
            "public class Test$$ViewBinder<T extends Test> implements ButterFork.ViewBinder<T> {",
            "  @Override public void bind(final ButterFork.Finder finder, final T target, Object source) {",
            "    View view;",
            "    view = finder.findRequiredView(source, R.id.one, \"method 'doStuff1', method 'doStuff2', and method 'doStuff3'\");",
            "    view.setOnClickListener(new DebouncingOnClickListener() {",
            "      @Override public void doClick(View p0) {",
            "        target.doStuff1();",
            "        target.doStuff2();",
            "        target.doStuff3(p0);",
            "      }",
            "    });",
            "    view = finder.findRequiredView(source, R.id.two, \"method 'doStuff3'\");",
            "    view.setOnClickListener(new DebouncingOnClickListener() {",
            "      @Override public void doClick(View p0) {",
            "        target.doStuff3(p0);",
            "      }",
            "    });",
            "  }",
            "  @Override public void unbind(T target) {",
            "}"));

    ASSERT.about(javaSource()).that(source)
        .withCompilerOptions("-Arespackagename=" + R.class.getPackage().getName())
        .processedWith(new ButterForkProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(expectedSource);
  }

  @Test public void findOnlyCalledOnce() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.app.Activity;",
        "import android.view.View;",
        "import butterfork.Bind;",
        "import butterfork.OnClick;",
        "public class Test extends Activity {",
        "  @Bind(\"one\") View view;",
        "  @OnClick(\"one\") void doStuff() {}",
        "}"));

    JavaFileObject expectedSource = JavaFileObjects.forSourceString("test/Test$$ViewBinder",
        Joiner.on('\n').join(
            "package test;",
            "import android.view.View;",
            "import butterfork.ButterFork;",
            "import butterfork.internal.DebouncingOnClickListener;",
            "import butterfork.internal.R;",
            "import java.lang.Object;",
            "import java.lang.Override;",
            "public class Test$$ViewBinder<T extends Test> implements ButterFork.ViewBinder<T> {",
            "  @Override public void bind(final ButterFork.Finder finder, final T target, Object source) {",
            "    View view;",
            "    view = finder.findRequiredView(source, R.id.one, \"field 'view' and method 'doStuff'\");",
            "    target.view = view;",
            "    view.setOnClickListener(new DebouncingOnClickListener() {",
            "      @Override public void doClick(View p0) {",
            "        target.doStuff();",
            "      }",
            "    });",
            "  }",
            "  @Override public void unbind(T target) {",
            "    target.view = null;",
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

  @Test public void methodVisibility() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.app.Activity;",
        "import android.view.View;",
        "import butterfork.OnClick;",
        "public class Test extends Activity {",
        "  @OnClick(\"one\") public void thing1() {}",
        "  @OnClick(\"two\") void thing2() {}",
        "  @OnClick(\"three\") protected void thing3() {}",
        "}"
    ));

    ASSERT.about(javaSource()).that(source)
        .withCompilerOptions("-Arespackagename=" + R.class.getPackage().getName())
        .processedWith(new ButterForkProcessor())
        .compilesWithoutError();
  }

  @Test public void methodCastsArgument() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.app.Activity;",
        "import android.view.View;",
        "import android.widget.Button;",
        "import android.widget.TextView;",
        "import butterfork.OnClick;",
        "public class Test extends Activity {",
        "  interface TestInterface {}",
        "  @OnClick(\"zero\") void click0() {}",
        "  @OnClick(\"one\") void click1(View view) {}",
        "  @OnClick(\"two\") void click2(TextView view) {}",
        "  @OnClick(\"three\") void click3(Button button) {}",
        "  @OnClick(\"four\") void click4(TestInterface thing) {}",
        "}"
    ));

    JavaFileObject expectedSource = JavaFileObjects.forSourceString("test/Test$$ViewBinder",
        Joiner.on('\n').join(
            "package test;",
            "import android.view.View;",
            "import android.widget.Button;",
            "import android.widget.TextView;",
            "import butterfork.ButterFork;",
            "import butterfork.internal.DebouncingOnClickListener;",
            "import butterfork.internal.R;",
            "import java.lang.Object;",
            "import java.lang.Override;",
            "public class Test$$ViewBinder<T extends Test> implements ButterFork.ViewBinder<T> {",
            "  @Override public void bind(final ButterFork.Finder finder, final T target, Object source) {",
            "    View view;",
            "    view = finder.findRequiredView(source, R.id.zero, \"method 'click0'\");",
            "    view.setOnClickListener(new DebouncingOnClickListener() {",
            "      @Override public void doClick(View p0) {",
            "        target.click0();",
            "      }",
            "    });",
            "    view = finder.findRequiredView(source, R.id.one, \"method 'click1'\");",
            "    view.setOnClickListener(new DebouncingOnClickListener() {",
            "      @Override public void doClick(View p0) {",
            "        target.click1(p0);",
            "      }",
            "    });",
            "    view = finder.findRequiredView(source, R.id.two, \"method 'click2'\");",
            "    view.setOnClickListener(new DebouncingOnClickListener() {",
            "      @Override public void doClick(View p0) {",
            "        target.click2(finder.<TextView>castParam(p0, \"doClick\", 0, \"click2\", 0));",
            "      }",
            "    });",
            "    view = finder.findRequiredView(source, R.id.three, \"method 'click3'\");",
            "    view.setOnClickListener(new DebouncingOnClickListener() {",
            "      @Override public void doClick(View p0) {",
            "        target.click3(finder.<Button>castParam(p0, \"doClick\", 0, \"click3\", 0);",
            "      }",
            "    });",
            "    view = finder.findRequiredView(source, R.id.four, \"method 'click4'\");",
            "    view.setOnClickListener(new DebouncingOnClickListener() {",
            "      @Override public void doClick(View p0) {",
            "        target.click4(finder.<Test.TestInterface>castParam(p0, \"doClick\", 0, \"click4\", 0);",
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

  @Test public void methodWithMultipleIds() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.app.Activity;",
        "import android.view.View;",
        "import butterfork.OnClick;",
        "public class Test extends Activity {",
        "  @OnClick({\"one\", \"two\", \"three\"}) void click() {}",
        "}"
    ));

    JavaFileObject expectedSource = JavaFileObjects.forSourceString("test/Test$$ViewBinder",
        Joiner.on('\n').join(
            "package test;",
            "import android.view.View;",
            "import butterfork.ButterFork;",
            "import butterfork.internal.DebouncingOnClickListener;",
            "import butterfork.internal.R;",
            "import java.lang.Object;",
            "import java.lang.Override;",
            "public class Test$$ViewBinder<T extends Test> implements ButterFork.ViewBinder<T> {",
            "  @Override public void bind(final ButterFork.Finder finder, final T target, Object source) {",
            "    View view;",
            "    view = finder.findRequiredView(source, R.id.one, \"method 'click'\");",
            "    view.setOnClickListener(new DebouncingOnClickListener() {",
            "      @Override public void doClick(View p0) {",
            "        target.click();",
            "      }",
            "    });",
            "    view = finder.findRequiredView(source, R.id.two, \"method 'click'\");",
            "    view.setOnClickListener(new DebouncingOnClickListener() {",
            "      @Override public void doClick(View p0) {",
            "        target.click();",
            "      }",
            "    });",
            "    view = finder.findRequiredView(source, R.id.three, \"method 'click'\");",
            "    view.setOnClickListener(new DebouncingOnClickListener() {",
            "      @Override public void doClick(View p0) {",
            "        target.click();",
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

  @Test public void optional() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.app.Activity;",
        "import butterfork.OnClick;",
        "public class Test extends Activity {",
        "  @interface Nullable {}",
        "  @Nullable @OnClick(\"one\") void doStuff() {}",
        "}"));

    JavaFileObject expectedSource = JavaFileObjects.forSourceString("test/Test$$ViewBinder",
        Joiner.on('\n').join(
            "package test;",
            "import android.view.View;",
            "import butterfork.ButterFork;",
            "import butterfork.internal.DebouncingOnClickListener;",
            "import butterfork.internal.R;",
            "import java.lang.Object;",
            "import java.lang.Override;",
            "public class Test$$ViewBinder<T extends Test> implements ButterFork.ViewBinder<T> {",
            "  @Override public void bind(final ButterFork.Finder finder, final T target, Object source) {",
            "    View view;",
            "    view = finder.findOptionalView(source, R.id.one, null);",
            "    if (view != null) {",
            "      view.setOnClickListener(new DebouncingOnClickListener() {",
            "        @Override public void doClick(View p0) {",
            "          target.doStuff();",
            "        }",
            "      });",
            "    }",
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

  @Test public void optionalAndRequiredSkipsNullCheck() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.app.Activity;",
        "import android.view.View;",
        "import butterfork.Bind;",
        "import butterfork.OnClick;",
        "public class Test extends Activity {",
        "  @interface Nullable {}",
        "  @Bind(\"one\") View view;",
        "  @Nullable @OnClick(\"one\") void doStuff() {}",
        "}"));

    JavaFileObject expectedSource = JavaFileObjects.forSourceString("test/Test$$ViewBinder",
        Joiner.on('\n').join(
            "package test;",
            "import android.view.View;",
            "import butterfork.ButterFork;",
            "import butterfork.internal.DebouncingOnClickListener;",
            "import butterfork.internal.R;",
            "import java.lang.Object;",
            "import java.lang.Override;",
            "public class Test$$ViewBinder<T extends Test> implements ButterFork.ViewBinder<T> {",
            "  @Override public void bind(final ButterFork.Finder finder, final T target, Object source) {",
            "    View view;",
            "    view = finder.findRequiredView(source, R.id.one, \"field 'view'\");",
            "    target.view = view;",
            "    view.setOnClickListener(new DebouncingOnClickListener() {",
            "      @Override public void doClick(View p0) {",
            "        target.doStuff();",
            "      }",
            "    });",
            "  }",
            "  @Override public void unbind(T target) {",
            "    target.view = null;",
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

  @Test public void failsInJavaPackage() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package java.test;",
        "import butterfork.OnClick;",
        "public class Test {",
        "  @OnClick(\"one\") void doStuff() {}",
        "}"
    ));

    ASSERT.about(javaSource()).that(source)
        .withCompilerOptions("-Arespackagename=" + R.class.getPackage().getName())
        .processedWith(new ButterForkProcessor())
        .failsToCompile()
        .withErrorContaining(
            "@OnClick-annotated class incorrectly in Java framework package. (java.test.Test)")
        .in(source).onLine(4);
  }

  @Test public void failsInAndroidPackage() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package android.test;",
        "import butterfork.OnClick;",
        "public class Test {",
        "  @OnClick(\"one\") void doStuff() {}",
        "}"
    ));

    ASSERT.about(javaSource()).that(source)
        .withCompilerOptions("-Arespackagename=" + R.class.getPackage().getName())
        .processedWith(new ButterForkProcessor())
        .failsToCompile()
        .withErrorContaining(
            "@OnClick-annotated class incorrectly in Android framework package. (android.test.Test)")
        .in(source).onLine(4);
  }

  @Test public void failsIfHasReturnType() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.app.Activity;",
        "import butterfork.OnClick;",
        "public class Test extends Activity {",
        "  @OnClick(\"one\")",
        "  public String doStuff() {",
        "  }",
        "}"));

    ASSERT.about(javaSource()).that(source)
        .withCompilerOptions("-Arespackagename=" + R.class.getPackage().getName())
        .processedWith(new ButterForkProcessor())
        .failsToCompile()
        .withErrorContaining("@OnClick methods must have a 'void' return type. (test.Test.doStuff)")
        .in(source).onLine(6);
  }

  @Test public void failsIfPrivateMethod() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.app.Activity;",
        "import butterfork.OnClick;",
        "public class Test extends Activity {",
        "  @OnClick(\"one\")",
        "  private void doStuff() {",
        "  }",
        "}"));

    ASSERT.about(javaSource()).that(source)
        .withCompilerOptions("-Arespackagename=" + R.class.getPackage().getName())
        .processedWith(new ButterForkProcessor())
        .failsToCompile()
        .withErrorContaining("@OnClick methods must not be private or static. (test.Test.doStuff)")
        .in(source).onLine(6);
  }

  @Test public void failsIfStatic() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.app.Activity;",
        "import butterfork.OnClick;",
        "public class Test extends Activity {",
        "  @OnClick(\"one\")",
        "  public static void doStuff() {",
        "  }",
        "}"));

    ASSERT.about(javaSource()).that(source)
        .withCompilerOptions("-Arespackagename=" + R.class.getPackage().getName())
        .processedWith(new ButterForkProcessor())
        .failsToCompile()
        .withErrorContaining("@OnClick methods must not be private or static. (test.Test.doStuff)")
        .in(source).onLine(6);
  }

  @Test public void failsIfParameterNotView() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.app.Activity;",
        "import butterfork.OnClick;",
        "public class Test extends Activity {",
        "  @OnClick(\"one\")",
        "  public void doStuff(String thing) {",
        "  }",
        "}"));

    ASSERT.about(javaSource()).that(source)
        .withCompilerOptions("-Arespackagename=" + R.class.getPackage().getName())
        .processedWith(new ButterForkProcessor())
        .failsToCompile()
        .withErrorContaining(Joiner.on('\n').join(
            "Unable to match @OnClick method arguments. (test.Test.doStuff)",
            "  ",
            "    Parameter #1: java.lang.String",
            "      did not match any listener parameters",
            "  ",
            "  Methods may have up to 1 parameter(s):",
            "  ",
            "    android.view.View",
            "  ",
            "  These may be listed in any order but will be searched for from top to bottom."))
        .in(source).onLine(6);
  }

  @Test public void failsIfMoreThanOneParameter() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.app.Activity;",
        "import android.view.View;",
        "import butterfork.OnClick;",
        "public class Test extends Activity {",
        "  @OnClick(\"one\")",
        "  public void doStuff(View thing, View otherThing) {",
        "  }",
        "}"));

    ASSERT.about(javaSource()).that(source)
        .withCompilerOptions("-Arespackagename=" + R.class.getPackage().getName())
        .processedWith(new ButterForkProcessor())
        .failsToCompile()
        .withErrorContaining(
            "@OnClick methods can have at most 1 parameter(s). (test.Test.doStuff)")
        .in(source).onLine(7);
  }

  @Test public void failsIfInInterface() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import butterfork.OnClick;",
        "public interface Test {",
        "  @OnClick(\"one\")",
        "  void doStuff();",
        "}"));

    ASSERT.about(javaSource()).that(source)
        .withCompilerOptions("-Arespackagename=" + R.class.getPackage().getName())
        .processedWith(new ButterForkProcessor())
        .failsToCompile()
        .withErrorContaining(
            "@OnClick methods may only be contained in classes. (test.Test.doStuff)")
        .in(source).onLine(3);
  }

  @Test public void failsIfHasDuplicateIds() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.app.Activity;",
        "import butterfork.OnClick;",
        "public class Test extends Activity {",
        "  @OnClick({\"one\", \"two\", \"three\", \"one\"})",
        "  void doStuff() {",
        "  }",
        "}"));

    ASSERT.about(javaSource()).that(source)
        .withCompilerOptions("-Arespackagename=" + R.class.getPackage().getName())
        .processedWith(new ButterForkProcessor())
        .failsToCompile()
        .withErrorContaining(
            "@OnClick annotation for method contains duplicate ID one. (test.Test.doStuff)")
        .in(source).onLine(6);
  }
}
