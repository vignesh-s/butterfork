package butterfork.internal;

import butterfork.Unbinder;

public interface ViewBinder<T> {
  Unbinder bind(Finder finder, T target, Object source);
}
