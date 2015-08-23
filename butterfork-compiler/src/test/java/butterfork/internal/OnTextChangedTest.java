package butterfork.internal;

import com.google.common.base.Joiner;
import com.google.testing.compile.JavaFileObjects;

import org.junit.Test;

import javax.tools.JavaFileObject;

import static com.google.common.truth.Truth.ASSERT;
import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;

public class OnTextChangedTest {
  @Test public void textChanged() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.app.Activity;",
        "import butterfork.BindResources;",
        "import butterfork.OnTextChanged;",
        "@BindResources(butterfork.internal.R.class)",
        "public class Test extends Activity {",
        "  @OnTextChanged(\"one\") void doStuff() {}",
        "}"
    ));

    JavaFileObject expectedSource = JavaFileObjects.forSourceString("test/Test$$ViewBinder",
        Joiner.on('\n').join(
            "package test;",
            "import android.text.Editable;",
            "import android.text.TextWatcher;",
            "import android.view.View;",
            "import android.widget.TextView;",
            "import butterfork.ButterFork;",
            "import butterfork.internal.R;",
            "import java.lang.CharSequence;",
            "import java.lang.Object;",
            "import java.lang.Override;",
            "public class Test$$ViewBinder<T extends Test> implements ButterFork.ViewBinder<T> {",
            "  @Override public void bind(final ButterFork.Finder finder, final T target, Object source) {",
            "    View view;",
            "    view = finder.findRequiredView(source, R.id.one, \"method 'doStuff'\");",
            "    ((TextView) view).addTextChangedListener(new TextWatcher() {",
            "      @Override public void onTextChanged(CharSequence p0, int p1, int p2, int p3) {",
            "        target.doStuff();",
            "      }",
            "      @Override public void beforeTextChanged(CharSequence p0, int p1, int p2, int p3) {",
            "      }",
            "      @Override public void afterTextChanged(Editable p0) {",
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
}
