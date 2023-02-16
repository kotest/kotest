---
id: test_output
title: Test Output
sidebar_label: Test Output
slug: test_output.html
---

If you are running Kotest via Gradle's Junit Platform support, and if you are using a nested spec style, you
will notice that only the leaf test name is included in output and test reports. This is a limitation of gradle
which is designed around class.method test frameworks.

Until such time that Gradle improves their test integration so that tests can be arbitrarily nested, Kotest
offers a workaround by allowing you to specify `displayFullTestPath` in [project configuration](project_config.md).

When this setting is enabled, the test names will be the concatenation of the entire test path. So a test like this:

```kotlin
class MyTests: DescribeSpec({
  describe("describe 1"){
    it("test 1"){}
    it("test 2"){}
  }
})
```

Will be output as

```
MyTests. describe 1 - test 1
MyTests. describe 1 - test 2
```
