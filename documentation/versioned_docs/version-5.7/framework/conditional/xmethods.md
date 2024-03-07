---
id: xmethods
title: Conditional tests with X Methods
slug: conditional-tests-with-x-methods.html
sidebar_label: X Methods
---


An idea that is popular with Javascript testing frameworks is to allow the test keywords to be prefixed with 'x' to disable the test, and any nested tests.

This is similar to using the bang character in the test name.

For example, with `DescribeSpec` we can replace `describe` with `xdescribe` as in this example:

```kotlin
class XMethodsExample : DescribeSpec({

  xdescribe("this block and it's children are now disabled") {
    it("will not run") {
      // disabled test
    }
  }

})
```


Similarly, we could add the prefix to a nested test:

```kotlin
class XMethodsExample : DescribeSpec({

  describe("this block is enabled") {
    xit("will not run") {
      // disabled test
    }
    it("will run") {
       // enabled test
    }
  }

})
```

See which specs support this, and the syntax required on the [specs styles guide](../styles.md).

