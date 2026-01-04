---
id: xmethods
title: Conditional tests with X Methods
slug: conditional-tests-with-x-methods.html
sidebar_label: X Methods
---


An idea that is popular with Javascript testing frameworks is to allow the test keywords to be prefixed with `x` to
disable those tests, or to be prefixed with `f` to focus only those tests.

This is similar to using the [bang or focus](focus.md) characters in the test name.

Using `DescribeSpec` as an example, we can replace `describe` with `xdescribe` to disable that test:

```kotlin
class XMethodsExample : DescribeSpec({

  xdescribe("this block and it's children are now disabled") {
    it("will not run") {
      // disabled test
    }
  }

})
```

Similarly, we could add the prefix to a nested test by replacing `it` with `xit`:

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

And if we wanted to focus to one or more tests, we can replace `describe` with `fdescribe` or `it` with `fit`:

```kotlin
class XMethodsExample : DescribeSpec({

  fdescribe("this block is focused") {
    // tests
  }
  describe("this block will not run because it is not focused") {
    // tests
  }
})
```

:::info[Why is this helpful?]
If you just want to run a single test, you can of course just run that from intelliJ directly using the green arrow.
However sometimes you want to run a subset of tests, or you want to run all tests except a few. This is when focus and
disabling can be useful.
:::

See which specs support this, and the syntax required on the [specs styles guide](../styles.md).

