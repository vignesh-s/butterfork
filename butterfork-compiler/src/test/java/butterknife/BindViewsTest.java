package butterknife;

import com.google.common.base.Joiner;
import com.google.testing.compile.JavaFileObjects;

import org.junit.Test;

import javax.tools.JavaFileObject;

import butterknife.compiler.ButterKnifeProcessor;

import static com.google.common.truth.Truth.assertAbout;
import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;

public class BindViewsTest {
  @Test public void fieldVisibility() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.app.Activity;",
        "import android.view.View;",
        "import butterknife.BindViews;",
        "import java.util.List;",
        "public class Test extends Activity {",
        "  @BindViews(B.id.one) public List<View> thing1;",
        "  @BindViews(B.id.two) List<View> thing2;",
        "  @BindViews(B.id.three) protected List<View> thing3;",
        "}"
    ));

    assertAbout(javaSource()).that(source)
        .processedWith(new ButterKnifeProcessor())
        .compilesWithoutError();
  }

  @Test public void bindingArray() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.app.Activity;",
        "import android.view.View;",
        "import butterknife.BindViews;",
        "public class Test extends Activity {",
        "    @BindViews({B.id.one, B.id.two, B.id.three}) View[] thing;",
        "}"
    ));

    JavaFileObject expectedSource = JavaFileObjects.forSourceString("test/Test$$ViewBinding", ""
        + "package test;\n"
        + "import butterknife.Unbinder;\n"
        + "import butterknife.internal.Finder;\n"
        + "import butterknife.internal.Utils;\n"
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
        + "    protected InnerUnbinder(T target, Finder finder, Object source) {\n"
        + "      this.target = target;\n"
        + "      target.thing = Utils.arrayOf(\n"
        + "          finder.findRequiredView(source, test.R.id.one, \"field 'thing'\"), \n"
        + "          finder.findRequiredView(source, test.R.id.two, \"field 'thing'\"), \n"
        + "          finder.findRequiredView(source, test.R.id.three, \"field 'thing'\"));\n"
        + "    }\n"
        + "    @Override\n"
        + "    public void unbind() {\n"
        + "      T target = this.target;\n"
        + "      if (target == null) throw new IllegalStateException(\"Bindings already cleared.\");\n"
        + "      target.thing = null;\n"
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

  @Test public void bindingArrayWithGenerics() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.app.Activity;",
        "import android.view.View;",
        "import butterknife.BindViews;",
        "public class Test<T extends View> extends Activity {",
        "    @BindViews({B.id.one, B.id.two, B.id.three}) T[] thing;",
        "}"
    ));

    JavaFileObject expectedSource = JavaFileObjects.forSourceString("test/Test$$ViewBinder", ""
        + "package test;\n"
        + "import butterknife.Unbinder;\n"
        + "import butterknife.internal.Finder;\n"
        + "import butterknife.internal.Utils;\n"
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
        + "    protected InnerUnbinder(T target, Finder finder, Object source) {\n"
        + "      this.target = target;\n"
        + "      target.thing = Utils.arrayOf(\n"
        + "          finder.findRequiredView(source, test.R.id.one, \"field 'thing'\"), \n"
        + "          finder.findRequiredView(source, test.R.id.two, \"field 'thing'\"), \n"
        + "          finder.findRequiredView(source, test.R.id.three, \"field 'thing'\"));\n"
        + "    }\n"
        + "    @Override\n"
        + "    public void unbind() {\n"
        + "      T target = this.target;\n"
        + "      if (target == null) throw new IllegalStateException(\"Bindings already cleared.\");\n"
        + "      target.thing = null;\n"
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

  @Test public void bindingArrayWithCast() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.app.Activity;",
        "import android.widget.TextView;",
        "import butterknife.BindViews;",
        "public class Test extends Activity {",
        "    @BindViews({B.id.one, B.id.two, B.id.three}) TextView[] thing;",
        "}"
    ));

    JavaFileObject expectedSource = JavaFileObjects.forSourceString("test/Test$$ViewBinder", ""
        + "package test;\n"
        + "import android.widget.TextView;\n"
        + "import butterknife.Unbinder;\n"
        + "import butterknife.internal.Finder;\n"
        + "import butterknife.internal.Utils;\n"
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
        + "    protected InnerUnbinder(T target, Finder finder, Object source) {\n"
        + "      this.target = target;\n"
        + "      target.thing = Utils.arrayOf(\n"
        + "          (TextView) finder.findRequiredView(source, test.R.id.one, \"field 'thing'\"), \n"
        + "          (TextView) finder.findRequiredView(source, test.R.id.two, \"field 'thing'\"), \n"
        + "          (TextView) finder.findRequiredView(source, test.R.id.three, \"field 'thing'\"));\n"
        + "    }\n"
        + "    @Override\n"
        + "    public void unbind() {\n"
        + "      T target = this.target;\n"
        + "      if (target == null) throw new IllegalStateException(\"Bindings already cleared.\");\n"
        + "      target.thing = null;\n"
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

  @Test public void bindingList() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.app.Activity;",
        "import android.view.View;",
        "import butterknife.BindViews;",
        "import java.util.List;",
        "public class Test extends Activity {",
        "    @BindViews({B.id.one, B.id.two, B.id.three}) List<View> thing;",
        "}"
    ));

    JavaFileObject expectedSource = JavaFileObjects.forSourceString("test/Test$$ViewBinder", ""
        + "package test;\n"
        + "import butterknife.Unbinder;\n"
        + "import butterknife.internal.Finder;\n"
        + "import butterknife.internal.Utils;\n"
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
        + "    protected InnerUnbinder(T target, Finder finder, Object source) {\n"
        + "      this.target = target;\n"
        + "      target.thing = Utils.listOf(\n"
        + "          finder.findRequiredView(source, test.R.id.one, \"field 'thing'\"), \n"
        + "          finder.findRequiredView(source, test.R.id.two, \"field 'thing'\"), \n"
        + "          finder.findRequiredView(source, test.R.id.three, \"field 'thing'\"));\n"
        + "    }\n"
        + "    @Override\n"
        + "    public void unbind() {\n"
        + "      T target = this.target;\n"
        + "      if (target == null) throw new IllegalStateException(\"Bindings already cleared.\");\n"
        + "      target.thing = null;\n"
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

  @Test public void bindingListOfInterface() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.app.Activity;",
        "import butterknife.BindViews;",
        "import java.util.List;",
        "public class Test {",
        "    interface TestInterface {}",
        "    @BindViews({B.id.one, B.id.two, B.id.three}) List<TestInterface> thing;",
        "}"
    ));

    JavaFileObject expectedSource = JavaFileObjects.forSourceString("test/Test$$ViewBinder", ""
        + "package test;\n"
        + "import butterknife.Unbinder;\n"
        + "import butterknife.internal.Finder;\n"
        + "import butterknife.internal.Utils;\n"
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
        + "    protected InnerUnbinder(T target, Finder finder, Object source) {\n"
        + "      this.target = target;\n"
        + "      target.thing = Utils.listOf(\n"
        + "          (Test.TestInterface) finder.findRequiredView(source, test.R.id.one, \"field 'thing'\"), \n"
        + "          (Test.TestInterface) finder.findRequiredView(source, test.R.id.two, \"field 'thing'\"), \n"
        + "          (Test.TestInterface) finder.findRequiredView(source, test.R.id.three, \"field 'thing'\"));\n"
        + "    }\n"
        + "    @Override\n"
        + "    public void unbind() {\n"
        + "      T target = this.target;\n"
        + "      if (target == null) throw new IllegalStateException(\"Bindings already cleared.\");\n"
        + "      target.thing = null;\n"
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

  @Test public void bindingListWithGenerics() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.app.Activity;",
        "import android.view.View;",
        "import butterknife.BindViews;",
        "import java.util.List;",
        "public class Test<T extends View> extends Activity {",
        "    @BindViews({B.id.one, B.id.two, B.id.three}) List<T> thing;",
        "}"
    ));

    JavaFileObject expectedSource = JavaFileObjects.forSourceString("test/Test$$ViewBinder", ""
        + "package test;\n"
        + "import butterknife.Unbinder;\n"
        + "import butterknife.internal.Finder;\n"
        + "import butterknife.internal.Utils;\n"
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
        + "    protected InnerUnbinder(T target, Finder finder, Object source) {\n"
        + "      this.target = target;\n"
        + "      target.thing = Utils.listOf(\n"
        + "          finder.findRequiredView(source, test.R.id.one, \"field 'thing'\"), \n"
        + "          finder.findRequiredView(source, test.R.id.two, \"field 'thing'\"), \n"
        + "          finder.findRequiredView(source, test.R.id.three, \"field 'thing'\"));\n"
        + "    }\n"
        + "    @Override\n"
        + "    public void unbind() {\n"
        + "      T target = this.target;\n"
        + "      if (target == null) throw new IllegalStateException(\"Bindings already cleared.\");\n"
        + "      target.thing = null;\n"
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

  @Test public void nullableList() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.app.Activity;",
        "import android.view.View;",
        "import butterknife.BindViews;",
        "import java.util.List;",
        "public class Test extends Activity {",
        "    @interface Nullable {}",
        "    @Nullable @BindViews({B.id.one, B.id.two, B.id.three}) List<View> thing;",
        "}"
    ));

    JavaFileObject expectedSource = JavaFileObjects.forSourceString("test/Test$$ViewBinder", ""
        + "package test;\n"
        + "import butterknife.Unbinder;\n"
        + "import butterknife.internal.Finder;\n"
        + "import butterknife.internal.Utils;\n"
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
        + "    protected InnerUnbinder(T target, Finder finder, Object source) {\n"
        + "      this.target = target;\n"
        + "      target.thing = Utils.listOf(\n"
        + "          finder.findOptionalView(source, test.R.id.one), \n"
        + "          finder.findOptionalView(source, test.R.id.two), \n"
        + "          finder.findOptionalView(source, test.R.id.three));\n"
        + "    }\n"
        + "    @Override\n"
        + "    public void unbind() {\n"
        + "      T target = this.target;\n"
        + "      if (target == null) throw new IllegalStateException(\"Bindings already cleared.\");\n"
        + "      target.thing = null;\n"
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

  @Test public void failsIfNoIds() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.view.View;",
        "import butterknife.BindViews;",
        "import java.util.List;",
        "public class Test {",
        "  @BindViews({}) List<View> thing;",
        "}"
    ));

    assertAbout(javaSource()).that(source)
        .processedWith(new ButterKnifeProcessor())
        .failsToCompile()
        .withErrorContaining("@BindViews must specify at least one ID. (test.Test.thing)")
        .in(source).onLine(6);
  }

  @Test public void failsIfNoGenericType() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import butterknife.BindViews;",
        "import java.util.List;",
        "public class Test {",
        "  @BindViews(B.id.one) List thing;",
        "}"
    ));

    assertAbout(javaSource()).that(source)
        .processedWith(new ButterKnifeProcessor())
        .failsToCompile()
        .withErrorContaining("@BindViews List must have a generic component. (test.Test.thing)")
        .in(source).onLine(5);
  }

  @Test public void failsIfUnsupportedCollection() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.view.View;",
        "import butterknife.BindViews;",
        "import java.util.Deque;",
        "public class Test {",
        "  @BindViews(B.id.one) Deque<View> thing;",
        "}"
    ));

    assertAbout(javaSource()).that(source)
        .processedWith(new ButterKnifeProcessor())
        .failsToCompile()
        .withErrorContaining("@BindViews must be a List or array. (test.Test.thing)")
        .in(source).onLine(6);
  }

  @Test public void failsIfGenericNotView() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.app.Activity;",
        "import butterknife.BindViews;",
        "import java.util.List;",
        "public class Test extends Activity {",
        "  @BindViews(B.id.one) List<String> thing;",
        "}"
    ));

    assertAbout(javaSource()).that(source)
        .processedWith(new ButterKnifeProcessor())
        .failsToCompile()
        .withErrorContaining("@BindViews List or array type must extend from View or be an interface. (test.Test.thing)")
        .in(source).onLine(6);
  }

  @Test public void failsIfArrayNotView() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.app.Activity;",
        "import butterknife.BindViews;",
        "public class Test extends Activity {",
        "  @BindViews(B.id.one) String[] thing;",
        "}"));

    assertAbout(javaSource()).that(source)
        .processedWith(new ButterKnifeProcessor())
        .failsToCompile()
        .withErrorContaining("@BindViews List or array type must extend from View or be an interface. (test.Test.thing)")
        .in(source).onLine(5);
  }

  @Test public void failsIfContainsDuplicateIds() throws Exception {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.app.Activity;",
        "import android.view.View;",
        "import butterknife.BindViews;",
        "import java.util.List;",
        "public class Test extends Activity {",
        "    @BindViews({B.id.one, B.id.one}) List<View> thing;",
        "}"
    ));

    assertAbout(javaSource()).that(source)
        .processedWith(new ButterKnifeProcessor())
        .failsToCompile()
        .withErrorContaining("@BindViews annotation contains duplicate ID <test.R.id.one>. (test.Test.thing)")
        .in(source).onLine(7);
  }
}
