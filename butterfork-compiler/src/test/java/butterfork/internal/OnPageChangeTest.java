package butterfork.internal;

import com.google.common.base.Joiner;
import com.google.testing.compile.JavaFileObjects;

import org.junit.Test;

import javax.tools.JavaFileObject;

import static com.google.common.truth.Truth.ASSERT;
import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;

public class OnPageChangeTest {
  @Test public void pageChange() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.app.Activity;",
        "import butterfork.OnPageChange;",
        "public class Test extends Activity {",
        "  @OnPageChange(\"one\") void doStuff() {}",
        "}"
    ));

    JavaFileObject expectedSource = JavaFileObjects.forSourceString("test/Test$$ViewBinder",
        Joiner.on('\n').join(
            "package test;",
            "import android.support.v4.view.ViewPager;",
            "import android.view.View;",
            "import butterfork.ButterFork;",
            "import butterfork.internal.R;",
            "import java.lang.Object;",
            "import java.lang.Override;",
            "public class Test$$ViewBinder<T extends Test> implements ButterFork.ViewBinder<T> {",
            "  @Override public void bind(final ButterFork.Finder finder, final T target, Object source) {",
            "    View view;",
            "    view = finder.findRequiredView(source, R.id.one, \"method 'doStuff'\");",
            "    ((ViewPager) view).setOnPageChangeListener(new ViewPager.OnPageChangeListener() {",
            "      @Override public void onPageSelected(int p0) {",
            "        target.doStuff();",
            "      }",
            "      @Override public void onPageScrolled(int p0, float p1, int p2) {",
            "      }",
            "      @Override public void onPageScrollStateChanged(int p0) {",
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
}
