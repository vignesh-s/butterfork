package butterfork.internal;

import com.google.common.truth.Truth;

import org.junit.Test;

import static com.google.common.truth.Truth.assertThat;

public final class UtilsTest {
  @Test public void listOfFiltersNull() {
    Truth.assertThat(Utils.listOf(null, null, null)).isEmpty();
    Truth.assertThat(Utils.listOf("One", null, null)).containsExactly("One");
    Truth.assertThat(Utils.listOf(null, "One", null)).containsExactly("One");
    Truth.assertThat(Utils.listOf(null, null, "One")).containsExactly("One");
    Truth.assertThat(Utils.listOf("One", "Two", null)).containsExactly("One", "Two");
    Truth.assertThat(Utils.listOf("One", null, "Two")).containsExactly("One", "Two");
    Truth.assertThat(Utils.listOf(null, "One", "Two")).containsExactly("One", "Two");
  }

  @Test public void arrayOfFiltersNull() {
    Truth.assertThat(Utils.arrayOf(null, null, null)).isEmpty();
    Truth.assertThat(Utils.arrayOf("One", null, null)).asList().containsExactly("One");
    Truth.assertThat(Utils.arrayOf(null, "One", null)).asList().containsExactly("One");
    Truth.assertThat(Utils.arrayOf(null, null, "One")).asList().containsExactly("One");
    Truth.assertThat(Utils.arrayOf("One", "Two", null)).asList().containsExactly("One", "Two");
    Truth.assertThat(Utils.arrayOf("One", null, "Two")).asList().containsExactly("One", "Two");
    Truth.assertThat(Utils.arrayOf(null, "One", "Two")).asList().containsExactly("One", "Two");
  }
}
