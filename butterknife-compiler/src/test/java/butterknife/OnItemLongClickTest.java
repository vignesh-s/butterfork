package butterknife;

import com.google.testing.compile.JavaFileObjects;

import org.junit.Test;

import javax.tools.JavaFileObject;

import butterknife.compiler.ButterKnifeProcessor;

import static com.google.common.truth.Truth.assertAbout;
import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;

public class OnItemLongClickTest {
  @Test public void itemLongClick() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", ""
        + "package test;\n"
        + "import android.app.Activity;\n"
        + "import butterknife.OnItemLongClick;\n"
        + "public class Test extends Activity {\n"
        + "  @OnItemLongClick(B.id.one) boolean doStuff() { return false; }\n"
        + "}"
    );

    JavaFileObject expectedSource = JavaFileObjects.forSourceString("test/Test$$ViewBinder", ""
        + "package test;\n"
        + "import android.view.View;\n"
        + "import android.widget.AdapterView;\n"
        + "import butterknife.Unbinder;\n"
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
        + "      ((AdapterView<?>) view).setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {\n"
        + "        @Override\n"
        + "        public boolean onItemLongClick(AdapterView<?> p0, View p1, int p2, long p3) {\n"
        + "          return target.doStuff();\n"
        + "        }\n"
        + "      });\n"
        + "    }\n"
        + "    @Override\n"
        + "    public void unbind() {\n"
        + "      if (this.target == null) throw new IllegalStateException(\"Bindings already cleared.\");\n"
        + "      ((AdapterView<?>) testRidone).setOnItemLongClickListener(null);\n"
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
}
