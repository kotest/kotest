---
id: soft_assertions
title: Soft Assertions
slug: soft-assertions.html
---


Normally, assertions like `shouldBe` throw an exception when they fail.
But sometimes you want to perform multiple assertions in a test, and
would like to see all of the assertions that failed. Kotest provides
the `assertSoftly` function for this purpose.

```kotlin
assertSoftly {
  foo shouldBe bar
  foo should contain(baz)
}
```

If any assertions inside the block failed, the test will continue to
run. All failures will be reported in a single exception at the end of
the block.

Another version of `assertSoftly` takes a test target and lambda with test target as its receiver.

```kotlin
assertSoftly(foo) {
    shouldNotEndWith("b")
    length shouldBe 3
}
```


We can configure assert softly to be implicitly added to every test via [project config](../framework/project_config.md).
