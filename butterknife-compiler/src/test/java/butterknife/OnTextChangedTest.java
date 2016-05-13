package butterknife;

import com.google.testing.compile.JavaFileObjects;

import org.junit.Test;

import javax.tools.JavaFileObject;

import butterknife.compiler.ButterKnifeProcessor;

import static com.google.common.truth.Truth.assertAbout;
import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;

public class OnTextChangedTest {
  @Test public void textChanged() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", ""
        + "package test;\n"
        + "import android.app.Activity;\n"
        + "import butterknife.OnTextChanged;\n"
        + "public class Test extends Activity {\n"
        + "  @OnTextChanged(B.id.one) void doStuff() {}\n"
        + "}"
    );

    JavaFileObject expectedSource = JavaFileObjects.forSourceString("test/Test$$ViewBinder", ""
        + "package test;\n"
        + "import android.text.Editable;\n"
        + "import android.text.TextWatcher;\n"
        + "import android.view.View;\n"
        + "import android.widget.TextView;\n"
        + "import butterknife.Unbinder;\n"
        + "import butterknife.internal.Finder;\n"
        + "import butterknife.internal.ViewBinder;\n"
        + "import java.lang.CharSequence;\n"
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
        + "    private TextWatcher testRidoneTextWatcher;\n"
        + "    protected InnerUnbinder(final T target, Finder finder, Object source) {\n"
        + "      this.target = target;\n"
        + "      View view;\n"
        + "      view = finder.findRequiredView(source, test.R.id.one, \"method 'doStuff'\");\n"
        + "      testRidone = view;\n"
        + "      this.testRidoneTextWatcher = new TextWatcher() {\n"
        + "        @Override\n"
        + "        public void onTextChanged(CharSequence p0, int p1, int p2, int p3) {\n"
        + "          target.doStuff();\n"
        + "        }\n"
        + "        @Override\n"
        + "        public void beforeTextChanged(CharSequence p0, int p1, int p2, int p3) {\n"
        + "        }\n"
        + "        @Override\n"
        + "        public void afterTextChanged(Editable p0) {\n"
        + "        }\n"
        + "      };\n"
        + "      ((TextView) view).addTextChangedListener(testRidoneTextWatcher);\n"
        + "    }\n"
        + "    @Override\n"
        + "    public void unbind() {\n"
        + "      if (this.target == null) throw new IllegalStateException(\"Bindings already cleared.\");\n"
        + "      ((TextView) testRidone).removeTextChangedListener(testRidoneTextWatcher);\n"
        + "      testRidoneTextWatcher = null;\n"
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
