# kotest-intellij-plugin
![build-master](https://github.com/kotest/kotest-intellij-plugin/workflows/build-master/badge.svg)

The official [Kotest plugin](https://plugins.jetbrains.com/plugin/11585-kotlintest) for Intellij and Android Studio

## Getting Started

Note: This plugin is currently in a beta and requires the use of the 4.1.0.251-SNAPSHOT build of Kotest as well as the `kotest-runner-console-jvm` module included in your build.

```gradle
  // you need to add the sonatype snapshots repository to your build
  repositories {
    ...
    maven {
      url 'https://oss.sonatype.org/content/repositories/snapshots/'
    }
    ...
  }

  // and then the console runner required by the plugin
  dependencies {
    testImplementation("io.kotest:kotest-runner-console-jvm:4.1.0.251-SNAPSHOT")
  }
```

## Gutter Run Icons

The plugin provides green run icons for specs, top level tests, and nested tests. From this icon you can run, debug, run with coverage, profile and so on.

![gutter_icon_picture](docs/gutter_icons.png)

If you execute a spec then all tests in that spec will be executed. If you execute a test, then that test and all nested tests will be executed.

![gutter_icon_picture](docs/gutter_run.png)

## Tool Window

The plugin provides a tool window view which displays the structure of your tests.

The window describes the currently selected test file, which includes any specs defined in that file, the tests
defined inside those specs, any callbacks (such as before / after test), and any included test factories.

![gutter_icon_picture](docs/test_explorer_tests.png)

Clicking on a spec, test, include or callback will navigate directly to that element in the source editor.

Any tests that have been disabled using the bang prefix will have a different icon.

![gutter_icon_picture](docs/test_window_disabled_tests.png)

You can execute (run/debug/run with coverage) a test or spec directly from this window. In addition, the window shows all test modules and allows you to run all tests in that module.

![gutter_icon_picture](docs/test_explorer_run.png)

Modules, callbacks, and includes can be filtered out if you don't wish to see them. They are included by default.


## Intentions

This plugin has some basic intentions. For example, you can quickly mark a test as disabled.

![gutter_icon_picture](docs/intention_bang.png)

Or you can highlight some text and mark it as should throw, or surround with a soft assertion block.

![gutter_icon_picture](docs/intentions_surround.png)
