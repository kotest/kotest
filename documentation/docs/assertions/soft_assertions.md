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

**Note:** only Kotest's own assertions can be asserted softly. If any other checks fail and throw an `AssertionError`, it will not respect `assertSoftly` and bubble up, erasing the results of previous assertions. This includes Kotest's own `fail()` function, so when the following code runs, we won't know if the first assertion `foo shouldBe bar` succeeded or failed:

```kotlin
assertSoftly {
  foo shouldBe bar
  fail("Something happened")
}
```

Likewise, if mockk`s `verify(...)` fails in the following example, the second assertion will not execute:

```kotlin
assertSoftly {
  verify(exactly = 1) { myClass.myMethod(any()) }
  foo shouldBe bar
}
```

So if we want to invoke non-kotest assertions inside `assrtSoftly` blocks, they need to be invoked via `shouldPass`.
In the following example both `verify` and the second assertion can fail, and we shall get both errors accumulated:

```kotlin
assertSoftly {
  shouldPass {
    verify(exactly = 1) { myClass.myMethod(any()) }
  }
  foo shouldBe bar
}
```


