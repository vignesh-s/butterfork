package butterknife;

import com.google.common.base.Joiner;
import com.google.testing.compile.JavaFileObjects;

import org.junit.Test;

import javax.tools.JavaFileObject;

import butterknife.compiler.ButterKnifeProcessor;

import static com.google.common.truth.Truth.assertAbout;
import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;

public class OnClickTest {
  @Test public void onClickBinding() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", ""
        + "package test;\n"
        + "import android.app.Activity;\n"
        + "import butterknife.OnClick;\n"
        + "public class Test extends Activity {\n"
        + "  @OnClick(B.id.one) void doStuff() {}\n"
        + "}"
    );

    JavaFileObject expectedSource = JavaFileObjects.forSourceString("test/Test$$ViewBinder", ""
        + "package test;\n"
        + "import android.view.View;\n"
        + "import butterknife.Unbinder;\n"
        + "import butterknife.internal.DebouncingOnClickListener;\n"
        + "import butterknife.internal.Finder;\n"
        + "import butterknife.internal.ViewBinder;\n"
        + "import java.lang.IllegalStateException;\n"
        + "import java.lang.Object;\n"
        + "import java.lang.Override;\n"
        + "public class Test$$ViewBinder<T extends Test> implements ViewBinder<T> {\n"
        + "  @Override\n"
        + "  public Unbinder bind(Finder finder, T target, Object source) {\n"
        + "    return new InnerUnbinder(target, finder, source);\n"
        + "  }\n"
        + "  protected static class InnerUnbinder<T extends Test> implements Unbinder {\n"
        + "    protected T target;\n"
        + "    private View testRidone;\n"
        + "    protected InnerUnbinder(final T target, Finder finder, Object source) {\n"
        + "      this.target = target;\n"
        + "      View view;\n"
        + "      view = finder.findRequiredView(source, test.R.id.one, \"method 'doStuff'\");\n"
        + "      testRidone = view;\n"
        + "      view.setOnClickListener(new DebouncingOnClickListener() {\n"
        + "        @Override\n"
        + "        public void doClick(View p0) {\n"
        + "          target.doStuff();\n"
        + "        }\n"
        + "      });\n"
        + "    }\n"
        + "    @Override\n"
        + "    public void unbind() {\n"
        + "      if (this.target == null) throw new IllegalStateException(\"Bindings already cleared.\");\n"
        + "      testRidone.setOnClickListener(null);\n"
        + "      testRidone = null;\n"
        + "      this.target = null;\n"
        + "    }\n"
        + "  }\n"
        + "}"
    );

    assertAbout(javaSource()).that(source)
        .processedWith(new ButterKnifeProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(expectedSource);
  }

  @Test public void onClickBindingFinalType() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", ""
        + "package test;\n"
        + "import android.app.Activity;\n"
        + "import butterknife.OnClick;\n"
        + "public final class Test extends Activity {\n"
        + "  @OnClick(B.id.one) void doStuff() {}\n"
        + "}"
    );

    JavaFileObject expectedSource = JavaFileObjects.forSourceString("test/Test$$ViewBinder", ""
        + "package test;\n"
        + "import android.view.View;\n"
        + "import butterknife.Unbinder;\n"
        + "import butterknife.internal.DebouncingOnClickListener;\n"
        + "import butterknife.internal.Finder;\n"
        + "import butterknife.internal.ViewBinder;\n"
        + "import java.lang.IllegalStateException;\n"
        + "import java.lang.Object;\n"
        + "import java.lang.Override;\n"
        + "public final class Test$$ViewBinder implements ViewBinder<Test> {\n"
        + "  @Override\n"
        + "  public Unbinder bind(Finder finder, final Test target, Object source) {\n"
        + "    return new InnerUnbinder(target, finder, source);\n"
        + "  }\n"
        + "  private static final class InnerUnbinder implements Unbinder {\n"
        + "    private Test target;\n"
        + "    private View testRidone;\n"
        + "    InnerUnbinder(final Test target, Finder finder, Object source) {\n"
        + "      this.target = target;\n"
        + "      View view;\n"
        + "      view = finder.findRequiredView(source, test.R.id.one, \"method 'doStuff'\");\n"
        + "      testRidone = view;\n"
        + "      view.setOnClickListener(new DebouncingOnClickListener() {\n"
        + "        @Override\n"
        + "        public void doClick(View p0) {\n"
        + "          target.doStuff();\n"
        + "        }\n"
        + "      });\n"
        + "    }\n"
        + "    @Override\n"
        + "    public void unbind() {\n"
        + "      if (this.target == null) throw new IllegalStateException(\"Bindings already cleared.\");\n"
        + "      testRidone.setOnClickListener(null);\n"
        + "      testRidone = null;\n"
        + "      this.target = null;\n"
        + "    }\n"
        + "  }\n"
        + "}"
    );

    assertAbout(javaSource()).that(source)
        .processedWith(new ButterKnifeProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(expectedSource);
  }

  @Test public void onClickMultipleBindings() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", ""
        + "package test;\n"
        + "import android.view.View;\n"
        + "import android.app.Activity;\n"
        + "import butterknife.OnClick;\n"
        + "public class Test extends Activity {\n"
        + "  @OnClick(B.id.one) void doStuff1() {}\n"
        + "  @OnClick(B.id.one) void doStuff2() {}\n"
        + "  @OnClick({B.id.one, B.id.two}) void doStuff3(View v) {}\n"
        + "}"
    );

    JavaFileObject expectedSource = JavaFileObjects.forSourceString("test/Test$$ViewBinder", ""
        + "package test;\n"
        + "import android.view.View;\n"
        + "import butterknife.Unbinder;\n"
        + "import butterknife.internal.DebouncingOnClickListener;\n"
        + "import butterknife.internal.Finder;\n"
        + "import butterknife.internal.ViewBinder;\n"
        + "import java.lang.IllegalStateException;\n"
        + "import java.lang.Object;\n"
        + "import java.lang.Override;\n"
        + "public class Test$$ViewBinder<T extends Test> implements ViewBinder<T> {\n"
        + "  @Override\n"
        + "  public Unbinder bind(Finder finder, T target, Object source) {\n"
        + "    return new InnerUnbinder(target, finder, source);\n"
        + "  }\n"
        + "  protected static class InnerUnbinder<T extends Test> implements Unbinder {\n"
        + "    protected T target;\n"
        + "    private View testRidone;\n"
        + "    private View testRidtwo;\n"
        + "    protected InnerUnbinder(final T target, Finder finder, Object source) {\n"
        + "      this.target = target;\n"
        + "      View view;\n"
        + "      view = finder.findRequiredView(source, test.R.id.one, \"method 'doStuff1', method 'doStuff2', and method 'doStuff3'\");\n"
        + "      testRidone = view;\n"
        + "      view.setOnClickListener(new DebouncingOnClickListener() {\n"
        + "        @Override\n"
        + "        public void doClick(View p0) {\n"
        + "          target.doStuff1();\n"
        + "          target.doStuff2();\n"
        + "          target.doStuff3(p0);\n"
        + "        }\n"
        + "      });\n"
        + "      view = finder.findRequiredView(source, test.R.id.two, \"method 'doStuff3'\");\n"
        + "      testRidtwo = view;\n"
        + "      view.setOnClickListener(new DebouncingOnClickListener() {\n"
        + "        @Override\n"
        + "        public void doClick(View p0) {\n"
        + "          target.doStuff3(p0);\n"
        + "        }\n"
        + "      });\n"
        + "    }\n"
        + "    @Override\n"
        + "    public void unbind() {\n"
        + "      if (this.target == null) throw new IllegalStateException(\"Bindings already cleared.\");\n"
        + "      testRidone.setOnClickListener(null);\n"
        + "      testRidone = null;\n"
        + "      testRidtwo.setOnClickListener(null);\n"
        + "      testRidtwo = null;\n"
        + "      this.target = null;\n"
        + "    }\n"
        + "  }\n"
        + "}"
    );

    assertAbout(javaSource()).that(source)
        .processedWith(new ButterKnifeProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(expectedSource);
  }

  @Test public void findOnlyCalledOnce() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", ""
        + "package test;\n"
        + "import android.app.Activity;\n"
        + "import android.view.View;\n"
        + "import butterknife.BindView;\n"
        + "import butterknife.OnClick;\n"
        + "public class Test extends Activity {\n"
        + "  @BindView(B.id.one) View view;\n"
        + "  @OnClick(B.id.one) void doStuff() {}\n"
        + "}"
    );

    JavaFileObject expectedSource = JavaFileObjects.forSourceString("test/Test$$ViewBinder", ""
        + "package test;\n"
        + "import android.view.View;\n"
        + "import butterknife.Unbinder;\n"
        + "import butterknife.internal.DebouncingOnClickListener;\n"
        + "import butterknife.internal.Finder;\n"
        + "import butterknife.internal.ViewBinder;\n"
        + "import java.lang.IllegalStateException;\n"
        + "import java.lang.Object;\n"
        + "import java.lang.Override;\n"
        + "public class Test$$ViewBinder<T extends Test> implements ViewBinder<T> {\n"
        + "  @Override\n"
        + "  public Unbinder bind(Finder finder, T target, Object source) {\n"
        + "    return new InnerUnbinder(target, finder, source);\n"
        + "  }\n"
        + "  protected static class InnerUnbinder<T extends Test> implements Unbinder {\n"
        + "    protected T target;\n"
        + "    private View testRidone;\n"
        + "    protected InnerUnbinder(final T target, Finder finder, Object source) {\n"
        + "      this.target = target;\n"
        + "      View view;\n"
        + "      view = finder.findRequiredView(source, test.R.id.one, \"field 'view' and method 'doStuff'\");\n"
        + "      target.view = view;\n"
        + "      testRidone = view;\n"
        + "      view.setOnClickListener(new DebouncingOnClickListener() {\n"
        + "        @Override\n"
        + "        public void doClick(View p0) {\n"
        + "          target.doStuff();\n"
        + "        }\n"
        + "      });\n"
        + "    }\n"
        + "    @Override\n"
        + "    public void unbind() {\n"
        + "      T target = this.target;\n"
        + "      if (target == null) throw new IllegalStateException(\"Bindings already cleared.\");\n"
        + "      target.view = null;\n"
        + "      testRidone.setOnClickListener(null);\n"
        + "      testRidone = null;\n"
        + "      this.target = null;\n"
        + "    }\n"
        + "  }\n"
        + "}"
    );

    assertAbout(javaSource()).that(source)
        .processedWith(new ButterKnifeProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(expectedSource);
  }

  @Test public void methodVisibility() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.app.Activity;",
        "import android.view.View;",
        "import butterknife.OnClick;",
        "public class Test extends Activity {",
        "  @OnClick(B.id.one) public void thing1() {}",
        "  @OnClick(B.id.two) void thing2() {}",
        "  @OnClick(B.id.three) protected void thing3() {}",
        "}"
    ));

    assertAbout(javaSource()).that(source)
        .processedWith(new ButterKnifeProcessor())
        .compilesWithoutError();
  }

  @Test public void methodCastsArgument() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", ""
        + "package test;\n"
        + "import android.app.Activity;\n"
        + "import android.view.View;\n"
        + "import android.widget.Button;\n"
        + "import android.widget.TextView;\n"
        + "import butterknife.OnClick;\n"
        + "public class Test extends Activity {\n"
        + "  interface TestInterface {}\n"
        + "  @OnClick(B.id.zero) void click0() {}\n"
        + "  @OnClick(B.id.one) void click1(View view) {}\n"
        + "  @OnClick(B.id.two) void click2(TextView view) {}\n"
        + "  @OnClick(B.id.three) void click3(Button button) {}\n"
        + "  @OnClick(B.id.four) void click4(TestInterface thing) {}\n"
        + "}"
    );

    JavaFileObject expectedSource = JavaFileObjects.forSourceString("test/Test$$ViewBinder", ""
        + "package test;\n"
        + "import android.view.View;\n"
        + "import android.widget.Button;\n"
        + "import android.widget.TextView;\n"
        + "import butterknife.Unbinder;\n"
        + "import butterknife.internal.DebouncingOnClickListener;\n"
        + "import butterknife.internal.Finder;\n"
        + "import butterknife.internal.ViewBinder;\n"
        + "import java.lang.IllegalStateException;\n"
        + "import java.lang.Object;\n"
        + "import java.lang.Override;\n"
        + "public class Test$$ViewBinder<T extends Test> implements ViewBinder<T> {\n"
        + "  @Override\n"
        + "  public Unbinder bind(Finder finder, T target, Object source) {\n"
        + "    return new InnerUnbinder(target, finder, source);\n"
        + "  }\n"
        + "  protected static class InnerUnbinder<T extends Test> implements Unbinder {\n"
        + "    protected T target;\n"
        + "    private View testRidzero;\n"
        + "    private View testRidone;\n"
        + "    private View testRidtwo;\n"
        + "    private View testRidthree;\n"
        + "    private View testRidfour;\n"
        + "    protected InnerUnbinder(final T target, final Finder finder, Object source) {\n"
        + "      this.target = target;\n"
        + "      View view;\n"
        + "      view = finder.findRequiredView(source, test.R.id.zero, \"method 'click0'\");\n"
        + "      testRidzero = view;\n"
        + "      view.setOnClickListener(new DebouncingOnClickListener() {\n"
        + "        @Override\n"
        + "        public void doClick(View p0) {\n"
        + "          target.click0();\n"
        + "        }\n"
        + "      });\n"
        + "      view = finder.findRequiredView(source, test.R.id.one, \"method 'click1'\");\n"
        + "      testRidone = view;\n"
        + "      view.setOnClickListener(new DebouncingOnClickListener() {\n"
        + "        @Override\n"
        + "        public void doClick(View p0) {\n"
        + "          target.click1(p0);\n"
        + "        }\n"
        + "      });\n"
        + "      view = finder.findRequiredView(source, test.R.id.two, \"method 'click2'\");\n"
        + "      testRidtwo = view;\n"
        + "      view.setOnClickListener(new DebouncingOnClickListener() {\n"
        + "        @Override\n"
        + "        public void doClick(View p0) {\n"
        + "          target.click2(finder.<TextView>castParam(p0, \"doClick\", 0, \"click2\", 0));\n"
        + "        }\n"
        + "      });\n"
        + "      view = finder.findRequiredView(source, test.R.id.three, \"method 'click3'\");\n"
        + "      testRidthree = view;\n"
        + "      view.setOnClickListener(new DebouncingOnClickListener() {\n"
        + "        @Override\n"
        + "        public void doClick(View p0) {\n"
        + "          target.click3(finder.<Button>castParam(p0, \"doClick\", 0, \"click3\", 0));\n"
        + "        }\n"
        + "      });\n"
        + "      view = finder.findRequiredView(source, test.R.id.four, \"method 'click4'\");\n"
        + "      testRidfour = view;\n"
        + "      view.setOnClickListener(new DebouncingOnClickListener() {\n"
        + "        @Override\n"
        + "        public void doClick(View p0) {\n"
        + "          target.click4(finder.<Test.TestInterface>castParam(p0, \"doClick\", 0, \"click4\", 0));\n"
        + "        }\n"
        + "      });\n"
        + "    }\n"
        + "    @Override\n"
        + "    public void unbind() {\n"
        + "      if (this.target == null) throw new IllegalStateException(\"Bindings already cleared.\");\n"
        + "      testRidzero.setOnClickListener(null);\n"
        + "      testRidzero = null;\n"
        + "      testRidone.setOnClickListener(null);\n"
        + "      testRidone = null;\n"
        + "      testRidtwo.setOnClickListener(null);\n"
        + "      testRidtwo = null;\n"
        + "      testRidthree.setOnClickListener(null);\n"
        + "      testRidthree = null;\n"
        + "      testRidfour.setOnClickListener(null);\n"
        + "      testRidfour = null;\n"
        + "      this.target = null;\n"
        + "    }\n"
        + "  }\n"
        + "}"
    );

    assertAbout(javaSource()).that(source)
        .processedWith(new ButterKnifeProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(expectedSource);
  }

  @Test public void methodWithMultipleIds() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", ""
        + "package test;\n"
        + "import android.app.Activity;\n"
        + "import android.view.View;\n"
        + "import butterknife.OnClick;\n"
        + "public class Test extends Activity {\n"
        + "  @OnClick({B.id.one, B.id.two, B.id.three}) void click() {}\n"
        + "}"
    );

    JavaFileObject expectedSource = JavaFileObjects.forSourceString("test/Test$$ViewBinder", ""
        + "package test;\n"
        + "import android.view.View;\n"
        + "import butterknife.Unbinder;\n"
        + "import butterknife.internal.DebouncingOnClickListener;\n"
        + "import butterknife.internal.Finder;\n"
        + "import butterknife.internal.ViewBinder;\n"
        + "import java.lang.IllegalStateException;\n"
        + "import java.lang.Object;\n"
        + "import java.lang.Override;\n"
        + "public class Test$$ViewBinder<T extends Test> implements ViewBinder<T> {\n"
        + "  @Override\n"
        + "  public Unbinder bind(Finder finder, T target, Object source) {\n"
        + "    return new InnerUnbinder(target, finder, source);\n"
        + "  }\n"
        + "  protected static class InnerUnbinder<T extends Test> implements Unbinder {\n"
        + "    protected T target;\n"
        + "    private View testRidone;\n"
        + "    private View testRidtwo;\n"
        + "    private View testRidthree;\n"
        + "    protected InnerUnbinder(final T target, Finder finder, Object source) {\n"
        + "      this.target = target;\n"
        + "      View view;\n"
        + "      view = finder.findRequiredView(source, test.R.id.one, \"method 'click'\");\n"
        + "      testRidone = view;\n"
        + "      view.setOnClickListener(new DebouncingOnClickListener() {\n"
        + "        @Override\n"
        + "        public void doClick(View p0) {\n"
        + "          target.click();\n"
        + "        }\n"
        + "      });\n"
        + "      view = finder.findRequiredView(source, test.R.id.two, \"method 'click'\");\n"
        + "      testRidtwo = view;\n"
        + "      view.setOnClickListener(new DebouncingOnClickListener() {\n"
        + "        @Override\n"
        + "        public void doClick(View p0) {\n"
        + "          target.click();\n"
        + "        }\n"
        + "      });\n"
        + "      view = finder.findRequiredView(source, test.R.id.three, \"method 'click'\");\n"
        + "      testRidthree = view;\n"
        + "      view.setOnClickListener(new DebouncingOnClickListener() {\n"
        + "        @Override\n"
        + "        public void doClick(View p0) {\n"
        + "          target.click();\n"
        + "        }\n"
        + "      });\n"
        + "    }\n"
        + "    @Override\n"
        + "    public void unbind() {\n"
        + "      if (this.target == null) throw new IllegalStateException(\"Bindings already cleared.\");\n"
        + "      testRidone.setOnClickListener(null);\n"
        + "      testRidone = null;\n"
        + "      testRidtwo.setOnClickListener(null);\n"
        + "      testRidtwo = null;\n"
        + "      testRidthree.setOnClickListener(null);\n"
        + "      testRidthree = null;\n"
        + "      this.target = null;\n"
        + "    }\n"
        + "  }\n"
        + "}"
    );

    assertAbout(javaSource()).that(source)
        .processedWith(new ButterKnifeProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(expectedSource);
  }

  @Test public void nullable() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.app.Activity;",
        "import butterknife.OnClick;",
        "import butterknife.Optional;",
        "public class Test extends Activity {",
        "  @Optional @OnClick(B.id.one) void doStuff() {}",
        "}"));

    JavaFileObject expectedSource = JavaFileObjects.forSourceString("test/Test$$ViewBinder", ""
        + "package test;\n"
        + "import android.view.View;\n"
        + "import butterknife.Unbinder;\n"
        + "import butterknife.internal.DebouncingOnClickListener;\n"
        + "import butterknife.internal.Finder;\n"
        + "import butterknife.internal.ViewBinder;\n"
        + "import java.lang.IllegalStateException;\n"
        + "import java.lang.Object;\n"
        + "import java.lang.Override;\n"
        + "public class Test$$ViewBinder<T extends Test> implements ViewBinder<T> {\n"
        + "  @Override\n"
        + "  public Unbinder bind(Finder finder, T target, Object source) {\n"
        + "    return new InnerUnbinder(target, finder, source);\n"
        + "  }\n"
        + "  protected static class InnerUnbinder<T extends Test> implements Unbinder {\n"
        + "    protected T target;\n"
        + "    private View testRidone;\n"
        + "    protected InnerUnbinder(final T target, Finder finder, Object source) {\n"
        + "      this.target = target;\n"
        + "      View view;\n"
        + "      view = finder.findOptionalView(source, test.R.id.one);\n"
        + "      if (view != null) {\n"
        + "        testRidone = view;\n"
        + "        view.setOnClickListener(new DebouncingOnClickListener() {\n"
        + "          @Override\n"
        + "          public void doClick(View p0) {\n"
        + "            target.doStuff();\n"
        + "          }\n"
        + "        });\n"
        + "      }\n"
        + "    }\n"
        + "    @Override\n"
        + "    public void unbind() {\n"
        + "      if (this.target == null) throw new IllegalStateException(\"Bindings already cleared.\");\n"
        + "      if (testRidone != null) {\n"
        + "        testRidone.setOnClickListener(null);\n"
        + "        testRidone = null;\n"
        + "      }\n"
        + "      this.target = null;\n"
        + "    }\n"
        + "  }\n"
        + "}"
    );

    assertAbout(javaSource()).that(source)
        .processedWith(new ButterKnifeProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(expectedSource);
  }

  @Test public void optionalAndRequiredSkipsNullCheck() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.app.Activity;",
        "import android.view.View;",
        "import butterknife.BindView;",
        "import butterknife.OnClick;",
        "import butterknife.Optional;",
        "public class Test extends Activity {",
        "  @BindView(B.id.one) View view;",
        "  @Optional @OnClick(B.id.one) void doStuff() {}",
        "}"));

    JavaFileObject expectedSource = JavaFileObjects.forSourceString("test/Test$$ViewBinder", ""
        + "package test;\n"
        + "import android.view.View;\n"
        + "import butterknife.Unbinder;\n"
        + "import butterknife.internal.DebouncingOnClickListener;\n"
        + "import butterknife.internal.Finder;\n"
        + "import butterknife.internal.ViewBinder;\n"
        + "import java.lang.IllegalStateException;\n"
        + "import java.lang.Object;\n"
        + "import java.lang.Override;\n"
        + "public class Test$$ViewBinder<T extends Test> implements ViewBinder<T> {\n"
        + "  @Override\n"
        + "  public Unbinder bind(Finder finder, T target, Object source) {\n"
        + "    return new InnerUnbinder(target, finder, source);\n"
        + "  }\n"
        + "  protected static class InnerUnbinder<T extends Test> implements Unbinder {\n"
        + "    protected T target;\n"
        + "    private View testRidone;\n"
        + "    protected InnerUnbinder(final T target, Finder finder, Object source) {\n"
        + "      this.target = target;\n"
        + "      View view;\n"
        + "      view = finder.findRequiredView(source, test.R.id.one, \"field 'view'\");\n"
        + "      target.view = view;\n"
        + "      testRidone = view;\n"
        + "      view.setOnClickListener(new DebouncingOnClickListener() {\n"
        + "        @Override\n"
        + "        public void doClick(View p0) {\n"
        + "          target.doStuff();\n"
        + "        }\n"
        + "      });\n"
        + "    }\n"
        + "    @Override\n"
        + "    public void unbind() {\n"
        + "      T target = this.target;\n"
        + "      if (target == null) throw new IllegalStateException(\"Bindings already cleared.\");\n"
        + "      target.view = null;\n"
        + "      testRidone.setOnClickListener(null);\n"
        + "      testRidone = null;\n"
        + "      this.target = null;\n"
        + "    }\n"
        + "  }\n"
        + "}"
    );

    assertAbout(javaSource()).that(source)
        .processedWith(new ButterKnifeProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(expectedSource);
  }

  @Test public void failsInJavaPackage() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package java.test;",
        "import butterknife.OnClick;",
        "public class Test {",
        "  @OnClick(test.B.id.one) void doStuff() {}",
        "}"
    ));

    assertAbout(javaSource()).that(source)
        .processedWith(new ButterKnifeProcessor())
        .failsToCompile()
        .withErrorContaining(
            "@OnClick-annotated class incorrectly in Java framework package. (java.test.Test)")
        .in(source).onLine(4);
  }

  @Test public void failsInAndroidPackage() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package android.test;",
        "import butterknife.OnClick;",
        "public class Test {",
        "  @OnClick(test.B.id.one) void doStuff() {}",
        "}"
    ));

    assertAbout(javaSource()).that(source)
        .processedWith(new ButterKnifeProcessor())
        .failsToCompile()
        .withErrorContaining(
            "@OnClick-annotated class incorrectly in Android framework package. (android.test.Test)")
        .in(source).onLine(4);
  }

  @Test public void failsIfHasReturnType() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.app.Activity;",
        "import butterknife.OnClick;",
        "public class Test extends Activity {",
        "  @OnClick(B.id.one)",
        "  public String doStuff() {",
        "  }",
        "}"));

    assertAbout(javaSource()).that(source)
        .processedWith(new ButterKnifeProcessor())
        .failsToCompile()
        .withErrorContaining("@OnClick methods must have a 'void' return type. (test.Test.doStuff)")
        .in(source).onLine(6);
  }

  @Test public void failsIfPrivateMethod() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.app.Activity;",
        "import butterknife.OnClick;",
        "public class Test extends Activity {",
        "  @OnClick(B.id.one)",
        "  private void doStuff() {",
        "  }",
        "}"));

    assertAbout(javaSource()).that(source)
        .processedWith(new ButterKnifeProcessor())
        .failsToCompile()
        .withErrorContaining("@OnClick methods must not be private or static. (test.Test.doStuff)")
        .in(source).onLine(6);
  }

  @Test public void failsIfStatic() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.app.Activity;",
        "import butterknife.OnClick;",
        "public class Test extends Activity {",
        "  @OnClick(B.id.one)",
        "  public static void doStuff() {",
        "  }",
        "}"));

    assertAbout(javaSource()).that(source)
        .processedWith(new ButterKnifeProcessor())
        .failsToCompile()
        .withErrorContaining("@OnClick methods must not be private or static. (test.Test.doStuff)")
        .in(source).onLine(6);
  }

  @Test public void failsIfParameterNotView() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.app.Activity;",
        "import butterknife.OnClick;",
        "public class Test extends Activity {",
        "  @OnClick(B.id.one)",
        "  public void doStuff(String thing) {",
        "  }",
        "}"));

    assertAbout(javaSource()).that(source)
        .processedWith(new ButterKnifeProcessor())
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
        "import butterknife.OnClick;",
        "public class Test extends Activity {",
        "  @OnClick(B.id.one)",
        "  public void doStuff(View thing, View otherThing) {",
        "  }",
        "}"));

    assertAbout(javaSource()).that(source)
        .processedWith(new ButterKnifeProcessor())
        .failsToCompile()
        .withErrorContaining(
            "@OnClick methods can have at most 1 parameter(s). (test.Test.doStuff)")
        .in(source).onLine(7);
  }

  @Test public void failsIfInInterface() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import butterknife.OnClick;",
        "public interface Test {",
        "  @OnClick(B.id.one)",
        "  void doStuff();",
        "}"));

    assertAbout(javaSource()).that(source)
        .processedWith(new ButterKnifeProcessor())
        .failsToCompile()
        .withErrorContaining(
            "@OnClick methods may only be contained in classes. (test.Test.doStuff)")
        .in(source).onLine(3);
  }

  @Test public void failsIfHasDuplicateIds() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.app.Activity;",
        "import butterknife.OnClick;",
        "public class Test extends Activity {",
        "  @OnClick({B.id.one, B.id.two, B.id.three, B.id.one})",
        "  void doStuff() {",
        "  }",
        "}"));

    assertAbout(javaSource()).that(source)
        .processedWith(new ButterKnifeProcessor())
        .failsToCompile()
        .withErrorContaining(
            "@OnClick annotation for method contains duplicate ID <test.R.id.one>. (test.Test.doStuff)")
        .in(source).onLine(6);
  }
}
