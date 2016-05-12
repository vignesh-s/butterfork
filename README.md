Butter Fork
===========

![Logo](website/static/logo.png)

A fork of [ButterKnife][1] with support for Library projects (Solves the non-final id problem).

This change is incompatible with the current library and is therefore distributed under a different name. See the *Why ButterFork?* section below.

Usage is identical apart from 'Fork' instead of 'Knife' and 'B' instead of 'R'. The solution does not depend on reflection.

```java
@Bind(B.id.user) EditText username;
@Bind(B.id.pass) EditText password;
//..
ButterFork.bind(this);
```

Dependency
----------
Latest version: [![Maven Central][2]][3]

There are three components; the runtime module (butterfork-binder), annotation processor (butterfork-compiler) and a gradle plugin (butterfork-plugin). You will also need Hugo Visser's [android-apt][4].

Your `build.gradle` should look like this:
```groovy

buildscript {
    dependencies {
        //..
        classpath 'com.neenbedankt.gradle.plugins:android-apt:1.7'
        classpath 'com.oguzbabaoglu:butterfork-plugin:1.0.0'
    }
}

// It is important that the library plugin is applied before butterfork-plugin
apply plugin: 'com.android.library'
apply plugin: 'com.neenbedankt.android-apt'
apply plugin: 'com.oguzbabaoglu.butterfork-plugin'

dependencies {
    compile 'com.oguzbabaoglu:butterfork-binder:1.0.0'
    apt 'com.oguzbabaoglu:butterfork-compiler:1.0.0'
}
```

How does it work?
-----------------

On a clean build, the first thing to run is the plugin. The plugin adds a `generateB` task right after `processResources` (task that generates R) which generates a B class containing all field names in the R class as final String values.
```java
public final class B {
  public static final class id {
    public static final String text_view = "text_view";
  }
  public static final class string {
    public static final String app_name = "app_name";
  }
}
```

Values from the generated B class should be used in place of R values in the annotations.
```java
@Bind(B.id.text_view) TextView textView;
// Instead of
@Bind(R.id.text_view) TextView textView;
```

The processor reads these annotations and generates binder classes. ButterKnife uses the resolved int value from the R class, but ButterFork instead places a reference back to the R class inside the generated code.

```java
// What the ButterKnife processor generates:
view = finder.findRequiredView(source, 2131755092, "field 'textView'");
// What the ButterFork processor generates:
view = finder.findRequiredView(source, R.id.textView, "field 'textView'");
```

The runtime module is identical to ButterKnife (the binder), since the explicit R reference is resolved to an int when the generated classes are compiled.

Limitations
-----------
- Currently B is only generated for the 'local' R. It it is not possible to use R references from other libraries or even `android.R`.
- B is generated when gradle runs, whereas R is continually updated by the IDE. Therefore B will always lag behind R. This is especially problematic when resources are renamed using the IDE. It may however be possible to fix this by creating an IDE plugin (with a file watcher on R) to generate B instead of gradle.
- Although the B class helps a lot with keeping things compile-safe, it is still less safe than using R directly since the compiler will happily accept any string value only to fail after generating the (invalid) binding classes. It will not however carry on to runtime as the generated classes reference R instead of using reflection on it.

Why ButterFork?
---------------

There are a couple of issue threads where this problem was discussed for ButterKnife. In these you can find detailed explanations from Jake Wharton about why it is a bad idea to add this support to the existing library:
- [Issue #2](https://github.com/JakeWharton/butterknife/issues/2) - Add Support For Non-Final IDs
- [Issue #45](https://github.com/JakeWharton/butterknife/issues/45) - Library project issue
- [Issue #100](https://github.com/JakeWharton/butterknife/issues/100) - Injection does not work in library projects
- [Issue #123](https://github.com/JakeWharton/butterknife/issues/123) - take field/method name as default id

License
-------

    Original work Copyright 2013 Jake Wharton
    Modified work Copyright 2015 Oguz Babaoglu

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

 [1]: https://github.com/JakeWharton/butterknife
 [2]: https://img.shields.io/github/release/oguzbabaoglu/butterfork.svg
 [3]: https://github.com/oguzbabaoglu/butterfork/releases
 [4]: https://bitbucket.org/hvisser/android-apt
