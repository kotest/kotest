---
id: extension_examples
title: Extension Examples
slug: extension-examples.html
sidebar_label: Examples
---


### System Out Listener

A real example of an extension, is the `NoSystemOutListener` which comes with Kotest.
This extension throws an error if any output is written to standard out.

```kotlin
class MyTestSpec : DescribeSpec({

    listener(NoSystemOutListener)

    describe("All these tests should not write to standard out") {
        it("silence in the court") {
          println("boom") // failure
        }
    }
})
```

### Timer Listener

Another example would be if we wanted to log the time taken for each test case.
We can do this by using the `beforeTest` and `afterTest` functions as follows:

```kotlin
object TimerListener : BeforeTestListener, AfterTestListener {

  var started = 0L

  override fun beforeTest(testCase: TestCase): Unit {
    started = System.currentTimeMillis()
  }

  override fun afterTest(testCase: TestCase, result: TestResult): Unit {
    println("Duration of ${testCase.descriptor} = " + (System.currentTimeMillis() - started))
  }
}
```

Then we can register like so:

```kotlin
class MyTestClass : FunSpec({
  extensions(TimerListener)
  // tests here
})
```

Or we could register it project wide:

```kotlin
object MyConfig : AbstractProjectConfig() {
    override fun extensions(): List<Extension> = listOf(TimerListener)
}
```


